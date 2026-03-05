/*
 * Copyright © 2025 by tdbang.
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;

import com.tdbang.crm.dtos.DashboardDTO;
import com.tdbang.crm.dtos.ResponseDTO;
import com.tdbang.crm.dtos.SalesOrderDTO;
import com.tdbang.crm.dtos.nativequerydto.DashboardQueryDTO;
import com.tdbang.crm.dtos.nativequerydto.SalesOrderQueryDTO;
import com.tdbang.crm.entities.Contact;
import com.tdbang.crm.entities.SalesOrder;
import com.tdbang.crm.entities.User;
import com.tdbang.crm.enums.SalesOrderStatus;
import com.tdbang.crm.exceptions.CRMException;
import com.tdbang.crm.mappers.SalesOrderMapper;
import com.tdbang.crm.repositories.ContactRepository;
import com.tdbang.crm.repositories.SalesOrderRepository;
import com.tdbang.crm.repositories.UserRepository;
import com.tdbang.crm.repositories.custom.CustomRepository;
import com.tdbang.crm.specifications.SpecificationFilterUtil;
import com.tdbang.crm.utils.MessageConstants;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SalesOrderServiceTest {

    @Mock
    private SalesOrderRepository salesOrderRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ContactRepository contactRepository;

    @Mock
    private SalesOrderMapper salesOrderMapper;

    @Mock
    private SpecificationFilterUtil<SalesOrder> filterUtil;

    @Mock
    private CustomRepository<SalesOrder> customRepository;

    private SalesOrderService salesOrderService;

    @BeforeEach
    void setUp() {
        salesOrderService = new SalesOrderService(filterUtil, customRepository);
        ReflectionTestUtils.setField(salesOrderService, "salesOrderRepository", salesOrderRepository);
        ReflectionTestUtils.setField(salesOrderService, "userRepository", userRepository);
        ReflectionTestUtils.setField(salesOrderService, "contactRepository", contactRepository);
        ReflectionTestUtils.setField(salesOrderService, "salesOrderMapper", salesOrderMapper);
    }

    @Test
    void getSalesOrderDetails_withValidId_returnsSuccess() {
        SalesOrderQueryDTO dto = mock(SalesOrderQueryDTO.class);
        SalesOrderDTO salesOrderDTO = buildSalesOrderDTO();

        when(salesOrderRepository.getSalesOrderDetailsByPk(1L)).thenReturn(dto);
        when(salesOrderMapper.mappingSalesOrderQueryDTOToSalesOrderDTO(dto)).thenReturn(salesOrderDTO);

        ResponseDTO result = salesOrderService.getSalesOrderDetails(1L);

        assertNotNull(result);
        assertEquals(MessageConstants.SUCCESS_STATUS, result.getStatus());
        assertEquals(MessageConstants.FETCHING_SALES_ORDER_SUCCESS, result.getMsg());
    }

    @Test
    void getSalesOrderDetails_withNullId_returnsEmptyResponseWithoutCallingRepo() {
        ResponseDTO result = salesOrderService.getSalesOrderDetails(null);

        assertNotNull(result);
        verify(salesOrderRepository, times(0)).getSalesOrderDetailsByPk(anyLong());
    }

    @Test
    void createNewSalesOrder_withAssignedToUserFkAndContactFk_returnsSuccess() {
        Long creatorFk = 1L;
        SalesOrderDTO dto = buildSalesOrderDTO();
        dto.setAssignedToUserFk(2L);
        dto.setContactFk(3L);

        User creator = buildUser(1L, "Creator");
        User assigned = buildUser(2L, "AssignedUser");
        Contact contact = buildContact(3L);

        when(userRepository.findUserByPk(creatorFk)).thenReturn(creator);
        when(userRepository.findUserByPk(2L)).thenReturn(assigned);
        when(contactRepository.findByPk(3L)).thenReturn(Optional.of(contact));
        when(salesOrderRepository.save(any(SalesOrder.class))).thenReturn(new SalesOrder());

        ResponseDTO result = salesOrderService.createNewSalesOrder(dto, creatorFk);

        assertNotNull(result);
        assertEquals(MessageConstants.SUCCESS_STATUS, result.getStatus());
        assertEquals(MessageConstants.CREATING_NEW_SALES_ORDER_SUCCESS, result.getMsg());
    }

    @Test
    void createNewSalesOrder_withRepositoryException_throwsCRMException() {
        Long creatorFk = 1L;
        SalesOrderDTO dto = buildSalesOrderDTO();
        dto.setAssignedToUserFk(2L);
        dto.setContactFk(3L);

        User creator = buildUser(1L, "Creator");
        User assigned = buildUser(2L, "AssignedUser");
        Contact contact = buildContact(3L);

        when(userRepository.findUserByPk(creatorFk)).thenReturn(creator);
        when(userRepository.findUserByPk(2L)).thenReturn(assigned);
        when(contactRepository.findByPk(3L)).thenReturn(Optional.of(contact));
        when(salesOrderRepository.save(any(SalesOrder.class))).thenThrow(new RuntimeException("DB error"));

        CRMException ex = assertThrows(CRMException.class,
            () -> salesOrderService.createNewSalesOrder(dto, creatorFk));

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
    }

    @Test
    void updateSalesOrderDetails_withSameCreator_returnsSuccess() {
        Long orderPk = 1L;
        Long creatorFk = 1L;
        SalesOrderDTO dto = buildSalesOrderDTO();
        dto.setAssignedToUserFk(2L);
        dto.setContactFk(3L);

        User creator = buildUser(1L, "Creator");
        User assigned = buildUser(2L, "AssignedUser");
        Contact contact = buildContact(3L);
        SalesOrder existing = buildSalesOrder(orderPk, creator);

        when(salesOrderRepository.findByPk(orderPk)).thenReturn(Optional.of(existing));
        when(userRepository.findUserByPk(2L)).thenReturn(assigned);
        when(contactRepository.findByPk(3L)).thenReturn(Optional.of(contact));

        ResponseDTO result = salesOrderService.updateSalesOrderDetails(orderPk, creatorFk, dto);

        assertNotNull(result);
        assertEquals(MessageConstants.SUCCESS_STATUS, result.getStatus());
    }

    @Test
    void updateSalesOrderDetails_withDifferentCreator_throwsForbidden() {
        Long orderPk = 1L;
        User owner = buildUser(1L, "Owner");
        SalesOrder existing = buildSalesOrder(orderPk, owner);

        when(salesOrderRepository.findByPk(orderPk)).thenReturn(Optional.of(existing));

        CRMException ex = assertThrows(CRMException.class,
            () -> salesOrderService.updateSalesOrderDetails(orderPk, 99L, new SalesOrderDTO()));

        assertEquals(HttpStatus.FORBIDDEN, ex.getStatus());
    }

    @Test
    void updateSalesOrderDetails_withNotFoundOrder_throwsNotFound() {
        when(salesOrderRepository.findByPk(anyLong())).thenReturn(Optional.empty());

        CRMException ex = assertThrows(CRMException.class,
            () -> salesOrderService.updateSalesOrderDetails(1L, 1L, new SalesOrderDTO()));

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
    }

    @Test
    void deleteSalesOrderDetails_withSameCreator_returnsSuccess() {
        User creator = buildUser(1L, "Creator");
        SalesOrder order = buildSalesOrder(1L, creator);

        when(salesOrderRepository.findByPk(1L)).thenReturn(Optional.of(order));

        ResponseDTO result = salesOrderService.deleteSalesOrderDetails(1L, 1L);

        assertEquals(MessageConstants.SUCCESS_STATUS, result.getStatus());
        assertEquals(MessageConstants.DELETING_SALES_ORDER_SUCCESS, result.getMsg());
        verify(salesOrderRepository, times(1)).delete(order);
    }

    @Test
    void deleteSalesOrderDetails_withDifferentCreator_throwsForbidden() {
        User owner = buildUser(1L, "Owner");
        SalesOrder order = buildSalesOrder(1L, owner);

        when(salesOrderRepository.findByPk(anyLong())).thenReturn(Optional.of(order));

        CRMException ex = assertThrows(CRMException.class,
            () -> salesOrderService.deleteSalesOrderDetails(1L, 99L));

        assertEquals(HttpStatus.FORBIDDEN, ex.getStatus());
    }

    @Test
    void deleteSalesOrderDetails_withNotFoundOrder_throwsNotFound() {
        when(salesOrderRepository.findByPk(anyLong())).thenReturn(Optional.empty());

        CRMException ex = assertThrows(CRMException.class,
            () -> salesOrderService.deleteSalesOrderDetails(1L, 1L));

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
    }

    @Test
    void deleteSaleOrders_withAllOwnOrders_returnsSuccess() {
        Long creatorFk = 1L;
        List<Long> ids = List.of(1L, 2L);
        User creator = buildUser(1L, "Creator");
        SalesOrder o1 = buildSalesOrder(1L, creator);
        SalesOrder o2 = buildSalesOrder(2L, creator);

        when(salesOrderRepository.getSaleOrdersByOrderPks(ids)).thenReturn(List.of(o1, o2));

        ResponseDTO result = salesOrderService.deleteSaleOrders(ids, creatorFk);

        assertEquals(MessageConstants.SUCCESS_STATUS, result.getStatus());
        assertEquals(MessageConstants.DELETING_LIST_OF_SALES_ORDERS_SUCCESS, result.getMsg());
    }

    @Test
    void deleteSaleOrders_withOtherCreatorOrder_throwsForbidden() {
        Long creatorFk = 1L;
        List<Long> ids = List.of(1L, 2L);
        User creator = buildUser(1L, "Creator");
        User other = buildUser(2L, "Other");
        SalesOrder o1 = buildSalesOrder(1L, creator);
        SalesOrder o2 = buildSalesOrder(2L, other);

        when(salesOrderRepository.getSaleOrdersByOrderPks(ids)).thenReturn(List.of(o1, o2));

        CRMException ex = assertThrows(CRMException.class,
            () -> salesOrderService.deleteSaleOrders(ids, creatorFk));

        assertEquals(HttpStatus.FORBIDDEN, ex.getStatus());
    }

    @Test
    void retrieveStatusEnumOfSalesOrder_returnsAllValues() {
        List<SalesOrderStatus> result = salesOrderService.retrieveStatusEnumOfSalesOrder();

        assertNotNull(result);
        assertEquals(SalesOrderStatus.values().length, result.size());
    }

    @Test
    void retrieveOrderDashboardByStatus_returnsGroupedData() {
        DashboardQueryDTO dto = buildDashboardQueryDTO(0, 3L);
        when(salesOrderRepository.countOrderGroupByStatus()).thenReturn(List.of(dto));

        ResponseDTO result = salesOrderService.retrieveOrderDashboardByStatus();

        assertNotNull(result);
        assertEquals(MessageConstants.SUCCESS_STATUS, result.getStatus());
        @SuppressWarnings("unchecked")
        List<DashboardDTO> data = (List<DashboardDTO>) result.getData();
        assertEquals(1, data.size());
        assertEquals(SalesOrderStatus.values()[0].getName(), data.get(0).getId());
        assertEquals(3L, data.get(0).getCount());
    }

    @Test
    void getListOfOrder_withNoPageSize_returnsAllOrders() {
        SalesOrder order = buildSalesOrder(1L, buildUser(1L, "Creator"));
        SalesOrderDTO salesOrderDTO = buildSalesOrderDTO();

        when(customRepository.findAll(any(Sort.class), any())).thenReturn(List.of());
        when(salesOrderMapper.mapRecordList(any())).thenReturn(List.of(order));
        when(salesOrderMapper.mappingSalesOrderEntityToSalesOrderDTO(order)).thenReturn(salesOrderDTO);

        ResponseDTO result = salesOrderService.getListOfOrder(null, 0, 0, "pk", "ASC", null);

        assertNotNull(result);
        assertEquals(MessageConstants.SUCCESS_STATUS, result.getStatus());
        assertEquals(MessageConstants.FETCHING_LIST_OF_SALES_ORDER_SUCCESS, result.getMsg());
    }

    @Test
    void getListOfOrder_withPageSize_returnsPagedOrders() {
        Page<Map<String, Object>> page = new PageImpl<>(List.of(), PageRequest.of(0, 10), 0);

        when(customRepository.findAll(any(Pageable.class), any())).thenReturn(page);
        when(salesOrderMapper.mapRecordList(any())).thenReturn(List.of());

        ResponseDTO result = salesOrderService.getListOfOrder(null, 10, 0, "pk", "ASC", null);

        assertNotNull(result);
        assertEquals(MessageConstants.SUCCESS_STATUS, result.getStatus());
        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) result.getData();
        assertNotNull(data.get("totalRecord"));
    }

    @Test
    void retrieveOrderListWithNonDynamicFilter_withPagination_returnsPagedResult() {
        SalesOrderQueryDTO dto = mock(SalesOrderQueryDTO.class);
        SalesOrderDTO salesOrderDTO = buildSalesOrderDTO();
        Page<SalesOrderQueryDTO> page = new PageImpl<>(List.of(dto));

        when(salesOrderRepository.getSalesOrderPageable(anyString(), any(Pageable.class))).thenReturn(page);
        when(salesOrderMapper.mappingToListSalesOrderDTO(any())).thenReturn(List.of(salesOrderDTO));

        ResponseDTO result = salesOrderService.retrieveOrderListWithNonDynamicFilter(0, 10, "Order");

        assertNotNull(result);
        assertEquals(MessageConstants.SUCCESS_STATUS, result.getStatus());
        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) result.getData();
        assertNotNull(data.get("totalRecord"));
    }

    @Test
    void retrieveOrderListWithNonDynamicFilter_withoutPagination_returnsAllOrders() {
        SalesOrderQueryDTO dto = mock(SalesOrderQueryDTO.class);
        SalesOrderDTO salesOrderDTO = buildSalesOrderDTO();

        when(salesOrderRepository.getAllSalesOrder("Order")).thenReturn(List.of(dto));
        when(salesOrderMapper.mappingToListSalesOrderDTO(any())).thenReturn(List.of(salesOrderDTO));

        ResponseDTO result = salesOrderService.retrieveOrderListWithNonDynamicFilter(null, null, "Order");

        assertNotNull(result);
        assertEquals(MessageConstants.SUCCESS_STATUS, result.getStatus());
        assertNotNull(result.getData());
    }

    // --- Helper methods ---

    private SalesOrderDTO buildSalesOrderDTO() {
        SalesOrderDTO dto = new SalesOrderDTO();
        dto.setSubject("Order Subject");
        dto.setContactName("John Doe");
        dto.setStatus("Created");
        dto.setTotal(BigDecimal.valueOf(500.00));
        dto.setAssignedTo("AssignedUser");
        return dto;
    }

    private User buildUser(Long pk, String name) {
        User user = new User();
        user.setPk(pk);
        user.setName(name);
        return user;
    }

    private Contact buildContact(Long pk) {
        Contact contact = new Contact();
        contact.setPk(pk);
        contact.setContactName("John Doe");
        return contact;
    }

    private SalesOrder buildSalesOrder(Long pk, User creator) {
        SalesOrder order = new SalesOrder();
        order.setPk(pk);
        order.setCreator(creator);
        return order;
    }

    private DashboardQueryDTO buildDashboardQueryDTO(int id, long count) {
        return new DashboardQueryDTO() {
            @Override
            public Integer getId() {
                return id;
            }

            @Override
            public Long getCount() {
                return count;
            }
        };
    }
}
