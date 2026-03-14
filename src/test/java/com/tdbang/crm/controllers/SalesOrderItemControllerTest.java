/*
 * Copyright © 2026 by tdbang.
 * All rights reserved.
 */

package com.tdbang.crm.controllers;

import java.math.BigDecimal;

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
import com.tdbang.crm.dtos.SalesOrderItemDTO;
import com.tdbang.crm.services.SalesOrderItemService;
import com.tdbang.crm.services.SecurityService;
import com.tdbang.crm.services.UserService;
import com.tdbang.crm.utils.MessageConstants;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
    value = SalesOrderItemController.class,
    excludeAutoConfiguration = {SecurityAutoConfiguration.class, SecurityFilterAutoConfiguration.class}
)
class SalesOrderItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private SalesOrderItemService salesOrderItemService;

    @MockitoBean
    private SecurityService securityService;

    @MockitoBean
    private UserService userService;

    @Test
    void addOrderItem_withValidBody_returnsCreated() throws Exception {
        SalesOrderItemDTO dto = buildOrderItemDTO();
        ResponseDTO response = new ResponseDTO(MessageConstants.SUCCESS_STATUS, MessageConstants.ADDING_ORDER_ITEM_SUCCESS);

        when(securityService.getCurrentUsername()).thenReturn("testuser");
        when(userService.getUserPkByUsername("testuser")).thenReturn(1L);
        when(salesOrderItemService.addOrderItem(anyLong(), any(SalesOrderItemDTO.class), anyLong())).thenReturn(response);

        mockMvc.perform(post("/api/v1/sales-order/1/item")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isCreated());
    }

    @Test
    void addOrderItem_withMissingRequiredField_returnsBadRequest() throws Exception {
        SalesOrderItemDTO dto = new SalesOrderItemDTO();

        mockMvc.perform(post("/api/v1/sales-order/1/item")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void listOrderItems_returnsOk() throws Exception {
        ResponseDTO response = new ResponseDTO(MessageConstants.SUCCESS_STATUS, MessageConstants.FETCHING_ORDER_ITEMS_SUCCESS);
        when(salesOrderItemService.listOrderItems(1L)).thenReturn(response);

        mockMvc.perform(get("/api/v1/sales-order/1/item"))
            .andExpect(status().isOk());
    }

    @Test
    void updateOrderItem_withValidBody_returnsOk() throws Exception {
        SalesOrderItemDTO dto = buildOrderItemDTO();
        ResponseDTO response = new ResponseDTO(MessageConstants.SUCCESS_STATUS, MessageConstants.UPDATING_ORDER_ITEM_SUCCESS);

        when(securityService.getCurrentUsername()).thenReturn("testuser");
        when(userService.getUserPkByUsername("testuser")).thenReturn(1L);
        when(salesOrderItemService.updateOrderItem(anyLong(), any(SalesOrderItemDTO.class), anyLong())).thenReturn(response);

        mockMvc.perform(put("/api/v1/sales-order/1/item/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isOk());
    }

    @Test
    void deleteOrderItem_returnsOk() throws Exception {
        ResponseDTO response = new ResponseDTO(MessageConstants.SUCCESS_STATUS, MessageConstants.DELETING_ORDER_ITEM_SUCCESS);

        when(securityService.getCurrentUsername()).thenReturn("testuser");
        when(userService.getUserPkByUsername("testuser")).thenReturn(1L);
        when(salesOrderItemService.deleteOrderItem(anyLong(), anyLong())).thenReturn(response);

        mockMvc.perform(delete("/api/v1/sales-order/1/item/1"))
            .andExpect(status().isOk());
    }

    private SalesOrderItemDTO buildOrderItemDTO() {
        SalesOrderItemDTO dto = new SalesOrderItemDTO();
        dto.setProductFk(1L);
        dto.setQuantity(3);
        dto.setUnitPrice(new BigDecimal("100.000"));
        dto.setDiscount(new BigDecimal("10.00"));
        return dto;
    }
}
