/*
 * Copyright © 2026 by tdbang.
 * All rights reserved.
 */

package com.tdbang.crm.entities;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import com.tdbang.crm.enums.EmailEntityType;
import com.tdbang.crm.enums.EmailJobStatus;

/**
 * Tracks every reminder email attempt dispatched by the email reminder batch job.
 * One record is created per send attempt; status is updated to SENT or FAILED
 * after the attempt completes.
 */
@Getter
@Setter
@Entity
@Table(name = "email_job")
public class EmailJob {

    @Id
    @Column(name = "pk")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pk;

    @Column(name = "entity_type", nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private EmailEntityType entityType;

    @Column(name = "entity_pk", nullable = false)
    private Long entityPk;

    @Column(name = "recipient_email", nullable = false, length = 200)
    private String recipientEmail;

    @Column(name = "recipient_name", length = 255)
    private String recipientName;

    @Column(name = "subject", length = 255)
    private String subject;

    @Column(name = "status", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private EmailJobStatus status;

    @Column(name = "scheduled_at", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date scheduledAt;

    @Column(name = "sent_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date sentAt;

    @Column(name = "error_message", length = 500)
    private String errorMessage;

    @Column(name = "created_on")
    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    private Date createdOn;
}
