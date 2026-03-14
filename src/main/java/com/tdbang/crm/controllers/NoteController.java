/*
 * Copyright © 2026 by tdbang.
 * All rights reserved.
 */

package com.tdbang.crm.controllers;

import java.util.Arrays;
import java.util.List;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.tdbang.crm.commons.AuditAction;
import com.tdbang.crm.dtos.NoteDTO;
import com.tdbang.crm.dtos.ResponseDTO;
import com.tdbang.crm.enums.NoteType;
import com.tdbang.crm.services.NoteService;

@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/note")
@Tag(name = "CRM Note APIs")
public class NoteController extends BaseController {

    private final NoteService noteService;

    @PostMapping("")
    @AuditAction(value = "CREATE_NOTE", description = "Create new note")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyAuthority('STAFF')")
    public MappingJacksonValue createNote(@RequestBody @Valid NoteDTO noteDTO) {
        log.info("Start createNote");
        ResponseDTO responseDTO = noteService.createNote(noteDTO, getPkUserLogged());
        log.info("End createNote");
        return new MappingJacksonValue(responseDTO);
    }

    @GetMapping("/list")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyAuthority('STAFF')")
    public MappingJacksonValue listNotes(@RequestParam String entityType, @RequestParam Long entityFk) {
        log.info("Start listNotes");
        ResponseDTO responseDTO = noteService.listNotes(entityType, entityFk);
        log.info("End listNotes");
        return new MappingJacksonValue(responseDTO);
    }

    @PutMapping("/{id}")
    @AuditAction(value = "UPDATE_NOTE", description = "Update existing note")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyAuthority('STAFF')")
    public MappingJacksonValue updateNote(@PathVariable Long id, @RequestBody @Valid NoteDTO noteDTO) {
        log.info("Start updateNote");
        ResponseDTO responseDTO = noteService.updateNote(id, noteDTO, getPkUserLogged());
        log.info("End updateNote");
        return new MappingJacksonValue(responseDTO);
    }

    @DeleteMapping("/{id}")
    @AuditAction(value = "DELETE_NOTE", description = "Delete existing note")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyAuthority('STAFF')")
    public MappingJacksonValue deleteNote(@PathVariable Long id) {
        log.info("Start deleteNote");
        ResponseDTO responseDTO = noteService.deleteNote(id, getPkUserLogged());
        log.info("End deleteNote");
        return new MappingJacksonValue(responseDTO);
    }

    @GetMapping("/type")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyAuthority('STAFF')")
    public MappingJacksonValue listNoteTypes() {
        log.info("Start listNoteTypes");
        List<NoteType> noteTypes = Arrays.stream(NoteType.values()).toList();
        log.info("End listNoteTypes");
        return new MappingJacksonValue(noteTypes);
    }
}
