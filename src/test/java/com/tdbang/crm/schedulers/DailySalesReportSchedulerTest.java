/*
 * Copyright © 2026 by tdbang.
 * All rights reserved.
 */

package com.tdbang.crm.schedulers;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.core.Job;

import com.tdbang.crm.batch.BatchJobRunnerService;
import com.tdbang.crm.config.CrmBatchProperties;
import com.tdbang.crm.enums.ReportStatus;
import com.tdbang.crm.repositories.DailySalesReportRepository;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DailySalesReportSchedulerTest {

    @Mock
    private BatchJobRunnerService jobRunnerService;

    @Mock
    private CrmBatchProperties batchProperties;

    @Mock
    private DailySalesReportRepository dailySalesReportRepository;

    @Mock
    private Job dailySalesReportJob;

    private DailySalesReportScheduler scheduler;

    private CrmBatchProperties.ReportJobProperties reportProps;

    @BeforeEach
    void setUp() {
        reportProps = mock(CrmBatchProperties.ReportJobProperties.class);
        when(batchProperties.getReport()).thenReturn(reportProps);
        scheduler = new DailySalesReportScheduler(
            jobRunnerService, batchProperties, dailySalesReportRepository, dailySalesReportJob);
    }

    @Test
    void runDailySalesReportJob_enabled_noExistingReport_launchesJob() {
        when(reportProps.isEnabled()).thenReturn(true);
        when(dailySalesReportRepository.existsByReportDateAndStatus(
            any(LocalDate.class), eq(ReportStatus.SUCCESS))).thenReturn(false);

        scheduler.runDailySalesReportJob();

        verify(jobRunnerService).run(dailySalesReportJob);
    }

    @Test
    void runDailySalesReportJob_enabled_successReportAlreadyExists_skipsJob() {
        when(reportProps.isEnabled()).thenReturn(true);
        when(dailySalesReportRepository.existsByReportDateAndStatus(
            any(LocalDate.class), eq(ReportStatus.SUCCESS))).thenReturn(true);

        scheduler.runDailySalesReportJob();

        verify(jobRunnerService, never()).run(any());
    }

    @Test
    void runDailySalesReportJob_disabled_skipsJob() {
        when(reportProps.isEnabled()).thenReturn(false);

        scheduler.runDailySalesReportJob();

        verify(jobRunnerService, never()).run(any());
        verify(dailySalesReportRepository, never()).existsByReportDateAndStatus(any(), any());
    }

    @Test
    void runDailySalesReportJob_checksYesterday() {
        when(reportProps.isEnabled()).thenReturn(true);
        LocalDate yesterday = LocalDate.now().minusDays(1);
        when(dailySalesReportRepository.existsByReportDateAndStatus(
            eq(yesterday), eq(ReportStatus.SUCCESS))).thenReturn(false);

        scheduler.runDailySalesReportJob();

        verify(dailySalesReportRepository).existsByReportDateAndStatus(
            eq(yesterday), eq(ReportStatus.SUCCESS));
    }
}
