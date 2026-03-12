/*
 * Copyright © 2026 by tdbang.
 * All rights reserved.
 */

package com.tdbang.crm.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for all CRM batch jobs.
 * To add a new job, add a new nested static class here
 * property block under "crm.batch.*" in crm-services.properties.
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "crm.batch")
public class CrmBatchProperties {

    private EmailJobProperties email = new EmailJobProperties();

    private ReportJobProperties report = new ReportJobProperties();

    @Getter
    @Setter
    public static class EmailJobProperties {
        /** Whether the email reminder job is enabled. */
        private boolean enabled = true;
        /** Cron expression for the email reminder job. */
        private String cron = "0 0 7 * * ?";
        /** Days of inactivity before sending a contact follow-up reminder. */
        private int inactivityDaysContact = 7;
        /** Days of inactivity before sending a sales order follow-up reminder. */
        private int inactivityDaysOrder = 3;
    }

    @Getter
    @Setter
    public static class ReportJobProperties {
        /** Whether the daily sales report job is enabled. */
        private boolean enabled = true;
        /** Cron expression for the daily sales report generation job. */
        private String cron = "0 0 1 * * ?";
    }
}
