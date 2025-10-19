/*
 * Copyright © 2025 by tdbang.
 * All rights reserved.
 */

package com.tdbang.crm.dtos;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.tdbang.crm.entities.FileAttachment;

/**
 * Simple DTO for transferring file metadata to the UI or API clients.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FileAttachmentDto {

    private Long id;
    private String entityType;
    private Long entityFk;
    private String fileName;
    private String contentType;
    private Long size;
    private String mongoFileId;
    private Long uploadedBy;
    private Timestamp uploadedOn;
    private String description;
    private Boolean isActive;

    public static FileAttachmentDto from(FileAttachment entity) {
        return new FileAttachmentDto(
            entity.getPk(),
            entity.getEntityType(),
            entity.getEntityFk(),
            entity.getFileName(),
            entity.getContentType(),
            entity.getSize(),
            entity.getMongoFileId(),
            entity.getUploadedBy(),
            entity.getUploadedOn(),
            entity.getDescription(),
            entity.getIsActive()
        );
    }
}
