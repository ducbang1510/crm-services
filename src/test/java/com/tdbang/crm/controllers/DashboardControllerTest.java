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
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.tdbang.crm.dtos.ResponseDTO;
import com.tdbang.crm.services.DashboardService;
import com.tdbang.crm.services.SecurityService;
import com.tdbang.crm.services.UserService;
import com.tdbang.crm.utils.MessageConstants;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
    value = DashboardController.class,
    excludeAutoConfiguration = {SecurityAutoConfiguration.class, SecurityFilterAutoConfiguration.class}
)
class DashboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DashboardService dashboardService;

    @MockitoBean
    private SecurityService securityService;

    @MockitoBean
    private UserService userService;

    @Test
    void getRevenueTrend_returnsOk() throws Exception {
        ResponseDTO response = new ResponseDTO(MessageConstants.SUCCESS_STATUS, MessageConstants.FETCHING_REVENUE_TREND_SUCCESS);
        when(dashboardService.getRevenueTrend(anyInt())).thenReturn(response);

        mockMvc.perform(get("/api/v1/dashboard/revenue-trend"))
            .andExpect(status().isOk());
    }

    @Test
    void getRevenueTrend_withCustomMonths_returnsOk() throws Exception {
        ResponseDTO response = new ResponseDTO(MessageConstants.SUCCESS_STATUS, MessageConstants.FETCHING_REVENUE_TREND_SUCCESS);
        when(dashboardService.getRevenueTrend(6)).thenReturn(response);

        mockMvc.perform(get("/api/v1/dashboard/revenue-trend")
                .param("months", "6"))
            .andExpect(status().isOk());
    }

    @Test
    void getPipelineSummary_returnsOk() throws Exception {
        ResponseDTO response = new ResponseDTO(MessageConstants.SUCCESS_STATUS, MessageConstants.FETCHING_PIPELINE_SUMMARY_SUCCESS);
        when(dashboardService.getPipelineSummary()).thenReturn(response);

        mockMvc.perform(get("/api/v1/dashboard/pipeline-summary"))
            .andExpect(status().isOk());
    }

    @Test
    void getTopUsers_returnsOk() throws Exception {
        ResponseDTO response = new ResponseDTO(MessageConstants.SUCCESS_STATUS, MessageConstants.FETCHING_TOP_USERS_SUCCESS);
        when(dashboardService.getTopUsers(anyInt())).thenReturn(response);

        mockMvc.perform(get("/api/v1/dashboard/top-users"))
            .andExpect(status().isOk());
    }

    @Test
    void getTopUsers_withCustomLimit_returnsOk() throws Exception {
        ResponseDTO response = new ResponseDTO(MessageConstants.SUCCESS_STATUS, MessageConstants.FETCHING_TOP_USERS_SUCCESS);
        when(dashboardService.getTopUsers(10)).thenReturn(response);

        mockMvc.perform(get("/api/v1/dashboard/top-users")
                .param("limit", "10"))
            .andExpect(status().isOk());
    }
}
