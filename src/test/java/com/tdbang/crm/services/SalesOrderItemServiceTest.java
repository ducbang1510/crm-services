/*
 * Copyright © 2026 by tdbang.
 * All rights reserved.
 */

package com.tdbang.crm.services;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;

import com.tdbang.crm.dtos.ResponseDTO;
import com.tdbang.crm.dtos.SalesOrderItemDTO;
import com.tdbang.crm.entities.Product;
import com.tdbang.crm.entities.SalesOrder;
import com.tdbang.crm.entities.SalesOrderItem;
import com.tdbang.crm.exceptions.CRMException;
import com.tdbang.crm.mappers.SalesOrderItemMapper;
import com.tdbang.crm.repositories.ProductRepository;
import com.tdbang.crm.repositories.SalesOrderItemRepository;
import com.tdbang.crm.repositories.SalesOrderRepository;
import com.tdbang.crm.utils.MessageConstants;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SalesOrderItemServiceTest {

    @Mock
    private SalesOrderItemRepository salesOrderItemRepository;

    @Mock
    private SalesOrderRepository salesOrderRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private SalesOrderItemMapper salesOrderItemMapper;

    private SalesOrderItemService service;

    @BeforeEach
    void setUp() {
        service = new SalesOrderItemService();
        ReflectionTestUtils.setField(service, "salesOrderItemRepository", salesOrderItemRepository);
        ReflectionTestUtils.setField(service, "salesOrderRepository", salesOrderRepository);
        ReflectionTestUtils.setField(service, "productRepository", productRepository);
        ReflectionTestUtils.setField(service, "salesOrderItemMapper", salesOrderItemMapper);
    }

    @Test
    void addOrderItem_validProduct_calculatesLineTotal() {
        SalesOrder order = buildSalesOrder(1L);
        Product product = buildProduct(1L, new BigDecimal("100.000"), true);
        SalesOrderItemDTO dto = buildItemDTO(1L, 3, new BigDecimal("100.000"), new BigDecimal("10.00"));
        SalesOrderItem savedItem = buildItem(10L, order, product);
        SalesOrderItemDTO resultDto = new SalesOrderItemDTO();
        resultDto.setPk(10L);
        // lineTotal = 100 * 3 * (1 - 10/100) = 270.00
        resultDto.setLineTotal(new BigDecimal("270.00"));

        when(salesOrderRepository.findByPk(1L)).thenReturn(Optional.of(order));
        when(productRepository.findByPk(1L)).thenReturn(Optional.of(product));
        when(salesOrderItemRepository.save(any(SalesOrderItem.class))).thenReturn(savedItem);
        when(salesOrderItemMapper.mappingEntityToDTO(savedItem)).thenReturn(resultDto);

        ResponseDTO result = service.addOrderItem(1L, dto, 1L);

        assertNotNull(result);
        assertEquals(MessageConstants.SUCCESS_STATUS, result.getStatus());
        assertEquals(MessageConstants.ADDING_ORDER_ITEM_SUCCESS, result.getMsg());
    }

    @Test
    void addOrderItem_inactiveProduct_throwsException() {
        SalesOrder order = buildSalesOrder(1L);
        Product product = buildProduct(1L, new BigDecimal("100.000"), false);
        SalesOrderItemDTO dto = buildItemDTO(1L, 1, null, null);

        when(salesOrderRepository.findByPk(1L)).thenReturn(Optional.of(order));
        when(productRepository.findByPk(1L)).thenReturn(Optional.of(product));

        CRMException ex = assertThrows(CRMException.class,
            () -> service.addOrderItem(1L, dto, 1L));

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
    }

    @Test
    void addOrderItem_nullUnitPrice_defaultsToProductPrice() {
        SalesOrder order = buildSalesOrder(1L);
        Product product = buildProduct(1L, new BigDecimal("250.500"), true);
        SalesOrderItemDTO dto = buildItemDTO(1L, 2, null, null);
        SalesOrderItem savedItem = buildItem(10L, order, product);
        SalesOrderItemDTO resultDto = new SalesOrderItemDTO();
        resultDto.setPk(10L);

        when(salesOrderRepository.findByPk(1L)).thenReturn(Optional.of(order));
        when(productRepository.findByPk(1L)).thenReturn(Optional.of(product));
        when(salesOrderItemRepository.save(any(SalesOrderItem.class))).thenAnswer(invocation -> {
            SalesOrderItem item = invocation.getArgument(0);
            // Verify unitPrice defaults to product price
            assertEquals(new BigDecimal("250.500"), item.getUnitPrice());
            return savedItem;
        });
        when(salesOrderItemMapper.mappingEntityToDTO(savedItem)).thenReturn(resultDto);

        ResponseDTO result = service.addOrderItem(1L, dto, 1L);

        assertNotNull(result);
        assertEquals(MessageConstants.SUCCESS_STATUS, result.getStatus());
    }

    @Test
    void updateOrderItem_recalculatesLineTotal() {
        SalesOrder order = buildSalesOrder(1L);
        Product product = buildProduct(1L, new BigDecimal("100.000"), true);
        SalesOrderItem existingItem = buildItem(10L, order, product);
        existingItem.setUnitPrice(new BigDecimal("100.000"));
        existingItem.setQuantity(2);
        existingItem.setDiscount(BigDecimal.ZERO);

        SalesOrderItemDTO dto = new SalesOrderItemDTO();
        dto.setQuantity(5);
        dto.setDiscount(new BigDecimal("20.00"));

        SalesOrderItemDTO resultDto = new SalesOrderItemDTO();
        resultDto.setPk(10L);

        when(salesOrderItemRepository.findById(10L)).thenReturn(Optional.of(existingItem));
        when(salesOrderItemRepository.save(any(SalesOrderItem.class))).thenReturn(existingItem);
        when(salesOrderItemMapper.mappingEntityToDTO(existingItem)).thenReturn(resultDto);

        ResponseDTO result = service.updateOrderItem(10L, dto, 1L);

        assertNotNull(result);
        assertEquals(MessageConstants.SUCCESS_STATUS, result.getStatus());
        assertEquals(MessageConstants.UPDATING_ORDER_ITEM_SUCCESS, result.getMsg());
    }

    @Test
    void listOrderItems_returnsSortedByOrder() {
        SalesOrder order = buildSalesOrder(1L);
        Product product = buildProduct(1L, new BigDecimal("50.000"), true);
        SalesOrderItem item1 = buildItem(1L, order, product);
        item1.setLineTotal(new BigDecimal("50.00"));
        SalesOrderItem item2 = buildItem(2L, order, product);
        item2.setLineTotal(new BigDecimal("100.00"));
        List<SalesOrderItem> items = List.of(item1, item2);

        SalesOrderItemDTO dto1 = new SalesOrderItemDTO();
        dto1.setPk(1L);
        SalesOrderItemDTO dto2 = new SalesOrderItemDTO();
        dto2.setPk(2L);

        when(salesOrderRepository.findByPk(1L)).thenReturn(Optional.of(order));
        when(salesOrderItemRepository.findBySalesOrderPkOrderBySortOrderAsc(1L)).thenReturn(items);
        when(salesOrderItemMapper.mappingToListDTO(items)).thenReturn(List.of(dto1, dto2));

        ResponseDTO result = service.listOrderItems(1L);

        assertNotNull(result);
        assertEquals(MessageConstants.SUCCESS_STATUS, result.getStatus());
        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) result.getData();
        assertEquals(new BigDecimal("150.00"), data.get("itemsTotal"));
    }

    @Test
    void deleteOrderItem_removesFromDb() {
        SalesOrder order = buildSalesOrder(1L);
        Product product = buildProduct(1L, new BigDecimal("50.000"), true);
        SalesOrderItem item = buildItem(10L, order, product);

        when(salesOrderItemRepository.findById(10L)).thenReturn(Optional.of(item));

        ResponseDTO result = service.deleteOrderItem(10L, 1L);

        assertEquals(MessageConstants.SUCCESS_STATUS, result.getStatus());
        assertEquals(MessageConstants.DELETING_ORDER_ITEM_SUCCESS, result.getMsg());
        verify(salesOrderItemRepository, times(1)).delete(item);
    }

    // --- Helper methods ---

    private SalesOrder buildSalesOrder(Long pk) {
        SalesOrder order = new SalesOrder();
        order.setPk(pk);
        return order;
    }

    private Product buildProduct(Long pk, BigDecimal price, boolean active) {
        Product product = new Product();
        product.setPk(pk);
        product.setName("Test Product");
        product.setPrice(price);
        product.setIsActive(active);
        return product;
    }

    private SalesOrderItemDTO buildItemDTO(Long productFk, Integer quantity, BigDecimal unitPrice, BigDecimal discount) {
        SalesOrderItemDTO dto = new SalesOrderItemDTO();
        dto.setProductFk(productFk);
        dto.setQuantity(quantity);
        dto.setUnitPrice(unitPrice);
        dto.setDiscount(discount);
        return dto;
    }

    private SalesOrderItem buildItem(Long pk, SalesOrder order, Product product) {
        SalesOrderItem item = new SalesOrderItem();
        item.setPk(pk);
        item.setSalesOrder(order);
        item.setProduct(product);
        item.setQuantity(1);
        item.setUnitPrice(product.getPrice());
        item.setDiscount(BigDecimal.ZERO);
        item.setLineTotal(product.getPrice());
        item.setSortOrder(0);
        return item;
    }
}
