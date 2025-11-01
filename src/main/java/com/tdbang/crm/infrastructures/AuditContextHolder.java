/*
 * Copyright © 2025 by tdbang.
 * All rights reserved.
 */

package com.tdbang.crm.infrastructures;

public class AuditContextHolder {

    private AuditContextHolder() {
        throw new IllegalStateException("Utility class");
    }

    private static final ThreadLocal<AuditLogInfo> currentAudit = new ThreadLocal<>();

    public static void setCurrentAudit(AuditLogInfo auditLogInfo) {
        currentAudit.set(auditLogInfo);
    }

    public static AuditLogInfo getCurrentAudit() {
        return currentAudit.get();
    }

    public static void clearCurrentAudit() {
        currentAudit.remove();
    }
}

