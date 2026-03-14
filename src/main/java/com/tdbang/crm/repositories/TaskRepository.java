/*
 * Copyright © 2026 by tdbang.
 * All rights reserved.
 */

package com.tdbang.crm.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tdbang.crm.entities.Task;
import com.tdbang.crm.enums.TaskStatus;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    Optional<Task> findByPk(Long pk);

    Page<Task> findByAssignedToPkOrderByDueDateAsc(Long userPk, Pageable pageable);

    List<Task> findByEntityTypeAndEntityFkOrderByCreatedOnDesc(String entityType, Long entityFk);

    long countByAssignedToPkAndStatus(Long userPk, TaskStatus status);
}
