/*
 * Copyright © 2026 by tdbang.
 * All rights reserved.
 */

package com.tdbang.crm.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.tdbang.crm.dtos.NoteDTO;
import com.tdbang.crm.dtos.ResponseDTO;
import com.tdbang.crm.services.NoteService;
import com.tdbang.crm.services.SecurityService;
import com.tdbang.crm.services.UserService;
import com.tdbang.crm.utils.MessageConstants;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
    value = NoteController.class,
    excludeAutoConfiguration = {SecurityAutoConfiguration.class, SecurityFilterAutoConfiguration.class}
)
class NoteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private NoteService noteService;

    @MockitoBean
    private SecurityService securityService;

    @MockitoBean
    private UserService userService;

    @Test
    void createNote_withValidBody_returnsCreated() throws Exception {
        NoteDTO dto = buildNoteDTO();
        ResponseDTO response = new ResponseDTO(MessageConstants.SUCCESS_STATUS, MessageConstants.CREATING_NOTE_SUCCESS);

        when(securityService.getCurrentUsername()).thenReturn("testuser");
        when(userService.getUserPkByUsername("testuser")).thenReturn(1L);
        when(noteService.createNote(any(NoteDTO.class), anyLong())).thenReturn(response);

        mockMvc.perform(post("/api/v1/note")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isCreated());
    }

    @Test
    void createNote_withMissingRequiredField_returnsBadRequest() throws Exception {
        NoteDTO dto = new NoteDTO();

        mockMvc.perform(post("/api/v1/note")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void listNotes_withValidParams_returnsOk() throws Exception {
        ResponseDTO response = new ResponseDTO(MessageConstants.SUCCESS_STATUS, MessageConstants.FETCHING_LIST_OF_NOTES_SUCCESS);
        when(noteService.listNotes(anyString(), anyLong())).thenReturn(response);

        mockMvc.perform(get("/api/v1/note/list")
                .param("entityType", "CONTACT")
                .param("entityFk", "1"))
            .andExpect(status().isOk());
    }

    @Test
    void updateNote_withValidBody_returnsOk() throws Exception {
        NoteDTO dto = buildNoteDTO();
        ResponseDTO response = new ResponseDTO(MessageConstants.SUCCESS_STATUS, MessageConstants.UPDATING_NOTE_SUCCESS);

        when(securityService.getCurrentUsername()).thenReturn("testuser");
        when(userService.getUserPkByUsername("testuser")).thenReturn(1L);
        when(noteService.updateNote(anyLong(), any(NoteDTO.class), anyLong())).thenReturn(response);

        mockMvc.perform(put("/api/v1/note/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isOk());
    }

    @Test
    void deleteNote_returnsOk() throws Exception {
        ResponseDTO response = new ResponseDTO(MessageConstants.SUCCESS_STATUS, MessageConstants.DELETING_NOTE_SUCCESS);

        when(securityService.getCurrentUsername()).thenReturn("testuser");
        when(userService.getUserPkByUsername("testuser")).thenReturn(1L);
        when(noteService.deleteNote(anyLong(), anyLong())).thenReturn(response);

        mockMvc.perform(delete("/api/v1/note/1"))
            .andExpect(status().isOk());
    }

    @Test
    void listNoteTypes_returnsOk() throws Exception {
        mockMvc.perform(get("/api/v1/note/type"))
            .andExpect(status().isOk());
    }

    // --- Helper methods ---

    private NoteDTO buildNoteDTO() {
        NoteDTO dto = new NoteDTO();
        dto.setEntityType("CONTACT");
        dto.setEntityFk(1L);
        dto.setNoteType("Call");
        dto.setContent("Discussed pricing");
        return dto;
    }
}
