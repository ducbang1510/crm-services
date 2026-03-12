/*
 * Copyright © 2026 by tdbang.
 * All rights reserved.
 */

package com.tdbang.crm.batch.email;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import com.tdbang.crm.config.CrmBatchProperties;
import com.tdbang.crm.entities.Contact;
import com.tdbang.crm.entities.SalesOrder;

/**
 * Spring Batch job definitions for the email reminder feature.
 *
 * <ul>
 *   <li><b>contactReminderJob</b> — scans inactive {@link Contact} records and
 *       sends follow-up reminder emails to their assigned users.</li>
 *   <li><b>salesOrderReminderJob</b> — scans {@link SalesOrder} records in
 *       actionable statuses and sends follow-up reminder emails.</li>
 * </ul>
 *
 * <p>Both jobs share the same {@link EmailReminderItemProcessor} (singleton) and
 * {@link EmailReminderItemWriter} (singleton). Readers are {@code @StepScope} so
 * each step execution gets a fresh reader with a recalculated cutoff date.
 */
@Configuration
public class EmailReminderJobConfig {

    private static final int CHUNK_SIZE = 10;

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final EntityManagerFactory entityManagerFactory;
    private final CrmBatchProperties batchProperties;
    private final EmailReminderItemProcessor processor;
    private final EmailReminderItemWriter writer;

    public EmailReminderJobConfig(JobRepository jobRepository, PlatformTransactionManager transactionManager, EntityManagerFactory entityManagerFactory,
                                  CrmBatchProperties batchProperties, EmailReminderItemProcessor processor, EmailReminderItemWriter writer) {

        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
        this.entityManagerFactory = entityManagerFactory;
        this.batchProperties = batchProperties;
        this.processor = processor;
        this.writer = writer;
    }

    // Contact reminder job
    @Bean
    public ContactReminderItemReader contactReminderItemReader() {
        return new ContactReminderItemReader(
            entityManagerFactory,
            batchProperties.getEmail().getInactivityDaysContact());
    }

    @Bean
    public Step contactReminderStep() {
        return new StepBuilder("contactReminderStep", jobRepository)
            .<Contact, com.tdbang.crm.dtos.EmailReminderContext>chunk(CHUNK_SIZE, transactionManager)
            .reader(contactReminderItemReader())
            .processor(processor)
            .writer(writer)
            .build();
    }

    @Bean
    public Job contactReminderJob(@Qualifier("contactReminderStep") Step contactReminderStep) {
        return new JobBuilder("contactReminderJob", jobRepository)
            .start(contactReminderStep)
            .build();
    }

    // Sales order reminder job
    @Bean
    public SalesOrderReminderItemReader salesOrderReminderItemReader() {
        return new SalesOrderReminderItemReader(
            entityManagerFactory,
            batchProperties.getEmail().getInactivityDaysOrder());
    }

    @Bean
    public Step salesOrderReminderStep() {
        return new StepBuilder("salesOrderReminderStep", jobRepository)
            .<SalesOrder, com.tdbang.crm.dtos.EmailReminderContext>chunk(CHUNK_SIZE, transactionManager)
            .reader(salesOrderReminderItemReader())
            .processor(processor)
            .writer(writer)
            .build();
    }

    @Bean
    public Job salesOrderReminderJob(@Qualifier("salesOrderReminderStep") Step salesOrderReminderStep) {
        return new JobBuilder("salesOrderReminderJob", jobRepository)
            .start(salesOrderReminderStep)
            .build();
    }
}
