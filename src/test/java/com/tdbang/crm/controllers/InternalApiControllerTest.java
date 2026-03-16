/*
 * Copyright © 2026 by tdbang.
 * All rights reserved.
 */

package com.tdbang.crm.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.tdbang.crm.config.ApiKeyAuthFilter;
import com.tdbang.crm.dtos.ResponseDTO;
import com.tdbang.crm.services.ContactService;
import com.tdbang.crm.services.DashboardService;
import com.tdbang.crm.services.NoteService;
import com.tdbang.crm.services.ProductService;
import com.tdbang.crm.services.ReportService;
import com.tdbang.crm.services.SalesOrderItemService;
import com.tdbang.crm.services.SalesOrderService;
import com.tdbang.crm.services.TaskService;
import com.tdbang.crm.utils.MessageConstants;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
    value = InternalApiController.class,
    excludeAutoConfiguration = {SecurityAutoConfiguration.class, SecurityFilterAutoConfiguration.class},
    excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = ApiKeyAuthFilter.class)
)
class InternalApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DashboardService dashboardService;

    @MockitoBean
    private ReportService reportService;

    @MockitoBean
    private TaskService taskService;

    @MockitoBean
    private ContactService contactService;

    @MockitoBean
    private SalesOrderService salesOrderService;

    @MockitoBean
    private SalesOrderItemService salesOrderItemService;

    @MockitoBean
    private NoteService noteService;

    @MockitoBean
    private ProductService productService;

    // Dashboard

    @Test
    void getPipelineSummary_returnsOk() throws Exception {
        ResponseDTO response = new ResponseDTO(MessageConstants.SUCCESS_STATUS, MessageConstants.FETCHING_PIPELINE_SUMMARY_SUCCESS);
        when(dashboardService.getPipelineSummary()).thenReturn(response);

        mockMvc.perform(get("/internal/v1/dashboard/pipeline-summary"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value(MessageConstants.SUCCESS_STATUS));
    }

    @Test
    void getRevenueTrend_returnsOk() throws Exception {
        ResponseDTO response = new ResponseDTO(MessageConstants.SUCCESS_STATUS, MessageConstants.FETCHING_REVENUE_TREND_SUCCESS);
        when(dashboardService.getRevenueTrend(anyInt())).thenReturn(response);

        mockMvc.perform(get("/internal/v1/dashboard/revenue-trend"))
            .andExpect(status().isOk());
    }

    @Test
    void getRevenueTrend_withCustomMonths_returnsOk() throws Exception {
        ResponseDTO response = new ResponseDTO(MessageConstants.SUCCESS_STATUS, MessageConstants.FETCHING_REVENUE_TREND_SUCCESS);
        when(dashboardService.getRevenueTrend(6)).thenReturn(response);

        mockMvc.perform(get("/internal/v1/dashboard/revenue-trend")
                .param("months", "6"))
            .andExpect(status().isOk());
    }

    @Test
    void getTopUsers_returnsOk() throws Exception {
        ResponseDTO response = new ResponseDTO(MessageConstants.SUCCESS_STATUS, MessageConstants.FETCHING_TOP_USERS_SUCCESS);
        when(dashboardService.getTopUsers(anyInt())).thenReturn(response);

        mockMvc.perform(get("/internal/v1/dashboard/top-users"))
            .andExpect(status().isOk());
    }

    // Tasks

    @Test
    void getTaskSummary_returnsOk() throws Exception {
        ResponseDTO response = new ResponseDTO(MessageConstants.SUCCESS_STATUS, MessageConstants.FETCHING_TASK_SUMMARY_SUCCESS);
        when(taskService.getTaskSummary(anyLong())).thenReturn(response);

        mockMvc.perform(get("/internal/v1/tasks/summary")
                .param("userPk", "1"))
            .andExpect(status().isOk());
    }

    // Contacts

    @Test
    void searchContacts_returnsOk() throws Exception {
        ResponseDTO response = new ResponseDTO(MessageConstants.SUCCESS_STATUS, MessageConstants.FETCHING_LIST_OF_CONTACTS_SUCCESS);
        when(contactService.getListOfContactWithNonDynamicFilter(anyInt(), anyInt(), anyString())).thenReturn(response);

        mockMvc.perform(get("/internal/v1/contacts")
                .param("contactName", "John"))
            .andExpect(status().isOk());
    }

    @Test
    void getContactDetails_returnsOk() throws Exception {
        ResponseDTO response = new ResponseDTO(MessageConstants.SUCCESS_STATUS, MessageConstants.FETCHING_CONTACT_SUCCESS);
        when(contactService.getContactDetails(anyLong())).thenReturn(response);

        mockMvc.perform(get("/internal/v1/contacts/1"))
            .andExpect(status().isOk());
    }

    // Sales Orders

    @Test
    void searchOrders_returnsOk() throws Exception {
        ResponseDTO response = new ResponseDTO(MessageConstants.SUCCESS_STATUS, MessageConstants.FETCHING_LIST_OF_SALES_ORDER_SUCCESS);
        when(salesOrderService.retrieveOrderListWithNonDynamicFilter(anyInt(), anyInt(), anyString())).thenReturn(response);

        mockMvc.perform(get("/internal/v1/sales-orders")
                .param("subject", "test"))
            .andExpect(status().isOk());
    }

    @Test
    void getOrderDetails_returnsOk() throws Exception {
        ResponseDTO response = new ResponseDTO(MessageConstants.SUCCESS_STATUS, MessageConstants.FETCHING_SALES_ORDER_SUCCESS);
        when(salesOrderService.getSalesOrderDetails(anyLong())).thenReturn(response);

        mockMvc.perform(get("/internal/v1/sales-orders/5"))
            .andExpect(status().isOk());
    }

    @Test
    void getOrderStatusCounts_returnsOk() throws Exception {
        ResponseDTO response = new ResponseDTO(MessageConstants.SUCCESS_STATUS, MessageConstants.COUNTING_NO_SALES_ORDERS_BY_STATUS_SUCCESS);
        when(salesOrderService.retrieveOrderDashboardByStatus()).thenReturn(response);

        mockMvc.perform(get("/internal/v1/sales-orders/count/status"))
            .andExpect(status().isOk());
    }

    @Test
    void getOrderItems_returnsOk() throws Exception {
        ResponseDTO response = new ResponseDTO(MessageConstants.SUCCESS_STATUS, MessageConstants.FETCHING_ORDER_ITEMS_SUCCESS);
        when(salesOrderItemService.listOrderItems(anyLong())).thenReturn(response);

        mockMvc.perform(get("/internal/v1/sales-orders/5/items"))
            .andExpect(status().isOk());
    }

    // Notes

    @Test
    void listNotes_returnsOk() throws Exception {
        ResponseDTO response = new ResponseDTO(MessageConstants.SUCCESS_STATUS, MessageConstants.FETCHING_LIST_OF_NOTES_SUCCESS);
        when(noteService.listNotes(anyString(), anyLong())).thenReturn(response);

        mockMvc.perform(get("/internal/v1/notes")
                .param("entityType", "CONTACT")
                .param("entityFk", "1"))
            .andExpect(status().isOk());
    }

    // Products

    @Test
    void listProducts_returnsOk() throws Exception {
        ResponseDTO response = new ResponseDTO(MessageConstants.SUCCESS_STATUS, MessageConstants.FETCHING_LIST_OF_PRODUCTS_SUCCESS);
        when(productService.getListOfProduct(isNull(), anyInt(), anyInt(), anyString(), anyString(), isNull())).thenReturn(response);

        mockMvc.perform(get("/internal/v1/products"))
            .andExpect(status().isOk());
    }
}
