/*
 * Copyright © 2026 by tdbang.
 * All rights reserved.
 */

package com.tdbang.crm.services;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.tdbang.crm.dtos.ResponseDTO;
import com.tdbang.crm.dtos.nativequerydto.PipelineSummaryDTO;
import com.tdbang.crm.dtos.nativequerydto.RevenueTrendDTO;
import com.tdbang.crm.dtos.nativequerydto.TopUserDTO;
import com.tdbang.crm.repositories.SalesOrderRepository;
import com.tdbang.crm.utils.MessageConstants;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    @Mock
    private SalesOrderRepository salesOrderRepository;

    private DashboardService dashboardService;

    @BeforeEach
    void setUp() {
        dashboardService = new DashboardService();
        ReflectionTestUtils.setField(dashboardService, "salesOrderRepository", salesOrderRepository);
    }

    @Test
    void getRevenueTrend_returns12Months() {
        RevenueTrendDTO jan = mockRevenueTrend();
        RevenueTrendDTO feb = mockRevenueTrend();
        when(salesOrderRepository.aggregateRevenueTrend(12)).thenReturn(List.of(jan, feb));

        ResponseDTO result = dashboardService.getRevenueTrend(12);

        assertNotNull(result);
        assertEquals(MessageConstants.SUCCESS_STATUS, result.getStatus());
        assertEquals(MessageConstants.FETCHING_REVENUE_TREND_SUCCESS, result.getMsg());
        @SuppressWarnings("unchecked")
        List<RevenueTrendDTO> data = (List<RevenueTrendDTO>) result.getData();
        assertEquals(2, data.size());
    }

    @Test
    void getPipelineSummary_returnsAllStatuses() {
        PipelineSummaryDTO created = mockPipelineSummary();
        PipelineSummaryDTO approved = mockPipelineSummary();
        when(salesOrderRepository.aggregatePipelineSummary()).thenReturn(List.of(created, approved));

        ResponseDTO result = dashboardService.getPipelineSummary();

        assertNotNull(result);
        assertEquals(MessageConstants.SUCCESS_STATUS, result.getStatus());
        assertEquals(MessageConstants.FETCHING_PIPELINE_SUMMARY_SUCCESS, result.getMsg());
        @SuppressWarnings("unchecked")
        List<PipelineSummaryDTO> data = (List<PipelineSummaryDTO>) result.getData();
        assertEquals(2, data.size());
    }

    @Test
    void getTopUsers_limitsCorrectly() {
        TopUserDTO user1 = mockTopUser();
        TopUserDTO user2 = mockTopUser();
        when(salesOrderRepository.aggregateTopUsersByRevenue(5)).thenReturn(List.of(user1, user2));

        ResponseDTO result = dashboardService.getTopUsers(5);

        assertNotNull(result);
        assertEquals(MessageConstants.SUCCESS_STATUS, result.getStatus());
        assertEquals(MessageConstants.FETCHING_TOP_USERS_SUCCESS, result.getMsg());
        @SuppressWarnings("unchecked")
        List<TopUserDTO> data = (List<TopUserDTO>) result.getData();
        assertEquals(2, data.size());
    }

    // --- Helper methods ---

    private RevenueTrendDTO mockRevenueTrend() {
        return mock(RevenueTrendDTO.class);
    }

    private PipelineSummaryDTO mockPipelineSummary() {
        return mock(PipelineSummaryDTO.class);
    }

    private TopUserDTO mockTopUser() {
        return mock(TopUserDTO.class);
    }
}
