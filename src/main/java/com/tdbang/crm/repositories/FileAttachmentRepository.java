package com.tdbang.crm.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tdbang.crm.entities.FileAttachment;

@Repository
public interface FileAttachmentRepository extends JpaRepository<FileAttachment, Long> {
    List<FileAttachment> findByEntityTypeAndEntityFkAndIsActive(String entityType, Long entityFk, Boolean isActive);
}
