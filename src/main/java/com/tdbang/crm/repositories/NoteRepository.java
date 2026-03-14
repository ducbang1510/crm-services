/*
 * Copyright © 2026 by tdbang.
 * All rights reserved.
 */

package com.tdbang.crm.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tdbang.crm.entities.Note;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {

    List<Note> findByEntityTypeAndEntityFkOrderByCreatedOnDesc(String entityType, Long entityFk);
}
