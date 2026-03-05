/*
 * Copyright © 2025 by tdbang.
 * All rights reserved.
 */

package com.tdbang.crm.controllers;

import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.tdbang.crm.dtos.ContactDTO;
import com.tdbang.crm.dtos.ResponseDTO;
import com.tdbang.crm.enums.LeadSource;
import com.tdbang.crm.enums.Salutation;
import com.tdbang.crm.services.ContactService;
import com.tdbang.crm.services.SecurityService;
import com.tdbang.crm.services.UserService;
import com.tdbang.crm.utils.MessageConstants;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
    value = ContactController.class,
    excludeAutoConfiguration = {SecurityAutoConfiguration.class, SecurityFilterAutoConfiguration.class}
)
class ContactControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ContactService contactService;

    @MockitoBean
    private SecurityService securityService;

    @MockitoBean
    private UserService userService;

    @Test
    void createContact_withValidBody_returnsCreated() throws Exception {
        ContactDTO dto = buildContactDTO();
        ResponseDTO response = new ResponseDTO(MessageConstants.SUCCESS_STATUS, MessageConstants.CREATING_NEW_CONTACT_SUCCESS);

        when(securityService.getCurrentUsername()).thenReturn("testuser");
        when(userService.getUserPkByUsername("testuser")).thenReturn(1L);
        when(contactService.createNewContact(any(ContactDTO.class), anyLong())).thenReturn(response);

        mockMvc.perform(post("/api/v1/contact")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isCreated());
    }

    @Test
    void createContact_withMissingRequiredField_returnsBadRequest() throws Exception {
        ContactDTO dto = new ContactDTO();

        mockMvc.perform(post("/api/v1/contact")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void retrieveContactDetails_withValidId_returnsOk() throws Exception {
        ResponseDTO response = new ResponseDTO(MessageConstants.SUCCESS_STATUS, MessageConstants.FETCHING_CONTACT_SUCCESS);
        when(contactService.getContactDetails(1L)).thenReturn(response);

        mockMvc.perform(get("/api/v1/contact/1"))
            .andExpect(status().isOk());
    }

    @Test
    void updateContactDetails_withValidBody_returnsOk() throws Exception {
        ContactDTO dto = buildContactDTO();
        ResponseDTO response = new ResponseDTO(MessageConstants.SUCCESS_STATUS, MessageConstants.UPDATING_CONTACT_SUCCESS);

        when(securityService.getCurrentUsername()).thenReturn("testuser");
        when(userService.getUserPkByUsername("testuser")).thenReturn(1L);
        when(contactService.updateContactDetails(anyLong(), anyLong(), any(ContactDTO.class))).thenReturn(response);

        mockMvc.perform(put("/api/v1/contact/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isOk());
    }

    @Test
    void deleteContactDetails_returnsOk() throws Exception {
        ResponseDTO response = new ResponseDTO(MessageConstants.SUCCESS_STATUS, MessageConstants.DELETING_CONTACT_SUCCESS);

        when(securityService.getCurrentUsername()).thenReturn("testuser");
        when(userService.getUserPkByUsername("testuser")).thenReturn(1L);
        when(contactService.deleteContactDetails(anyLong(), anyLong())).thenReturn(response);

        mockMvc.perform(delete("/api/v1/contact/1"))
            .andExpect(status().isOk());
    }

    @Test
    void retrieveContactList_returnsOk() throws Exception {
        ResponseDTO response = new ResponseDTO(MessageConstants.SUCCESS_STATUS, MessageConstants.FETCHING_LIST_OF_CONTACTS_SUCCESS);
        when(contactService.getListOfContact(isNull(), anyInt(), anyInt(), anyString(), anyString(), isNull()))
            .thenReturn(response);

        mockMvc.perform(get("/api/v1/contact/list"))
            .andExpect(status().isOk());
    }

    @Test
    void retrieveContactNameList_returnsOk() throws Exception {
        ResponseDTO response = new ResponseDTO(MessageConstants.SUCCESS_STATUS, MessageConstants.FETCHING_LIST_OF_CONTACTS_SUCCESS);
        when(contactService.getListOfContactName()).thenReturn(response);

        mockMvc.perform(get("/api/v1/contact/list/contact-name"))
            .andExpect(status().isOk());
    }

    @Test
    void deleteContacts_returnsOk() throws Exception {
        List<Long> ids = List.of(1L, 2L);
        ResponseDTO response = new ResponseDTO(MessageConstants.SUCCESS_STATUS, MessageConstants.DELETING_LIST_OF_CONTACTS_SUCCESS);

        when(securityService.getCurrentUsername()).thenReturn("testuser");
        when(userService.getUserPkByUsername("testuser")).thenReturn(1L);
        when(contactService.deleteContacts(anyList(), anyLong())).thenReturn(response);

        mockMvc.perform(post("/api/v1/contact/delete")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ids)))
            .andExpect(status().isOk());
    }

    @Test
    void retrieveSalutationEnum_returnsOk() throws Exception {
        when(contactService.retrieveSalutationEnumOfContact())
            .thenReturn(List.of(Salutation.values()));

        mockMvc.perform(get("/api/v1/contact/salutation"))
            .andExpect(status().isOk());
    }

    @Test
    void retrieveLeadSourceEnum_returnsOk() throws Exception {
        when(contactService.retrieveLeadSourceEnumOfContact())
            .thenReturn(List.of(LeadSource.values()));

        mockMvc.perform(get("/api/v1/contact/lead-source"))
            .andExpect(status().isOk());
    }

    @Test
    void retrieveContactDashboardByLeadSource_returnsOk() throws Exception {
        ResponseDTO response = new ResponseDTO(MessageConstants.SUCCESS_STATUS, MessageConstants.COUNTING_NO_CONTACTS_BY_LEAD_SRC_SUCCESS);
        when(contactService.retrieveContactDashboardByLeadSource()).thenReturn(response);

        mockMvc.perform(get("/api/v1/contact/count/lead-source"))
            .andExpect(status().isOk());
    }

    // --- Helper methods ---

    private ContactDTO buildContactDTO() {
        ContactDTO dto = new ContactDTO();
        dto.setContactName("John Doe");
        dto.setSalutation("Mr.");
        dto.setOrganization("OrgA");
        dto.setAssignedTo("AssignedUser");
        return dto;
    }
}
