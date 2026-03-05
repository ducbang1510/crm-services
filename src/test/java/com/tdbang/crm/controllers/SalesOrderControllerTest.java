/*
 * Copyright © 2025 by tdbang.
 * All rights reserved.
 */

package com.tdbang.crm.controllers;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.tdbang.crm.dtos.ResponseDTO;
import com.tdbang.crm.dtos.SalesOrderDTO;
import com.tdbang.crm.enums.SalesOrderStatus;
import com.tdbang.crm.services.SalesOrderService;
import com.tdbang.crm.services.SecurityService;
import com.tdbang.crm.services.UserService;
import com.tdbang.crm.utils.MessageConstants;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
    value = SalesOderController.class,
    excludeAutoConfiguration = {SecurityAutoConfiguration.class, SecurityFilterAutoConfiguration.class}
)
class SalesOrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private SalesOrderService salesOrderService;

    @MockitoBean
    private SecurityService securityService;

    @MockitoBean
    private UserService userService;

    @Test
    void createSalesOrder_withValidBody_returnsCreated() throws Exception {
        SalesOrderDTO dto = buildSalesOrderDTO();
        ResponseDTO response = new ResponseDTO(MessageConstants.SUCCESS_STATUS, MessageConstants.CREATING_NEW_SALES_ORDER_SUCCESS);

        when(securityService.getCurrentUsername()).thenReturn("testuser");
        when(userService.getUserPkByUsername("testuser")).thenReturn(1L);
        when(salesOrderService.createNewSalesOrder(any(SalesOrderDTO.class), anyLong())).thenReturn(response);

        mockMvc.perform(post("/api/v1/sales-order")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isCreated());
    }

    @Test
    void createSalesOrder_withMissingRequiredField_returnsBadRequest() throws Exception {
        SalesOrderDTO dto = new SalesOrderDTO();

        mockMvc.perform(post("/api/v1/sales-order")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void retrieveOrderDetails_withValidId_returnsOk() throws Exception {
        ResponseDTO response = new ResponseDTO(MessageConstants.SUCCESS_STATUS, MessageConstants.FETCHING_SALES_ORDER_SUCCESS);
        when(salesOrderService.getSalesOrderDetails(1L)).thenReturn(response);

        mockMvc.perform(get("/api/v1/sales-order/1"))
            .andExpect(status().isOk());
    }

    @Test
    void updateOrderDetails_withValidBody_returnsOk() throws Exception {
        SalesOrderDTO dto = buildSalesOrderDTO();
        ResponseDTO response = new ResponseDTO(MessageConstants.SUCCESS_STATUS, MessageConstants.UPDATING_SALES_ORDER_SUCCESS);

        when(securityService.getCurrentUsername()).thenReturn("testuser");
        when(userService.getUserPkByUsername("testuser")).thenReturn(1L);
        when(salesOrderService.updateSalesOrderDetails(anyLong(), anyLong(), any(SalesOrderDTO.class))).thenReturn(response);

        mockMvc.perform(put("/api/v1/sales-order/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isOk());
    }

    @Test
    void deleteOrderDetails_returnsOk() throws Exception {
        ResponseDTO response = new ResponseDTO(MessageConstants.SUCCESS_STATUS, MessageConstants.DELETING_SALES_ORDER_SUCCESS);

        when(securityService.getCurrentUsername()).thenReturn("testuser");
        when(userService.getUserPkByUsername("testuser")).thenReturn(1L);
        when(salesOrderService.deleteSalesOrderDetails(anyLong(), anyLong())).thenReturn(response);

        mockMvc.perform(delete("/api/v1/sales-order/1"))
            .andExpect(status().isOk());
    }

    @Test
    void retrieveOrderList_returnsOk() throws Exception {
        ResponseDTO response = new ResponseDTO(MessageConstants.SUCCESS_STATUS, MessageConstants.FETCHING_LIST_OF_SALES_ORDER_SUCCESS);
        when(salesOrderService.getListOfOrder(isNull(), anyInt(), anyInt(), anyString(), anyString(), isNull()))
            .thenReturn(response);

        mockMvc.perform(get("/api/v1/sales-order/list"))
            .andExpect(status().isOk());
    }

    @Test
    void deleteOrders_returnsOk() throws Exception {
        List<Long> ids = List.of(1L, 2L);
        ResponseDTO response = new ResponseDTO(MessageConstants.SUCCESS_STATUS, MessageConstants.DELETING_LIST_OF_SALES_ORDERS_SUCCESS);

        when(securityService.getCurrentUsername()).thenReturn("testuser");
        when(userService.getUserPkByUsername("testuser")).thenReturn(1L);
        when(salesOrderService.deleteSaleOrders(anyList(), anyLong())).thenReturn(response);

        mockMvc.perform(post("/api/v1/sales-order/delete")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ids)))
            .andExpect(status().isOk());
    }

    @Test
    void retrieveStatusEnum_returnsOk() throws Exception {
        when(salesOrderService.retrieveStatusEnumOfSalesOrder())
            .thenReturn(List.of(SalesOrderStatus.values()));

        mockMvc.perform(get("/api/v1/sales-order/status"))
            .andExpect(status().isOk());
    }

    @Test
    void retrieveOrderDashboardByStatus_returnsOk() throws Exception {
        ResponseDTO response = new ResponseDTO(MessageConstants.SUCCESS_STATUS, MessageConstants.COUNTING_NO_SALES_ORDERS_BY_STATUS_SUCCESS);
        when(salesOrderService.retrieveOrderDashboardByStatus()).thenReturn(response);

        mockMvc.perform(get("/api/v1/sales-order/count/status"))
            .andExpect(status().isOk());
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
}
