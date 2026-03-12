/*
 * Copyright © 2026 by tdbang.
 * All rights reserved.
 */

package com.tdbang.crm.batch.report;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.scope.context.StepContext;
import org.springframework.batch.repeat.RepeatStatus;

import com.tdbang.crm.dtos.SalesOrderAggregationDTO;
import com.tdbang.crm.dtos.nativequerydto.SalesOrderDailyAggregationDTO;
import com.tdbang.crm.repositories.SalesOrderRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SalesOrderAggregationTaskletTest {

    @Mock
    private SalesOrderRepository salesOrderRepository;

    @Captor
    private ArgumentCaptor<Date> startCaptor;

    @Captor
    private ArgumentCaptor<Date> endCaptor;

    private SalesOrderAggregationTasklet tasklet;

    @BeforeEach
    void setUp() {
        tasklet = new SalesOrderAggregationTasklet(salesOrderRepository);
    }

    @Test
    void execute_storesAggregationDtoAndDateRangeInJobContext() throws Exception {
        SalesOrderDailyAggregationDTO raw = mock(SalesOrderDailyAggregationDTO.class);
        when(raw.getTotalOrders()).thenReturn(5L);
        when(raw.getTotalRevenue()).thenReturn(new BigDecimal("2500.00"));
        when(raw.getOrdersCreated()).thenReturn(2);
        when(raw.getOrdersApproved()).thenReturn(1);
        when(raw.getOrdersDelivered()).thenReturn(1);
        when(raw.getOrdersCanceled()).thenReturn(1);
        when(salesOrderRepository.aggregateDailyOrders(any(Date.class), any(Date.class)))
            .thenReturn(raw);

        ChunkContext chunkContext = buildChunkContext();
        RepeatStatus status = tasklet.execute(null, chunkContext);

        assertEquals(RepeatStatus.FINISHED, status);

        verify(salesOrderRepository).aggregateDailyOrders(startCaptor.capture(), endCaptor.capture());
        Date start = startCaptor.getValue();
        Date end = endCaptor.getValue();

        // endOfDay must be > startOfDay and the difference must be ~1 day
        long diffMs = end.getTime() - start.getTime();
        assertEquals(86_400_000L, diffMs, 1000L);

        var jobContext = chunkContext.getStepContext()
            .getStepExecution().getJobExecution().getExecutionContext();

        SalesOrderAggregationDTO dto =
            (SalesOrderAggregationDTO) jobContext.get(SalesOrderAggregationTasklet.KEY_AGGREGATION_DTO);
        assertNotNull(dto);
        assertEquals(LocalDate.now().minusDays(1), dto.getReportDate());
        assertEquals(5, dto.getTotalOrders());
        assertEquals(new BigDecimal("2500.00"), dto.getTotalRevenue());
        assertEquals(2, dto.getOrdersCreated());
        assertEquals(1, dto.getOrdersApproved());
        assertEquals(1, dto.getOrdersDelivered());
        assertEquals(1, dto.getOrdersCanceled());

        assertNotNull(jobContext.get(SalesOrderAggregationTasklet.KEY_START_OF_DAY));
        assertNotNull(jobContext.get(SalesOrderAggregationTasklet.KEY_END_OF_DAY));
    }

    @Test
    void execute_nullValuesFromRepository_defaultsToZero() throws Exception {
        SalesOrderDailyAggregationDTO raw = mock(SalesOrderDailyAggregationDTO.class);
        when(raw.getTotalOrders()).thenReturn(null);
        when(raw.getTotalRevenue()).thenReturn(null);
        when(raw.getOrdersCreated()).thenReturn(null);
        when(raw.getOrdersApproved()).thenReturn(null);
        when(raw.getOrdersDelivered()).thenReturn(null);
        when(raw.getOrdersCanceled()).thenReturn(null);
        when(salesOrderRepository.aggregateDailyOrders(any(), any())).thenReturn(raw);

        ChunkContext chunkContext = buildChunkContext();
        tasklet.execute(null, chunkContext);

        var jobContext = chunkContext.getStepContext()
            .getStepExecution().getJobExecution().getExecutionContext();
        SalesOrderAggregationDTO dto =
            (SalesOrderAggregationDTO) jobContext.get(SalesOrderAggregationTasklet.KEY_AGGREGATION_DTO);

        assertEquals(0, dto.getTotalOrders());
        assertEquals(BigDecimal.ZERO, dto.getTotalRevenue());
        assertEquals(0, dto.getOrdersCreated());
    }

    private ChunkContext buildChunkContext() {
        JobExecution jobExecution = new JobExecution(1L);
        StepExecution stepExecution = new StepExecution("aggregationStep", jobExecution);
        return new ChunkContext(new StepContext(stepExecution));
    }
}
