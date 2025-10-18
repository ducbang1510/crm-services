package com.tdbang.crm.services;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.gridfs.model.GridFSFile;
import jakarta.persistence.EntityNotFoundException;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.tdbang.crm.dtos.FileAttachmentDto;
import com.tdbang.crm.entities.FileAttachment;
import com.tdbang.crm.repositories.FileAttachmentRepository;

@Service
public class FileStorageService {

    private final GridFsTemplate gridFsTemplate;
    private final FileAttachmentRepository fileAttachmentRepository;
    private final ObjectMapper objectMapper; // optional

    public FileStorageService(GridFsTemplate gridFsTemplate,
                              FileAttachmentRepository fileAttachmentRepository) {
        this.gridFsTemplate = gridFsTemplate;
        this.fileAttachmentRepository = fileAttachmentRepository;
        this.objectMapper = new ObjectMapper();
    }

    public FileAttachmentDto store(MultipartFile file,
                                   String entityType,
                                   Long entityFk,
                                   Long uploadedBy,
                                   String description) throws IOException {

        // Basic validation
        if (file.isEmpty()) throw new IllegalArgumentException("File is empty");
        if (file.getSize() > (10L * 1024 * 1024)) { // 10 MB example limit
            throw new IllegalArgumentException("File too large");
        }

        // Optional: sanitize filename
        String filename = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));

        // Prepare metadata for GridFS
        Document metadata = new Document();
        metadata.put("entityType", entityType);
        metadata.put("entityFk", entityFk);
        metadata.put("uploadedBy", uploadedBy);
        metadata.put("description", description);

        // Store in GridFS
        ObjectId gridFsId;
        try (InputStream inputStream = file.getInputStream()) {
            gridFsId = gridFsTemplate.store(inputStream, filename, file.getContentType(), metadata);
        }

        // Save metadata in MySQL
        FileAttachment entity = new FileAttachment();
        entity.setEntityType(entityType);
        entity.setEntityFk(entityFk);
        entity.setFileName(filename);
        entity.setContentType(file.getContentType());
        entity.setSize(file.getSize());
        entity.setMongoFileId(gridFsId.toHexString());
        entity.setUploadedBy(uploadedBy);
        entity.setDescription(description);
        entity.setIsActive(true);
        fileAttachmentRepository.save(entity);

        return FileAttachmentDto.from(entity);
    }

    public Optional<GridFsResource> getGridFsResource(String mongoFileId) {
        GridFSFile gridFsFile = gridFsTemplate.findOne(
                Query.query(Criteria.where("_id").is(new ObjectId(mongoFileId)))
        );
        if (gridFsFile == null) return Optional.empty();
        return Optional.of(gridFsTemplate.getResource(gridFsFile));
    }

    public Optional<FileAttachment> findMetadata(Long id) {
        return fileAttachmentRepository.findById(id).filter(FileAttachment::getIsActive);
    }

    public List<FileAttachment> listForEntity(String entityType, Long entityFk) {
        return fileAttachmentRepository.findByEntityTypeAndEntityFkAndIsActive(entityType, entityFk, true);
    }

    public void delete(Long id) {
        FileAttachment meta = fileAttachmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("File not found"));

        // Remove from GridFS
        gridFsTemplate.delete(Query.query(Criteria.where("_id").is(new ObjectId(meta.getMongoFileId()))));

        // Mark as inactive or delete row
        meta.setIsActive(false);
        fileAttachmentRepository.save(meta);
    }
}

