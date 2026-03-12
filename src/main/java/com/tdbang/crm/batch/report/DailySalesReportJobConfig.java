/*
 * Copyright © 2026 by tdbang.
 * All rights reserved.
 */

package com.tdbang.crm.batch.report;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * Spring Batch job definition for the daily sales report feature.
 *
 * <p>The {@code dailySalesReportJob} is composed of three sequential steps:
 * <ol>
 *   <li><b>aggregationStep</b> — queries yesterday's order totals and stores
 *       them in the job {@code ExecutionContext}.</li>
 *   <li><b>excelGenerationStep</b> — builds an Excel workbook from the
 *       aggregation data and stores the bytes in the job {@code ExecutionContext}.</li>
 *   <li><b>persistenceStep</b> — uploads the Excel file to MongoDB GridFS
 *       and saves the report metadata to MySQL. Always completes (catches its
 *       own exceptions and records a {@code FAILED} status).</li>
 * </ol>
 *
 * <p>The job is fail-fast: if aggregation or Excel generation throws, the
 * remaining steps are skipped and the job exits with {@code FAILED} status.
 */
@Configuration
public class DailySalesReportJobConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final SalesOrderAggregationTasklet aggregationTasklet;
    private final ExcelGeneratorTasklet excelGeneratorTasklet;
    private final ReportPersistenceTasklet persistenceTasklet;

    public DailySalesReportJobConfig(
        JobRepository jobRepository,
        PlatformTransactionManager transactionManager,
        SalesOrderAggregationTasklet aggregationTasklet,
        ExcelGeneratorTasklet excelGeneratorTasklet,
        ReportPersistenceTasklet persistenceTasklet) {

        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
        this.aggregationTasklet = aggregationTasklet;
        this.excelGeneratorTasklet = excelGeneratorTasklet;
        this.persistenceTasklet = persistenceTasklet;
    }

    @Bean
    public Step aggregationStep() {
        return new StepBuilder("aggregationStep", jobRepository)
            .tasklet(aggregationTasklet, transactionManager)
            .build();
    }

    @Bean
    public Step excelGenerationStep() {
        return new StepBuilder("excelGenerationStep", jobRepository)
            .tasklet(excelGeneratorTasklet, transactionManager)
            .build();
    }

    @Bean
    public Step persistenceStep() {
        return new StepBuilder("persistenceStep", jobRepository)
            .tasklet(persistenceTasklet, transactionManager)
            .build();
    }

    @Bean
    public Job dailySalesReportJob() {
        return new JobBuilder("dailySalesReportJob", jobRepository)
            .start(aggregationStep())
            .next(excelGenerationStep())
            .next(persistenceStep())
            .build();
    }
}
