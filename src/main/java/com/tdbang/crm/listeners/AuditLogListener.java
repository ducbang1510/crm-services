/*
 * Copyright © 2025 by tdbang.
 * All rights reserved.
 */

package com.tdbang.crm.listeners;

import org.hibernate.envers.RevisionListener;

import com.tdbang.crm.entities.AuditLog;
import com.tdbang.crm.infrastructures.AuditContextHolder;
import com.tdbang.crm.infrastructures.AuditLogInfo;

public class AuditLogListener implements RevisionListener {

    @Override
    public void newRevision(Object revisionEntity) {
        AuditLogInfo current = AuditContextHolder.getCurrentAudit();
        AuditLog auditLog = (AuditLog) revisionEntity;
        auditLog.setUsername(current.getUsername());
        auditLog.setRequestUrl(current.getRequestUrl());
        auditLog.setAction(current.getAction());
        auditLog.setDescription(current.getDescription());
        auditLog.setDetails(current.getDetails());
    }
}
