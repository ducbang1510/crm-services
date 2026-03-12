/*
 * Copyright © 2026 by tdbang.
 * All rights reserved.
 */

package com.tdbang.crm.schedulers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.core.Job;

import com.tdbang.crm.batch.BatchJobRunnerService;
import com.tdbang.crm.config.CrmBatchProperties;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmailReminderSchedulerTest {

    @Mock
    private BatchJobRunnerService jobRunnerService;

    @Mock
    private CrmBatchProperties batchProperties;

    @Mock
    private Job contactReminderJob;

    @Mock
    private Job salesOrderReminderJob;

    private EmailReminderScheduler scheduler;

    private CrmBatchProperties.EmailJobProperties emailProps;

    @BeforeEach
    void setUp() {
        emailProps = mock(CrmBatchProperties.EmailJobProperties.class);
        when(batchProperties.getEmail()).thenReturn(emailProps);
        scheduler = new EmailReminderScheduler(
            jobRunnerService, batchProperties, contactReminderJob, salesOrderReminderJob);
    }

    @Test
    void runEmailReminderJobs_enabled_launchesBothJobs() {
        when(emailProps.isEnabled()).thenReturn(true);

        scheduler.runEmailReminderJobs();

        verify(jobRunnerService, times(1)).run(contactReminderJob);
        verify(jobRunnerService, times(1)).run(salesOrderReminderJob);
    }

    @Test
    void runEmailReminderJobs_disabled_skipsAllJobs() {
        when(emailProps.isEnabled()).thenReturn(false);

        scheduler.runEmailReminderJobs();

        verify(jobRunnerService, never()).run(contactReminderJob);
        verify(jobRunnerService, never()).run(salesOrderReminderJob);
    }

    @Test
    void runEmailReminderJobs_enabled_contactJobRunsBeforeSalesOrderJob() {
        when(emailProps.isEnabled()).thenReturn(true);

        scheduler.runEmailReminderJobs();

        // Verify both are called (order is enforced by the sequential impl)
        var inOrder = org.mockito.Mockito.inOrder(jobRunnerService);
        inOrder.verify(jobRunnerService).run(contactReminderJob);
        inOrder.verify(jobRunnerService).run(salesOrderReminderJob);
    }
}
