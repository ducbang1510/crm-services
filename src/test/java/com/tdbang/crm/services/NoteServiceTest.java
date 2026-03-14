/*
 * Copyright © 2026 by tdbang.
 * All rights reserved.
 */

package com.tdbang.crm.services;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;

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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NoteServiceTest {

    @Mock
    private NoteRepository noteRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private NoteMapper noteMapper;

    private NoteService noteService;

    @BeforeEach
    void setUp() {
        noteService = new NoteService();
        ReflectionTestUtils.setField(noteService, "noteRepository", noteRepository);
        ReflectionTestUtils.setField(noteService, "userRepository", userRepository);
        ReflectionTestUtils.setField(noteService, "noteMapper", noteMapper);
    }

    @Test
    void createNote_validInput_returnsSuccess() {
        NoteDTO dto = buildNoteDTO("CONTACT", 1L, "Call", "Discussed pricing");
        User creator = buildUser(1L, "Admin");
        Note savedNote = buildNote(10L, creator);
        NoteDTO resultDto = new NoteDTO();
        resultDto.setPk(10L);

        when(userRepository.findUserByPk(1L)).thenReturn(creator);
        when(noteRepository.save(any(Note.class))).thenReturn(savedNote);
        when(noteMapper.mappingNoteEntityToNoteDTO(savedNote)).thenReturn(resultDto);

        ResponseDTO result = noteService.createNote(dto, 1L);

        assertNotNull(result);
        assertEquals(MessageConstants.SUCCESS_STATUS, result.getStatus());
        assertEquals(MessageConstants.CREATING_NOTE_SUCCESS, result.getMsg());
        assertNotNull(result.getData());
    }

    @Test
    void createNote_invalidEntityType_throwsException() {
        NoteDTO dto = buildNoteDTO("INVALID_TYPE", 1L, "Call", "Content");

        CRMException ex = assertThrows(CRMException.class,
            () -> noteService.createNote(dto, 1L));

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
    }

    @Test
    void listNotes_returnsOrderedList() {
        User creator = buildUser(1L, "Admin");
        Note note1 = buildNote(1L, creator);
        Note note2 = buildNote(2L, creator);
        List<Note> notes = List.of(note1, note2);
        NoteDTO dto1 = new NoteDTO();
        dto1.setPk(1L);
        NoteDTO dto2 = new NoteDTO();
        dto2.setPk(2L);

        when(noteRepository.findByEntityTypeAndEntityFkOrderByCreatedOnDesc("CONTACT", 1L))
            .thenReturn(notes);
        when(noteMapper.mappingToListNoteDTO(notes)).thenReturn(List.of(dto1, dto2));

        ResponseDTO result = noteService.listNotes("CONTACT", 1L);

        assertNotNull(result);
        assertEquals(MessageConstants.SUCCESS_STATUS, result.getStatus());
        assertEquals(MessageConstants.FETCHING_LIST_OF_NOTES_SUCCESS, result.getMsg());
        @SuppressWarnings("unchecked")
        List<NoteDTO> data = (List<NoteDTO>) result.getData();
        assertEquals(2, data.size());
    }

    @Test
    void updateNote_byCreator_succeeds() {
        User creator = buildUser(1L, "Admin");
        Note existingNote = buildNote(10L, creator);
        NoteDTO dto = new NoteDTO();
        dto.setNoteType("Meeting");
        dto.setContent("Updated content");
        Note updatedNote = buildNote(10L, creator);
        NoteDTO resultDto = new NoteDTO();
        resultDto.setPk(10L);

        when(noteRepository.findById(10L)).thenReturn(Optional.of(existingNote));
        when(noteRepository.save(any(Note.class))).thenReturn(updatedNote);
        when(noteMapper.mappingNoteEntityToNoteDTO(updatedNote)).thenReturn(resultDto);

        ResponseDTO result = noteService.updateNote(10L, dto, 1L);

        assertNotNull(result);
        assertEquals(MessageConstants.SUCCESS_STATUS, result.getStatus());
        assertEquals(MessageConstants.UPDATING_NOTE_SUCCESS, result.getMsg());
    }

    @Test
    void updateNote_byOtherUser_throwsException() {
        User creator = buildUser(1L, "Admin");
        Note existingNote = buildNote(10L, creator);

        when(noteRepository.findById(10L)).thenReturn(Optional.of(existingNote));

        CRMException ex = assertThrows(CRMException.class,
            () -> noteService.updateNote(10L, new NoteDTO(), 99L));

        assertEquals(HttpStatus.FORBIDDEN, ex.getStatus());
    }

    @Test
    void deleteNote_byCreator_succeeds() {
        User creator = buildUser(1L, "Admin");
        Note note = buildNote(10L, creator);

        when(noteRepository.findById(10L)).thenReturn(Optional.of(note));

        ResponseDTO result = noteService.deleteNote(10L, 1L);

        assertEquals(MessageConstants.SUCCESS_STATUS, result.getStatus());
        assertEquals(MessageConstants.DELETING_NOTE_SUCCESS, result.getMsg());
        verify(noteRepository, times(1)).delete(note);
    }

    @Test
    void deleteNote_byOtherUser_throwsForbidden() {
        User creator = buildUser(1L, "Admin");
        Note note = buildNote(10L, creator);

        when(noteRepository.findById(10L)).thenReturn(Optional.of(note));

        CRMException ex = assertThrows(CRMException.class,
            () -> noteService.deleteNote(10L, 99L));

        assertEquals(HttpStatus.FORBIDDEN, ex.getStatus());
    }

    @Test
    void deleteNote_notFound_throwsNotFound() {
        when(noteRepository.findById(10L)).thenReturn(Optional.empty());

        CRMException ex = assertThrows(CRMException.class,
            () -> noteService.deleteNote(10L, 1L));

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
    }

    // --- Helper methods ---

    private NoteDTO buildNoteDTO(String entityType, Long entityFk, String noteType, String content) {
        NoteDTO dto = new NoteDTO();
        dto.setEntityType(entityType);
        dto.setEntityFk(entityFk);
        dto.setNoteType(noteType);
        dto.setContent(content);
        return dto;
    }

    private User buildUser(Long pk, String name) {
        User user = new User();
        user.setPk(pk);
        user.setName(name);
        return user;
    }

    private Note buildNote(Long pk, User creator) {
        Note note = new Note();
        note.setPk(pk);
        note.setEntityType("CONTACT");
        note.setEntityFk(1L);
        note.setNoteType(NoteType.CALL);
        note.setContent("Test content");
        note.setCreatedBy(creator);
        return note;
    }
}
