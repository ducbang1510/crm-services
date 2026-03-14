/*
 * Copyright © 2026 by tdbang.
 * All rights reserved.
 */

package com.tdbang.crm.services;

import java.util.List;
import java.util.Set;

import jakarta.transaction.Transactional;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.tdbang.crm.dtos.NoteDTO;
import com.tdbang.crm.dtos.ResponseDTO;
import com.tdbang.crm.entities.Note;
import com.tdbang.crm.entities.User;
import com.tdbang.crm.enums.NoteType;
import com.tdbang.crm.exceptions.CRMException;
import com.tdbang.crm.mappers.NoteMapper;
import com.tdbang.crm.repositories.NoteRepository;
import com.tdbang.crm.repositories.UserRepository;
import com.tdbang.crm.utils.MessageConstants;

@Log4j2
@Service
public class NoteService {

    private static final Set<String> VALID_ENTITY_TYPES = Set.of("CONTACT", "SALES_ORDER");

    @Autowired
    private NoteRepository noteRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private NoteMapper noteMapper;

    @Transactional
    public ResponseDTO createNote(NoteDTO dto, Long creatorPk) {
        try {
            validateEntityType(dto.getEntityType());
            NoteType noteType = NoteType.fromName(dto.getNoteType());

            User creator = userRepository.findUserByPk(creatorPk);

            Note note = new Note();
            note.setEntityType(dto.getEntityType().toUpperCase());
            note.setEntityFk(dto.getEntityFk());
            note.setNoteType(noteType);
            note.setContent(dto.getContent());
            note.setCreatedBy(creator);

            Note savedNote = noteRepository.save(note);

            NoteDTO resultDto = noteMapper.mappingNoteEntityToNoteDTO(savedNote);
            return new ResponseDTO(MessageConstants.SUCCESS_STATUS, MessageConstants.CREATING_NOTE_SUCCESS, resultDto);
        } catch (CRMException e) {
            throw e;
        } catch (Exception e) {
            throw new CRMException(HttpStatus.BAD_REQUEST, MessageConstants.BAD_REQUEST_CODE, MessageConstants.CREATING_NOTE_ERROR, e.getMessage());
        }
    }

    public ResponseDTO listNotes(String entityType, Long entityFk) {
        try {
            validateEntityType(entityType);
            List<Note> notes = noteRepository.findByEntityTypeAndEntityFkOrderByCreatedOnDesc(entityType.toUpperCase(), entityFk);
            List<NoteDTO> noteDTOs = noteMapper.mappingToListNoteDTO(notes);
            return new ResponseDTO(MessageConstants.SUCCESS_STATUS, MessageConstants.FETCHING_LIST_OF_NOTES_SUCCESS, noteDTOs);
        } catch (CRMException e) {
            throw e;
        } catch (Exception e) {
            throw new CRMException(HttpStatus.INTERNAL_SERVER_ERROR,
                MessageConstants.INTERNAL_ERROR_CODE, MessageConstants.FETCHING_LIST_OF_NOTES_ERROR, e.getMessage());
        }
    }

    @Transactional
    public ResponseDTO updateNote(Long pk, NoteDTO dto, Long updaterPk) {
        Note note = noteRepository.findById(pk)
            .orElseThrow(() -> new CRMException(HttpStatus.NOT_FOUND, MessageConstants.NOT_FOUND_CODE, MessageConstants.NOT_FOUND_MESSAGE));

        if (!note.getCreatedBy().getPk().equals(updaterPk)) {
            throw new CRMException(HttpStatus.FORBIDDEN, MessageConstants.FORBIDDEN_CODE, MessageConstants.FORBIDDEN_MESSAGE);
        }

        try {
            if (dto.getNoteType() != null) {
                note.setNoteType(NoteType.fromName(dto.getNoteType()));
            }
            if (dto.getContent() != null) {
                note.setContent(dto.getContent());
            }

            Note updatedNote = noteRepository.save(note);
            NoteDTO resultDto = noteMapper.mappingNoteEntityToNoteDTO(updatedNote);
            return new ResponseDTO(MessageConstants.SUCCESS_STATUS, MessageConstants.UPDATING_NOTE_SUCCESS, resultDto);
        } catch (CRMException e) {
            throw e;
        } catch (Exception e) {
            throw new CRMException(HttpStatus.BAD_REQUEST, MessageConstants.BAD_REQUEST_CODE, MessageConstants.UPDATING_NOTE_ERROR, e.getMessage());
        }
    }

    @Transactional
    public ResponseDTO deleteNote(Long pk, Long deleterPk) {
        Note note = noteRepository.findById(pk)
            .orElseThrow(() -> new CRMException(HttpStatus.NOT_FOUND, MessageConstants.NOT_FOUND_CODE, MessageConstants.NOT_FOUND_MESSAGE));

        if (!note.getCreatedBy().getPk().equals(deleterPk)) {
            throw new CRMException(HttpStatus.FORBIDDEN, MessageConstants.FORBIDDEN_CODE, MessageConstants.FORBIDDEN_MESSAGE);
        }

        noteRepository.delete(note);
        return new ResponseDTO(MessageConstants.SUCCESS_STATUS, MessageConstants.DELETING_NOTE_SUCCESS);
    }

    private void validateEntityType(String entityType) {
        if (entityType == null || !VALID_ENTITY_TYPES.contains(entityType.toUpperCase())) {
            throw new CRMException(HttpStatus.BAD_REQUEST, MessageConstants.BAD_REQUEST_CODE,
                "Invalid entity type. Must be one of: " + VALID_ENTITY_TYPES);
        }
    }
}
