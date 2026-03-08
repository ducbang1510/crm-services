/*
 * Copyright © 2026 by tdbang.
 * All rights reserved.
 */

package com.tdbang.crm.batch.report;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.scope.context.StepContext;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;

import com.tdbang.crm.dtos.SalesOrderAggregationDTO;
import com.tdbang.crm.dtos.nativequerydto.SalesOrderUserSummaryDTO;
import com.tdbang.crm.repositories.SalesOrderRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExcelGeneratorTaskletTest {

    @Mock
    private SalesOrderRepository salesOrderRepository;

    private ExcelGeneratorTasklet tasklet;

    @BeforeEach
    void setUp() {
        tasklet = new ExcelGeneratorTasklet(salesOrderRepository);
    }

    @Test
    void execute_generatesWorkbookWithThreeSheets() throws Exception {
        SalesOrderUserSummaryDTO userSummary = mock(SalesOrderUserSummaryDTO.class);
        when(userSummary.getAssignedToName()).thenReturn("Alice");
        when(userSummary.getOrderCount()).thenReturn(3L);
        when(userSummary.getTotalRevenue()).thenReturn(new BigDecimal("1500.00"));
        when(salesOrderRepository.aggregateDailyOrdersByUser(any(), any()))
            .thenReturn(List.of(userSummary));

        ChunkContext chunkContext = buildChunkContextWithDto();
        RepeatStatus status = tasklet.execute(null, chunkContext);

        assertEquals(RepeatStatus.FINISHED, status);

        byte[] bytes = (byte[]) chunkContext.getStepContext()
            .getStepExecution().getJobExecution().getExecutionContext()
            .get(ExcelGeneratorTasklet.KEY_REPORT_BYTES);

        assertNotNull(bytes);
        assertTrue(bytes.length > 0);

        try (XSSFWorkbook wb = new XSSFWorkbook(
                new java.io.ByteArrayInputStream(bytes))) {

            assertEquals(3, wb.getNumberOfSheets());
            assertEquals("Summary", wb.getSheetName(0));
            assertEquals("By Status", wb.getSheetName(1));
            assertEquals("By User", wb.getSheetName(2));
        }
    }

    @Test
    void execute_summarySheetContainsCorrectValues() throws Exception {
        when(salesOrderRepository.aggregateDailyOrdersByUser(any(), any()))
            .thenReturn(List.of());

        ChunkContext chunkContext = buildChunkContextWithDto();
        tasklet.execute(null, chunkContext);

        byte[] bytes = (byte[]) chunkContext.getStepContext()
            .getStepExecution().getJobExecution().getExecutionContext()
            .get(ExcelGeneratorTasklet.KEY_REPORT_BYTES);

        try (XSSFWorkbook wb = new XSSFWorkbook(
                new java.io.ByteArrayInputStream(bytes))) {

            var summarySheet = wb.getSheet("Summary");
            // Header row
            assertEquals("Metric", summarySheet.getRow(0).getCell(0).getStringCellValue());
            assertEquals("Value", summarySheet.getRow(0).getCell(1).getStringCellValue());
            // Data rows
            assertEquals("Report Date", summarySheet.getRow(1).getCell(0).getStringCellValue());
            assertEquals("Total Orders", summarySheet.getRow(2).getCell(0).getStringCellValue());
            assertEquals("10", summarySheet.getRow(2).getCell(1).getStringCellValue());
            assertEquals("Total Revenue", summarySheet.getRow(3).getCell(0).getStringCellValue());
        }
    }

    @Test
    void execute_byStatusSheetContainsAllStatuses() throws Exception {
        when(salesOrderRepository.aggregateDailyOrdersByUser(any(), any()))
            .thenReturn(List.of());

        ChunkContext chunkContext = buildChunkContextWithDto();
        tasklet.execute(null, chunkContext);

        byte[] bytes = (byte[]) chunkContext.getStepContext()
            .getStepExecution().getJobExecution().getExecutionContext()
            .get(ExcelGeneratorTasklet.KEY_REPORT_BYTES);

        try (XSSFWorkbook wb = new XSSFWorkbook(
                new java.io.ByteArrayInputStream(bytes))) {

            var byStatusSheet = wb.getSheet("By Status");
            assertEquals("CREATED",   byStatusSheet.getRow(1).getCell(0).getStringCellValue());
            assertEquals("APPROVED",  byStatusSheet.getRow(2).getCell(0).getStringCellValue());
            assertEquals("DELIVERED", byStatusSheet.getRow(3).getCell(0).getStringCellValue());
            assertEquals("CANCELED",  byStatusSheet.getRow(4).getCell(0).getStringCellValue());
        }
    }

    @Test
    void execute_byUserSheetContainsUserData() throws Exception {
        SalesOrderUserSummaryDTO u1 = mock(SalesOrderUserSummaryDTO.class);
        when(u1.getAssignedToName()).thenReturn("Bob");
        when(u1.getOrderCount()).thenReturn(5L);
        when(u1.getTotalRevenue()).thenReturn(new BigDecimal("3000.00"));

        when(salesOrderRepository.aggregateDailyOrdersByUser(any(), any()))
            .thenReturn(List.of(u1));

        ChunkContext chunkContext = buildChunkContextWithDto();
        tasklet.execute(null, chunkContext);

        byte[] bytes = (byte[]) chunkContext.getStepContext()
            .getStepExecution().getJobExecution().getExecutionContext()
            .get(ExcelGeneratorTasklet.KEY_REPORT_BYTES);

        try (XSSFWorkbook wb = new XSSFWorkbook(
                new java.io.ByteArrayInputStream(bytes))) {

            var byUserSheet = wb.getSheet("By User");
            // Header
            assertEquals("Assigned To", byUserSheet.getRow(0).getCell(0).getStringCellValue());
            assertEquals("Order Count", byUserSheet.getRow(0).getCell(1).getStringCellValue());
            assertEquals("Total Revenue", byUserSheet.getRow(0).getCell(2).getStringCellValue());
            // Data row
            assertEquals("Bob", byUserSheet.getRow(1).getCell(0).getStringCellValue());
            assertEquals(5.0, byUserSheet.getRow(1).getCell(1).getNumericCellValue());
            assertEquals(3000.0, byUserSheet.getRow(1).getCell(2).getNumericCellValue(), 0.01);
        }
    }

    private ChunkContext buildChunkContextWithDto() {
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
        jobContext.putLong(SalesOrderAggregationTasklet.KEY_START_OF_DAY,
            System.currentTimeMillis() - 86_400_000L);
        jobContext.putLong(SalesOrderAggregationTasklet.KEY_END_OF_DAY,
            System.currentTimeMillis());

        StepExecution stepExecution = new StepExecution("excelStep", jobExecution);
        return new ChunkContext(new StepContext(stepExecution));
    }
}
