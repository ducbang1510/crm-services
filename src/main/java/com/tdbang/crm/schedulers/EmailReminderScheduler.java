/*
 * Copyright © 2026 by tdbang.
 * All rights reserved.
 */

package com.tdbang.crm.schedulers;

import lombok.extern.log4j.Log4j2;
import org.springframework.batch.core.Job;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.tdbang.crm.batch.BatchJobRunnerService;
import com.tdbang.crm.config.CrmBatchProperties;

/**
 * Triggers the email reminder batch jobs on a configurable cron schedule.
 *
 * <p>Both {@code contactReminderJob} and {@code salesOrderReminderJob} are
 * launched sequentially via {@link BatchJobRunnerService}. If
 * {@code crm.batch.email.enabled=false} neither job runs.
 */
@Log4j2
@Component
public class EmailReminderScheduler {

    private final BatchJobRunnerService jobRunnerService;
    private final CrmBatchProperties batchProperties;
    private final Job contactReminderJob;
    private final Job salesOrderReminderJob;

    public EmailReminderScheduler(
        BatchJobRunnerService jobRunnerService,
        CrmBatchProperties batchProperties,
        @Qualifier("contactReminderJob") Job contactReminderJob,
        @Qualifier("salesOrderReminderJob") Job salesOrderReminderJob) {

        this.jobRunnerService = jobRunnerService;
        this.batchProperties = batchProperties;
        this.contactReminderJob = contactReminderJob;
        this.salesOrderReminderJob = salesOrderReminderJob;
    }

    @Scheduled(cron = "${crm.batch.email.cron}")
    public void runEmailReminderJobs() {
        if (!batchProperties.getEmail().isEnabled()) {
            log.info("Email reminder jobs are disabled (crm.batch.email.enabled=false). Skipping.");
            return;
        }
        log.info("Starting scheduled email reminder jobs.");
        jobRunnerService.run(contactReminderJob);
        jobRunnerService.run(salesOrderReminderJob);
        log.info("Scheduled email reminder jobs completed.");
    }
}
