/*
 * Copyright © 2025 by tdbang.
 * All rights reserved.
 */

package com.tdbang.crm.controllers;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import com.mongodb.client.gridfs.model.GridFSFile;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.tdbang.crm.dtos.FileAttachmentDto;
import com.tdbang.crm.enums.CollectionType;
import com.tdbang.crm.enums.EntityType;
import com.tdbang.crm.services.FileStorageService;

@RestController
@RequestMapping("/api/v1/file")
public class FileController extends BaseController {

    private final FileStorageService fileStorageService;

    public FileController(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    @PostMapping("/upload")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyAuthority('STAFF')")
    public MappingJacksonValue uploadFile(@RequestParam EntityType entityType,
                                          @RequestParam Long entityFk,
                                          @RequestParam MultipartFile file,
                                          @RequestParam CollectionType collectionType,
                                          @RequestParam(required = false) String description) throws IOException {

        Long uploadedBy = getPkUserLogged();
        FileAttachmentDto dto = fileStorageService.uploadFile(file, entityType.getName(), entityFk, collectionType.getName(), uploadedBy, description);
        return new MappingJacksonValue(dto);
    }

    @GetMapping("/download")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyAuthority('STAFF')")
    public ResponseEntity<byte[]> downloadFile(@RequestParam CollectionType collectionType,
                                               @RequestParam String fileId) {
        String collectionName = collectionType.getName();
        GridFSFile gridFSFile = fileStorageService.getFile(collectionName, fileId);
        if (gridFSFile == null) {
            return ResponseEntity.notFound().build();
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        fileStorageService.downloadFile(collectionName, fileId, outputStream);

        String filename = gridFSFile.getFilename();
        String contentType = gridFSFile.getMetadata() != null
            ? gridFSFile.getMetadata().getString("contentType")
            : "application/octet-stream";

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
            .contentType(MediaType.parseMediaType(contentType))
            .body(outputStream.toByteArray());
    }

    @GetMapping("/list")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyAuthority('STAFF')")
    public MappingJacksonValue listFileAttachments(@RequestParam EntityType entityType,
                                                   @RequestParam Long entityFk) {
        List<FileAttachmentDto> list = fileStorageService.listFileAttachments(entityType.getName(), entityFk)
            .stream().map(FileAttachmentDto::from).toList();
        return new MappingJacksonValue(list);
    }

    @DeleteMapping("/delete")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyAuthority('STAFF')")
    public void deleteFile(@RequestParam Long pk) {
        fileStorageService.deleteFile(pk);
    }
}

