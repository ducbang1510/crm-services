/*
 * Copyright © 2026 by tdbang.
 * All rights reserved.
 */

package com.tdbang.crm.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.tdbang.crm.dtos.ResponseDTO;
import com.tdbang.crm.dtos.nativequerydto.PipelineSummaryDTO;
import com.tdbang.crm.dtos.nativequerydto.RevenueTrendDTO;
import com.tdbang.crm.dtos.nativequerydto.TopUserDTO;
import com.tdbang.crm.enums.SalesOrderStatus;
import com.tdbang.crm.exceptions.CRMException;
import com.tdbang.crm.repositories.SalesOrderRepository;
import com.tdbang.crm.utils.MessageConstants;

@Log4j2
@Service
public class DashboardService {

    @Autowired
    private SalesOrderRepository salesOrderRepository;

    public ResponseDTO getRevenueTrend(int months) {
        try {
            List<RevenueTrendDTO> trend = salesOrderRepository.aggregateRevenueTrend(months);
            return new ResponseDTO(MessageConstants.SUCCESS_STATUS, MessageConstants.FETCHING_REVENUE_TREND_SUCCESS, trend);
        } catch (Exception e) {
            throw new CRMException(HttpStatus.INTERNAL_SERVER_ERROR,
                MessageConstants.INTERNAL_ERROR_CODE, MessageConstants.FETCHING_REVENUE_TREND_ERROR, e.getMessage());
        }
    }

    public ResponseDTO getPipelineSummary() {
        try {
            List<PipelineSummaryDTO> summary = salesOrderRepository.aggregatePipelineSummary();
            SalesOrderStatus[] statuses = SalesOrderStatus.values();
            List<Map<String, Object>> mapped = new ArrayList<>();
            for (PipelineSummaryDTO dto : summary) {
                Map<String, Object> entry = new HashMap<>();
                int ordinal = dto.getStatus();
                entry.put("status", ordinal >= 0 && ordinal < statuses.length
                        ? statuses[ordinal].getName() : String.valueOf(ordinal));
                entry.put("orderCount", dto.getOrderCount());
                entry.put("totalRevenue", dto.getTotalRevenue());
                mapped.add(entry);
            }
            return new ResponseDTO(MessageConstants.SUCCESS_STATUS, MessageConstants.FETCHING_PIPELINE_SUMMARY_SUCCESS, mapped);
        } catch (Exception e) {
            throw new CRMException(HttpStatus.INTERNAL_SERVER_ERROR,
                MessageConstants.INTERNAL_ERROR_CODE, MessageConstants.FETCHING_PIPELINE_SUMMARY_ERROR, e.getMessage());
        }
    }

    public ResponseDTO getTopUsers(int limit) {
        try {
            List<TopUserDTO> topUsers = salesOrderRepository.aggregateTopUsersByRevenue(limit);
            return new ResponseDTO(MessageConstants.SUCCESS_STATUS, MessageConstants.FETCHING_TOP_USERS_SUCCESS, topUsers);
        } catch (Exception e) {
            throw new CRMException(HttpStatus.INTERNAL_SERVER_ERROR,
                MessageConstants.INTERNAL_ERROR_CODE, MessageConstants.FETCHING_TOP_USERS_ERROR, e.getMessage());
        }
    }
}
