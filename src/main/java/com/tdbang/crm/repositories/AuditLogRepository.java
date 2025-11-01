/*
 * Copyright © 2025 by tdbang.
 * All rights reserved.
 */

package com.tdbang.crm.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tdbang.crm.entities.AuditLog;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
}
