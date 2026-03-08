/*
 * Copyright © 2026 by tdbang.
 * All rights reserved.
 */

package com.tdbang.crm.batch.report;

import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;

import org.bson.types.ObjectId;
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
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;

import com.tdbang.crm.dtos.SalesOrderAggregationDTO;
import com.tdbang.crm.entities.DailySalesReport;
import com.tdbang.crm.enums.ReportStatus;
import com.tdbang.crm.repositories.DailySalesReportRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReportPersistenceTaskletTest {

    @Mock
    private GridFsTemplate gridFsTemplate;

    @Mock
    private DailySalesReportRepository dailySalesReportRepository;

    @Captor
    private ArgumentCaptor<DailySalesReport> reportCaptor;

    private ReportPersistenceTasklet tasklet;

    @BeforeEach
    void setUp() {
        tasklet = new ReportPersistenceTasklet(gridFsTemplate, dailySalesReportRepository);
    }

    @Test
    void execute_successPath_savesReportWithSuccessStatus() throws Exception {
        ObjectId objectId = new ObjectId();
        when(gridFsTemplate.store(any(InputStream.class), anyString(), anyString()))
            .thenReturn(objectId);
        when(dailySalesReportRepository.save(any(DailySalesReport.class)))
            .thenAnswer(inv -> inv.getArgument(0));

        ChunkContext chunkContext = buildChunkContext();
        RepeatStatus status = tasklet.execute(null, chunkContext);

        assertEquals(RepeatStatus.FINISHED, status);

        verify(gridFsTemplate).store(any(InputStream.class), anyString(), anyString());
        verify(dailySalesReportRepository).save(reportCaptor.capture());

        DailySalesReport saved = reportCaptor.getValue();
        assertEquals(ReportStatus.SUCCESS, saved.getStatus());
        assertEquals(objectId.toHexString(), saved.getMongoFileId());
        assertNotNull(saved.getFileName());
        assertTrue(saved.getFileName().contains("daily-sales-report-"));
        assertTrue(saved.getFileName().endsWith(".xlsx"));
        assertNull(saved.getErrorMessage());
        assertEquals(LocalDate.now().minusDays(1), saved.getReportDate());
        assertEquals(10, saved.getTotalOrders());
        assertEquals(new BigDecimal("5000.00"), saved.getTotalRevenue());
    }

    @Test
    void execute_gridFsThrows_savesReportWithFailedStatus() throws Exception {
        doThrow(new RuntimeException("MongoDB connection failed"))
            .when(gridFsTemplate).store(any(InputStream.class), anyString(), anyString());
        when(dailySalesReportRepository.save(any(DailySalesReport.class)))
            .thenAnswer(inv -> inv.getArgument(0));

        ChunkContext chunkContext = buildChunkContext();
        RepeatStatus status = tasklet.execute(null, chunkContext);

        // Always returns FINISHED — exceptions are caught internally
        assertEquals(RepeatStatus.FINISHED, status);

        verify(dailySalesReportRepository).save(reportCaptor.capture());

        DailySalesReport saved = reportCaptor.getValue();
        assertEquals(ReportStatus.FAILED, saved.getStatus());
        assertNull(saved.getMongoFileId());
        assertNotNull(saved.getErrorMessage());
        assertTrue(saved.getErrorMessage().contains("MongoDB connection failed"));
    }

    @Test
    void execute_longErrorMessage_truncatedTo500Chars() throws Exception {
        String longMessage = "E".repeat(600);
        doThrow(new RuntimeException(longMessage))
            .when(gridFsTemplate).store(any(InputStream.class), anyString(), anyString());
        when(dailySalesReportRepository.save(any(DailySalesReport.class)))
            .thenAnswer(inv -> inv.getArgument(0));

        tasklet.execute(null, buildChunkContext());

        verify(dailySalesReportRepository).save(reportCaptor.capture());
        assertEquals(500, reportCaptor.getValue().getErrorMessage().length());
    }

    private ChunkContext buildChunkContext() {
        SalesOrderAggregationDTO dto = new SalesOrderAggregationDTO();
        dto.setReportDate(LocalDate.now().minusDays(1));
        dto.setTotalOrders(10);
        dto.setTotalRevenue(new BigDecimal("5000.00"));
        dto.setOrdersCreated(4);
        dto.setOrdersApproved(3);
        dto.setOrdersDelivered(2);
        dto.setOrdersCanceled(1);

        JobExecution jobExecution = new JobExecution(1L);
        ExecutionContext jobContext = jobExecution.getExecutionContext();
        jobContext.put(SalesOrderAggregationTasklet.KEY_AGGREGATION_DTO, dto);
        jobContext.put(ExcelGeneratorTasklet.KEY_REPORT_BYTES, new byte[]{1, 2, 3});

        StepExecution stepExecution = new StepExecution("persistenceStep", jobExecution);
        return new ChunkContext(new StepContext(stepExecution));
    }
}
