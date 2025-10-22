/*
 * Copyright © 2025 by tdbang.
 * All rights reserved.
 */

package com.tdbang.crm.entities;

import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

@Getter
@Setter
@RequiredArgsConstructor
@Entity
@Table(name = "file_attachment")
public class FileAttachment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pk;

    @Column(name = "entity_type")
    private String entityType;
    @Column(name = "entity_fk")
    private Long entityFk;
    @Column(name = "collection_name")
    private String collectionName;
    @Column(name = "mongo_file_id")
    private String mongoFileId;
    @Column(name = "file_name")
    private String fileName;
    @Column(name = "content_type")
    private String contentType;
    @Column(name = "size")
    private Long size;
    @Column(name = "uploaded_by")
    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    private Long uploadedBy;
    @Column(name = "uploaded_on")
    private Timestamp uploadedOn;
    @Column(name = "description")
    private String description;
    @Column(name = "is_active")
    private Boolean isActive = true;
}
