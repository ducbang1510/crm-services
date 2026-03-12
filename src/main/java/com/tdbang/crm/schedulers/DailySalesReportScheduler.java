/*
 * Copyright © 2026 by tdbang.
 * All rights reserved.
 */

package com.tdbang.crm.schedulers;

import java.time.LocalDate;

import lombok.extern.log4j.Log4j2;
import org.springframework.batch.core.Job;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.tdbang.crm.batch.BatchJobRunnerService;
import com.tdbang.crm.config.CrmBatchProperties;
import com.tdbang.crm.enums.ReportStatus;
import com.tdbang.crm.repositories.DailySalesReportRepository;

/**
 * Triggers the daily sales report batch job on a configurable cron schedule.
 *
 * <p>Before launching, checks whether a {@code SUCCESS} report already exists
 * for <em>yesterday</em> to prevent duplicate reports on re-trigger or restart.
 * If {@code crm.batch.report.enabled=false} the job is skipped entirely.
 */
@Log4j2
@Component
public class DailySalesReportScheduler {

    private final BatchJobRunnerService jobRunnerService;
    private final CrmBatchProperties batchProperties;
    private final DailySalesReportRepository dailySalesReportRepository;
    private final Job dailySalesReportJob;

    public DailySalesReportScheduler(
        BatchJobRunnerService jobRunnerService,
        CrmBatchProperties batchProperties,
        DailySalesReportRepository dailySalesReportRepository,
        @Qualifier("dailySalesReportJob") Job dailySalesReportJob) {

        this.jobRunnerService = jobRunnerService;
        this.batchProperties = batchProperties;
        this.dailySalesReportRepository = dailySalesReportRepository;
        this.dailySalesReportJob = dailySalesReportJob;
    }

    @Scheduled(cron = "${crm.batch.report.cron}")
    public void runDailySalesReportJob() {
        if (!batchProperties.getReport().isEnabled()) {
            log.info("Daily sales report job is disabled (crm.batch.report.enabled=false). Skipping.");
            return;
        }

        LocalDate reportDate = LocalDate.now().minusDays(1);
        if (dailySalesReportRepository.existsByReportDateAndStatus(reportDate, ReportStatus.SUCCESS)) {
            log.info("SUCCESS report already exists for {}. Skipping daily sales report job.", reportDate);
            return;
        }

        log.info("Starting scheduled daily sales report job for date: {}", reportDate);
        jobRunnerService.run(dailySalesReportJob);
        log.info("Scheduled daily sales report job completed.");
    }
}
