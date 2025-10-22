/*
 * Copyright © 2025 by tdbang.
 * All rights reserved.
 */

package com.tdbang.crm.services;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.mongodb.client.gridfs.model.GridFSUploadOptions;
import jakarta.persistence.EntityNotFoundException;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.tdbang.crm.dtos.FileAttachmentDto;
import com.tdbang.crm.entities.FileAttachment;
import com.tdbang.crm.repositories.FileAttachmentRepository;

@Service
public class FileStorageService {

    private final MongoDatabaseFactory mongoDatabaseFactory;
    private final FileAttachmentRepository fileAttachmentRepository;

    @Autowired
    public FileStorageService(MongoDatabaseFactory mongoDatabaseFactory,
                              FileAttachmentRepository fileAttachmentRepository) {
        this.mongoDatabaseFactory = mongoDatabaseFactory;
        this.fileAttachmentRepository = fileAttachmentRepository;
    }

    private GridFSBucket getBucket(String collectionName) {
        // Use collection name (not entity name)
        return GridFSBuckets.create(
            mongoDatabaseFactory.getMongoDatabase(),
            collectionName
        );
    }

    /**
     * Upload and store file in MongoDB GridFS and MySQL metadata table
     */
    public FileAttachmentDto uploadFile(MultipartFile file, String entityType, Long entityFk, String collectionName, Long uploadedBy, String description) throws IOException {
        GridFSBucket bucket = getBucket(collectionName);
        String filename = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        Document metadata = new Document();
        metadata.put("entityType", entityType);
        metadata.put("entityFk", entityFk);
        metadata.put("uploadedBy", uploadedBy);
        metadata.put("uploadedOn", new Date());
        metadata.put("contentType", file.getContentType());
        metadata.put("description", description);

        try (InputStream inputStream = file.getInputStream()) {
            ObjectId fileId = bucket.uploadFromStream(filename, inputStream, new GridFSUploadOptions().metadata(metadata));

            // Save metadata in MySQL
            FileAttachment attachment = new FileAttachment();
            attachment.setEntityType(entityType);
            attachment.setEntityFk(entityFk);
            attachment.setCollectionName(collectionName);
            attachment.setMongoFileId(fileId.toHexString());
            attachment.setFileName(filename);
            attachment.setContentType(file.getContentType());
            attachment.setSize(file.getSize());
            attachment.setUploadedBy(uploadedBy);
            attachment.setUploadedOn(new Timestamp(System.currentTimeMillis()));
            attachment.setDescription(description);
            attachment.setIsActive(true);
            fileAttachmentRepository.save(attachment);

            return FileAttachmentDto.from(attachment);
        }
    }

    /**
     * Retrieve file metadata (GridFSFile)
     */
    public GridFSFile getFile(String collectionName, String fileId) {
        GridFSBucket bucket = getBucket(collectionName);
        return bucket.find(new Document("_id", new ObjectId(fileId))).first();
    }

    /**
     * Download file content into an OutputStream
     */
    public void downloadFile(String collectionName, String fileId, OutputStream outputStream) {
        GridFSBucket bucket = getBucket(collectionName);
        bucket.downloadToStream(new ObjectId(fileId), outputStream);
    }

    /**
     * Get metadata list from MySQL
     */
    public List<FileAttachment> listFileAttachments(String entityType, Long entityFk) {
        return fileAttachmentRepository.findByEntityTypeAndEntityFkAndIsActive(entityType, entityFk, true);
    }

    /**
     * Delete both MongoDB and inactive MySQL records
     */
    public void deleteFile(Long pk) {
        FileAttachment fileAttachment = fileAttachmentRepository.findById(pk)
            .orElseThrow(() -> new EntityNotFoundException("File not found"));
        GridFSBucket bucket = getBucket(fileAttachment.getCollectionName());
        bucket.delete(new ObjectId(fileAttachment.getMongoFileId()));

        fileAttachment.setIsActive(false);
        fileAttachmentRepository.save(fileAttachment);
    }
}
