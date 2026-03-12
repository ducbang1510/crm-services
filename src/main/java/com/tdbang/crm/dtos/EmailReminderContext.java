/*
 * Copyright © 2026 by tdbang.
 * All rights reserved.
 */

package com.tdbang.crm.dtos;

import lombok.Getter;
import lombok.Setter;

import com.tdbang.crm.enums.EmailEntityType;

/**
 * Carries all data needed to send one reminder email.
 * Built by {@code EmailReminderItemProcessor} and consumed by
 * {@code EmailReminderItemWriter}. The {@code emailJobPk} references the
 * PENDING {@code EmailJob} row already persisted by the processor so the
 * writer can update its status after the send attempt.
 */
@Getter
@Setter
public class EmailReminderContext {

    /** PK of the PENDING EmailJob record created by the processor. */
    private Long emailJobPk;

    private EmailEntityType entityType;

    private Long entityPk;

    private String recipientEmail;

    private String recipientName;

    private String subject;

    private String body;
}
