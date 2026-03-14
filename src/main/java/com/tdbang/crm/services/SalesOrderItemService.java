/*
 * Copyright © 2026 by tdbang.
 * All rights reserved.
 */

package com.tdbang.crm.services;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.transaction.Transactional;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

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

@Log4j2
@Service
public class SalesOrderItemService {

    @Autowired
    private SalesOrderItemRepository salesOrderItemRepository;
    @Autowired
    private SalesOrderRepository salesOrderRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private SalesOrderItemMapper salesOrderItemMapper;

    public ResponseDTO listOrderItems(Long salesOrderPk) {
        try {
            salesOrderRepository.findByPk(salesOrderPk)
                .orElseThrow(() -> new CRMException(HttpStatus.NOT_FOUND, MessageConstants.NOT_FOUND_CODE, MessageConstants.NOT_FOUND_MESSAGE));

            List<SalesOrderItem> items = salesOrderItemRepository.findBySalesOrderPkOrderBySortOrderAsc(salesOrderPk);
            List<SalesOrderItemDTO> itemDTOs = salesOrderItemMapper.mappingToListDTO(items);

            BigDecimal itemsTotal = items.stream()
                .map(SalesOrderItem::getLineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("items", itemDTOs);
            resultMap.put("itemsTotal", itemsTotal);

            return new ResponseDTO(MessageConstants.SUCCESS_STATUS, MessageConstants.FETCHING_ORDER_ITEMS_SUCCESS, resultMap);
        } catch (CRMException e) {
            throw e;
        } catch (Exception e) {
            throw new CRMException(HttpStatus.INTERNAL_SERVER_ERROR,
                MessageConstants.INTERNAL_ERROR_CODE, MessageConstants.FETCHING_ORDER_ITEMS_ERROR, e.getMessage());
        }
    }

    @Transactional
    public ResponseDTO addOrderItem(Long salesOrderPk, SalesOrderItemDTO dto, Long userPk) {
        try {
            SalesOrder salesOrder = salesOrderRepository.findByPk(salesOrderPk)
                .orElseThrow(() -> new CRMException(HttpStatus.NOT_FOUND, MessageConstants.NOT_FOUND_CODE, MessageConstants.NOT_FOUND_MESSAGE));

            Product product = productRepository.findByPk(dto.getProductFk())
                .orElseThrow(() -> new CRMException(HttpStatus.NOT_FOUND, MessageConstants.NOT_FOUND_CODE, "Product not found"));

            if (!Boolean.TRUE.equals(product.getIsActive())) {
                throw new CRMException(HttpStatus.BAD_REQUEST, MessageConstants.BAD_REQUEST_CODE, "Product is not active");
            }

            BigDecimal unitPrice = dto.getUnitPrice() != null ? dto.getUnitPrice() : product.getPrice();
            BigDecimal discount = dto.getDiscount() != null ? dto.getDiscount() : BigDecimal.ZERO;
            BigDecimal lineTotal = calculateLineTotal(unitPrice, dto.getQuantity(), discount);

            SalesOrderItem item = new SalesOrderItem();
            item.setSalesOrder(salesOrder);
            item.setProduct(product);
            item.setQuantity(dto.getQuantity());
            item.setUnitPrice(unitPrice);
            item.setDiscount(discount);
            item.setLineTotal(lineTotal);
            item.setSortOrder(dto.getSortOrder() != null ? dto.getSortOrder() : 0);

            SalesOrderItem savedItem = salesOrderItemRepository.save(item);
            SalesOrderItemDTO resultDto = salesOrderItemMapper.mappingEntityToDTO(savedItem);

            return new ResponseDTO(MessageConstants.SUCCESS_STATUS, MessageConstants.ADDING_ORDER_ITEM_SUCCESS, resultDto);
        } catch (CRMException e) {
            throw e;
        } catch (Exception e) {
            throw new CRMException(HttpStatus.BAD_REQUEST, MessageConstants.BAD_REQUEST_CODE, MessageConstants.ADDING_ORDER_ITEM_ERROR, e.getMessage());
        }
    }

    @Transactional
    public ResponseDTO updateOrderItem(Long itemPk, SalesOrderItemDTO dto, Long userPk) {
        SalesOrderItem item = salesOrderItemRepository.findById(itemPk)
            .orElseThrow(() -> new CRMException(HttpStatus.NOT_FOUND, MessageConstants.NOT_FOUND_CODE, MessageConstants.NOT_FOUND_MESSAGE));

        try {
            if (dto.getProductFk() != null) {
                Product product = productRepository.findByPk(dto.getProductFk())
                    .orElseThrow(() -> new CRMException(HttpStatus.NOT_FOUND, MessageConstants.NOT_FOUND_CODE, "Product not found"));
                item.setProduct(product);
            }
            if (dto.getQuantity() != null) {
                item.setQuantity(dto.getQuantity());
            }
            if (dto.getUnitPrice() != null) {
                item.setUnitPrice(dto.getUnitPrice());
            }
            if (dto.getDiscount() != null) {
                item.setDiscount(dto.getDiscount());
            }
            if (dto.getSortOrder() != null) {
                item.setSortOrder(dto.getSortOrder());
            }

            item.setLineTotal(calculateLineTotal(item.getUnitPrice(), item.getQuantity(), item.getDiscount()));

            SalesOrderItem updatedItem = salesOrderItemRepository.save(item);
            SalesOrderItemDTO resultDto = salesOrderItemMapper.mappingEntityToDTO(updatedItem);

            return new ResponseDTO(MessageConstants.SUCCESS_STATUS, MessageConstants.UPDATING_ORDER_ITEM_SUCCESS, resultDto);
        } catch (CRMException e) {
            throw e;
        } catch (Exception e) {
            throw new CRMException(HttpStatus.BAD_REQUEST, MessageConstants.BAD_REQUEST_CODE, MessageConstants.UPDATING_ORDER_ITEM_ERROR, e.getMessage());
        }
    }

    @Transactional
    public ResponseDTO deleteOrderItem(Long itemPk, Long userPk) {
        SalesOrderItem item = salesOrderItemRepository.findById(itemPk)
            .orElseThrow(() -> new CRMException(HttpStatus.NOT_FOUND, MessageConstants.NOT_FOUND_CODE, MessageConstants.NOT_FOUND_MESSAGE));

        salesOrderItemRepository.delete(item);
        return new ResponseDTO(MessageConstants.SUCCESS_STATUS, MessageConstants.DELETING_ORDER_ITEM_SUCCESS);
    }

    private BigDecimal calculateLineTotal(BigDecimal unitPrice, Integer quantity, BigDecimal discount) {
        BigDecimal subtotal = unitPrice.multiply(BigDecimal.valueOf(quantity));
        BigDecimal discountFactor = BigDecimal.ONE.subtract(discount.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP));
        return subtotal.multiply(discountFactor).setScale(2, RoundingMode.HALF_UP);
    }
}
