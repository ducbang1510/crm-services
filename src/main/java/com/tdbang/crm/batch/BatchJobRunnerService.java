/*
 * Copyright © 2026 by tdbang.
 * All rights reserved.
 */

package com.tdbang.crm.batch;

import lombok.extern.log4j.Log4j2;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.stereotype.Service;

/**
 * Centralized service for launching Spring Batch jobs.
 */
@Log4j2
@Service
public class BatchJobRunnerService {

    private final JobLauncher jobLauncher;

    public BatchJobRunnerService(JobLauncher jobLauncher) {
        this.jobLauncher = jobLauncher;
    }

    /**
     * Launches the given job with a unique {@code runAt} timestamp parameter,
     * allowing the same job to be re-run multiple times.
     *
     * @param job the Spring Batch Job to launch
     * @return the {@link JobExecution} produced by the run
     * @throws IllegalStateException if the job fails to launch
     */
    public JobExecution run(Job job) {
        String jobName = job.getName();
        log.info("Launching batch job [{}]", jobName);
        try {
            JobParameters params = new JobParametersBuilder()
                .addLong("runAt", System.currentTimeMillis())
                .toJobParameters();
            JobExecution execution = jobLauncher.run(job, params);
            log.info("Batch job [{}] finished. BatchStatus: {}, ExitCode: {}",
                jobName, execution.getStatus(), execution.getExitStatus().getExitCode());
            return execution;
        } catch (Exception e) {
            log.error("Batch job [{}] failed to launch: {}", jobName, e.getMessage(), e);
            throw new IllegalStateException("Failed to launch batch job: " + jobName, e);
        }
    }
}
