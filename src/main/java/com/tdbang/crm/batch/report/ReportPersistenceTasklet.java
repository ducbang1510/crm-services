/*
 * Copyright © 2026 by tdbang.
 * All rights reserved.
 */

package com.tdbang.crm.batch.report;

import java.io.ByteArrayInputStream;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.bson.types.ObjectId;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Component;

import com.tdbang.crm.dtos.SalesOrderAggregationDTO;
import com.tdbang.crm.entities.DailySalesReport;
import com.tdbang.crm.enums.ReportStatus;
import com.tdbang.crm.repositories.DailySalesReportRepository;

/**
 * Step 3 of the daily sales report job.
 *
 * <p>Reads the Excel {@code byte[]} stored by {@link ExcelGeneratorTasklet},
 * uploads it to MongoDB GridFS, then persists a {@link DailySalesReport}
 * record to MySQL with status {@code SUCCESS} and the GridFS file ID.
 *
 * <p>If any exception is thrown during GridFS upload or MySQL persistence,
 * the error is caught and a {@code FAILED} record is saved so that the
 * outcome is always traceable in the database.
 */
@Log4j2
@Component
@RequiredArgsConstructor
public class ReportPersistenceTasklet implements Tasklet {

    private static final String CONTENT_TYPE =
        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    private final GridFsTemplate gridFsTemplate;
    private final DailySalesReportRepository dailySalesReportRepository;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
        ExecutionContext jobContext = chunkContext.getStepContext()
            .getStepExecution()
            .getJobExecution()
            .getExecutionContext();

        SalesOrderAggregationDTO dto =
            (SalesOrderAggregationDTO) jobContext.get(SalesOrderAggregationTasklet.KEY_AGGREGATION_DTO);
        byte[] reportBytes =
            (byte[]) jobContext.get(ExcelGeneratorTasklet.KEY_REPORT_BYTES);

        String fileName = "daily-sales-report-" + dto.getReportDate() + ".xlsx";

        try {
            ObjectId objectId = gridFsTemplate.store(
                new ByteArrayInputStream(reportBytes), fileName, CONTENT_TYPE);

            log.info("Excel report uploaded to GridFS: fileId={}, fileName={}", objectId, fileName);

            saveReport(dto, objectId.toHexString(), fileName, ReportStatus.SUCCESS, null);

        } catch (Exception e) {
            log.error("Failed to persist daily sales report for date {}: {}",
                dto.getReportDate(), e.getMessage(), e);
            String truncated = e.getMessage() != null && e.getMessage().length() > 500
                ? e.getMessage().substring(0, 500) : e.getMessage();
            saveReport(dto, null, fileName, ReportStatus.FAILED, truncated);
        }

        return RepeatStatus.FINISHED;
    }

    private void saveReport(SalesOrderAggregationDTO dto, String mongoFileId,
                            String fileName, ReportStatus status, String errorMessage) {
        DailySalesReport report = new DailySalesReport();
        report.setReportDate(dto.getReportDate());
        report.setTotalRevenue(dto.getTotalRevenue());
        report.setTotalOrders(dto.getTotalOrders());
        report.setOrdersCreated(dto.getOrdersCreated());
        report.setOrdersApproved(dto.getOrdersApproved());
        report.setOrdersDelivered(dto.getOrdersDelivered());
        report.setOrdersCanceled(dto.getOrdersCanceled());
        report.setMongoFileId(mongoFileId);
        report.setFileName(fileName);
        report.setStatus(status);
        report.setErrorMessage(errorMessage);
        dailySalesReportRepository.save(report);
        log.info("DailySalesReport saved: reportDate={}, status={}", dto.getReportDate(), status);
    }
}
