/*
 * Copyright © 2025 by tdbang.
 * All rights reserved.
 */

package com.tdbang.crm.repositories;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tdbang.crm.entities.EmailJob;
import com.tdbang.crm.enums.EmailEntityType;
import com.tdbang.crm.enums.EmailJobStatus;

@Repository
public interface EmailJobRepository extends JpaRepository<EmailJob, Long> {

    /**
     * Returns all email jobs for a given entity and status, most recent first.
     * Used by the email reminder processor to check if a SENT reminder already
     * exists for an entity before queuing another one.
     */
    List<EmailJob> findByEntityTypeAndEntityPkAndStatusOrderByCreatedOnDesc(
        EmailEntityType entityType, Long entityPk, EmailJobStatus status);

    /**
     * Checks whether a successful reminder was sent for the given entity
     * after {@code cutoffDate}. Used as an anti-spam guard.
     */
    boolean existsByEntityTypeAndEntityPkAndStatusAndCreatedOnAfter(
        EmailEntityType entityType, Long entityPk, EmailJobStatus status, Date cutoffDate);
}
