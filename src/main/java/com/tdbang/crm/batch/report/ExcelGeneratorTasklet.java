/*
 * Copyright © 2026 by tdbang.
 * All rights reserved.
 */

package com.tdbang.crm.batch.report;

import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

import com.tdbang.crm.dtos.SalesOrderAggregationDTO;
import com.tdbang.crm.dtos.nativequerydto.SalesOrderUserSummaryDTO;
import com.tdbang.crm.repositories.SalesOrderRepository;

/**
 * Step 2 of the daily sales report job.
 *
 * <p>Reads the {@link SalesOrderAggregationDTO} stored by
 * {@link SalesOrderAggregationTasklet}, generates a three-sheet Excel workbook
 * using Apache POI, and stores the resulting {@code byte[]} in the job-level
 * {@link ExecutionContext} for {@link ReportPersistenceTasklet}.
 *
 * <ul>
 *   <li>Sheet 1 <b>"Summary"</b>: report date, total revenue, total orders</li>
 *   <li>Sheet 2 <b>"By Status"</b>: per-status order counts</li>
 *   <li>Sheet 3 <b>"By User"</b>: per-assignee order count and revenue</li>
 * </ul>
 */
@Log4j2
@Component
@RequiredArgsConstructor
public class ExcelGeneratorTasklet implements Tasklet {

    static final String KEY_REPORT_BYTES = "reportBytes";

    private final SalesOrderRepository salesOrderRepository;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        ExecutionContext jobContext = chunkContext.getStepContext()
            .getStepExecution()
            .getJobExecution()
            .getExecutionContext();

        SalesOrderAggregationDTO dto =
            (SalesOrderAggregationDTO) jobContext.get(SalesOrderAggregationTasklet.KEY_AGGREGATION_DTO);

        long startMillis = jobContext.getLong(SalesOrderAggregationTasklet.KEY_START_OF_DAY);
        long endMillis = jobContext.getLong(SalesOrderAggregationTasklet.KEY_END_OF_DAY);
        List<SalesOrderUserSummaryDTO> userSummary = salesOrderRepository.aggregateDailyOrdersByUser(
            new Date(startMillis), new Date(endMillis));

        log.info("Generating Excel report for date: {}", dto.getReportDate());

        byte[] bytes = buildWorkbook(dto, userSummary);
        jobContext.put(KEY_REPORT_BYTES, bytes);

        log.info("Excel report generated: {} bytes", bytes.length);
        return RepeatStatus.FINISHED;
    }

    private byte[] buildWorkbook(SalesOrderAggregationDTO dto,
                                 List<SalesOrderUserSummaryDTO> userSummary) throws Exception {
        try (XSSFWorkbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            CellStyle headerStyle = createHeaderStyle(workbook);

            buildSummarySheet(workbook, headerStyle, dto);
            buildByStatusSheet(workbook, headerStyle, dto);
            buildByUserSheet(workbook, headerStyle, userSummary);

            workbook.write(out);
            return out.toByteArray();
        }
    }

    // Sheet 1 — Summary
    private void buildSummarySheet(XSSFWorkbook wb, CellStyle headerStyle,
                                   SalesOrderAggregationDTO dto) {
        Sheet sheet = wb.createSheet("Summary");

        Row headerRow = sheet.createRow(0);
        createHeaderCell(headerRow, 0, "Metric", headerStyle);
        createHeaderCell(headerRow, 1, "Value", headerStyle);

        createDataRow(sheet, 1, "Report Date", dto.getReportDate().toString());
        createDataRow(sheet, 2, "Total Orders", String.valueOf(dto.getTotalOrders()));
        createDataRow(sheet, 3, "Total Revenue", dto.getTotalRevenue().toPlainString());

        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
    }

    // Sheet 2 — By Status
    private void buildByStatusSheet(XSSFWorkbook wb, CellStyle headerStyle,
                                    SalesOrderAggregationDTO dto) {
        Sheet sheet = wb.createSheet("By Status");

        Row headerRow = sheet.createRow(0);
        createHeaderCell(headerRow, 0, "Status", headerStyle);
        createHeaderCell(headerRow, 1, "Order Count", headerStyle);

        createDataRow(sheet, 1, "CREATED", String.valueOf(dto.getOrdersCreated()));
        createDataRow(sheet, 2, "APPROVED", String.valueOf(dto.getOrdersApproved()));
        createDataRow(sheet, 3, "DELIVERED", String.valueOf(dto.getOrdersDelivered()));
        createDataRow(sheet, 4, "CANCELED", String.valueOf(dto.getOrdersCanceled()));

        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
    }

    // Sheet 3 — By User
    private void buildByUserSheet(XSSFWorkbook wb, CellStyle headerStyle,
                                  List<SalesOrderUserSummaryDTO> userSummary) {
        Sheet sheet = wb.createSheet("By User");

        Row headerRow = sheet.createRow(0);
        createHeaderCell(headerRow, 0, "Assigned To", headerStyle);
        createHeaderCell(headerRow, 1, "Order Count", headerStyle);
        createHeaderCell(headerRow, 2, "Total Revenue", headerStyle);

        int rowNum = 1;
        for (SalesOrderUserSummaryDTO u : userSummary) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(u.getAssignedToName() != null ? u.getAssignedToName() : "Unassigned");
            row.createCell(1).setCellValue(u.getOrderCount() != null ? u.getOrderCount() : 0L);
            row.createCell(2).setCellValue(
                u.getTotalRevenue() != null ? u.getTotalRevenue().doubleValue() : 0.0);
        }

        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
        sheet.autoSizeColumn(2);
    }

    // Helpers
    private CellStyle createHeaderStyle(XSSFWorkbook wb) {
        CellStyle style = wb.createCellStyle();
        style.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        Font font = wb.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        return style;
    }

    private void createHeaderCell(Row row, int col, String value, CellStyle style) {
        Cell cell = row.createCell(col);
        cell.setCellValue(value);
        cell.setCellStyle(style);
    }

    private void createDataRow(Sheet sheet, int rowNum, String label, String value) {
        Row row = sheet.createRow(rowNum);
        row.createCell(0).setCellValue(label);
        row.createCell(1).setCellValue(value);
    }
}
