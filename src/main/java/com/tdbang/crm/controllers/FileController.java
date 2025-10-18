/*
 * Copyright © 2025 by tdbang.
 * All rights reserved.
 */

package com.tdbang.crm.controllers;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.tdbang.crm.dtos.FileAttachmentDto;
import com.tdbang.crm.entities.FileAttachment;
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
    public MappingJacksonValue uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("entityType") String entityType,
            @RequestParam("entityFk") Long entityFk,
            @RequestParam(value = "description", required = false) String description) throws IOException {

        Long uploadedBy = getPkUserLogged(); // implement according to your security
        FileAttachmentDto dto = fileStorageService.store(file, entityType, entityFk, uploadedBy, description);
        return new MappingJacksonValue(dto);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyAuthority('STAFF')")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long id) throws IOException {
        FileAttachment meta = fileStorageService.findMetadata(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        GridFsResource resource = fileStorageService.getGridFsResource(meta.getMongoFileId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(meta.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + meta.getFileName() + "\"")
                .body(new InputStreamResource(resource.getInputStream()));
    }

    @GetMapping("/entity/{entityType}/{entityFk}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyAuthority('STAFF')")
    public MappingJacksonValue listFilesByEntity(
            @PathVariable String entityType,
            @PathVariable Long entityFk) {
        List<FileAttachmentDto> list = fileStorageService.listForEntity(entityType, entityFk)
                .stream().map(FileAttachmentDto::from).collect(Collectors.toList());
        return new MappingJacksonValue(list);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyAuthority('STAFF')")
    public void deleteFile(@PathVariable Long id) {
        fileStorageService.delete(id);
    }
}

