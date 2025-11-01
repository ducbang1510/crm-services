/*
 * Copyright © 2025 by tdbang.
 * All rights reserved.
 */

package com.tdbang.crm.entities;

import java.io.Serializable;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.RevisionEntity;
import org.hibernate.envers.RevisionNumber;
import org.hibernate.envers.RevisionTimestamp;

import com.tdbang.crm.listeners.AuditLogListener;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "audit_log")
@RevisionEntity(AuditLogListener.class)
public class AuditLog implements Serializable {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @RevisionNumber
    private int id;

    @RevisionTimestamp
    @Column(name = "timestamp")
    private long timestamp;

    @Column(name = "username")
    private String username;
    @Column(name = "request_url")
    private String requestUrl;

    @Column(name = "action")
    private String action;
    @Column(name = "details")
    private String details;
    @Column(name = "description")
    private String description;

    @Transient
    public Date getRevisionDate() {
        return new Date(this.timestamp);
    }
}
