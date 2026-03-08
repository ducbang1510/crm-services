/*
 * Copyright © 2026 by tdbang.
 * All rights reserved.
 */

package com.tdbang.crm.batch.report;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

import com.tdbang.crm.dtos.SalesOrderAggregationDTO;
import com.tdbang.crm.dtos.nativequerydto.SalesOrderDailyAggregationDTO;
import com.tdbang.crm.repositories.SalesOrderRepository;

/**
 * Step 1 of the daily sales report job.
 *
 * <p>Queries aggregated sales order statistics for <em>yesterday</em> and
 * stores the result in the {@link ExecutionContext} (job-level) so that
 * subsequent tasklets can consume it without re-querying the database.
 *
 * <p>Context keys written:
 * <ul>
 *   <li>{@code aggregationDto} — {@link SalesOrderAggregationDTO}</li>
 *   <li>{@code startOfDay} — epoch millis (long) of yesterday 00:00:00</li>
 *   <li>{@code endOfDay} — epoch millis (long) of today 00:00:00 (exclusive)</li>
 * </ul>
 */
@Log4j2
@Component
@RequiredArgsConstructor
public class SalesOrderAggregationTasklet implements Tasklet {

    static final String KEY_AGGREGATION_DTO = "aggregationDto";
    static final String KEY_START_OF_DAY = "startOfDay";
    static final String KEY_END_OF_DAY = "endOfDay";

    private final SalesOrderRepository salesOrderRepository;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
        LocalDate reportDate = LocalDate.now().minusDays(1);
        Date startOfDay = toDate(reportDate);
        Date endOfDay = toDate(reportDate.plusDays(1));

        log.info("Aggregating sales orders for date: {}", reportDate);

        SalesOrderDailyAggregationDTO raw =
            salesOrderRepository.aggregateDailyOrders(startOfDay, endOfDay);

        SalesOrderAggregationDTO dto = mapToDto(reportDate, raw);
        log.info("Aggregation result: totalOrders={}, totalRevenue={}",
            dto.getTotalOrders(), dto.getTotalRevenue());

        ExecutionContext jobContext = chunkContext.getStepContext()
            .getStepExecution()
            .getJobExecution()
            .getExecutionContext();

        jobContext.put(KEY_AGGREGATION_DTO, dto);
        jobContext.putLong(KEY_START_OF_DAY, startOfDay.getTime());
        jobContext.putLong(KEY_END_OF_DAY, endOfDay.getTime());

        return RepeatStatus.FINISHED;
    }

    private SalesOrderAggregationDTO mapToDto(LocalDate reportDate, SalesOrderDailyAggregationDTO raw) {
        SalesOrderAggregationDTO dto = new SalesOrderAggregationDTO();
        dto.setReportDate(reportDate);
        dto.setTotalRevenue(raw.getTotalRevenue() != null ? raw.getTotalRevenue() : BigDecimal.ZERO);
        dto.setTotalOrders(raw.getTotalOrders() != null ? raw.getTotalOrders().intValue() : 0);
        dto.setOrdersCreated(raw.getOrdersCreated() != null ? raw.getOrdersCreated() : 0);
        dto.setOrdersApproved(raw.getOrdersApproved() != null ? raw.getOrdersApproved() : 0);
        dto.setOrdersDelivered(raw.getOrdersDelivered() != null ? raw.getOrdersDelivered() : 0);
        dto.setOrdersCanceled(raw.getOrdersCanceled() != null ? raw.getOrdersCanceled() : 0);
        return dto;
    }

    private Date toDate(LocalDate date) {
        return Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }
}
