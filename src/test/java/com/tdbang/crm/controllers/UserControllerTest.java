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

import com.tdbang.crm.dtos.ChangePasswordRequestDTO;
import com.tdbang.crm.dtos.ResponseDTO;
import com.tdbang.crm.dtos.UpdateUserRequestDTO;
import com.tdbang.crm.dtos.UserDTO;
import com.tdbang.crm.services.SecurityService;
import com.tdbang.crm.services.UserService;
import com.tdbang.crm.utils.MessageConstants;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
    value = UserController.class,
    excludeAutoConfiguration = {SecurityAutoConfiguration.class, SecurityFilterAutoConfiguration.class}
)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private SecurityService securityService;

    @MockitoBean
    private UserService userService;

    @Test
    void retrieveUserProfile_returnsOk() throws Exception {
        ResponseDTO response = new ResponseDTO(MessageConstants.SUCCESS_STATUS, MessageConstants.FETCHING_USER_INFO_SUCCESS);

        when(securityService.getCurrentUsername()).thenReturn("testuser");
        when(userService.getUserPkByUsername("testuser")).thenReturn(1L);
        when(userService.getUserInfo(1L)).thenReturn(response);

        mockMvc.perform(get("/api/v1/user"))
            .andExpect(status().isOk());
    }

    @Test
    void changePassword_withValidBody_returnsOk() throws Exception {
        ChangePasswordRequestDTO dto = new ChangePasswordRequestDTO("oldPass123", "newPass123");
        ResponseDTO response = new ResponseDTO(MessageConstants.SUCCESS_STATUS, MessageConstants.CHANGING_USER_PASSWORD_SUCCESS);

        when(securityService.getCurrentUsername()).thenReturn("testuser");
        when(userService.getUserPkByUsername("testuser")).thenReturn(1L);
        when(userService.changePassword(anyLong(), any(ChangePasswordRequestDTO.class))).thenReturn(response);

        mockMvc.perform(put("/api/v1/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isOk());
    }

    @Test
    void changePassword_withMissingRequiredField_returnsBadRequest() throws Exception {
        ChangePasswordRequestDTO dto = new ChangePasswordRequestDTO();

        mockMvc.perform(put("/api/v1/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void createUser_withValidBody_returnsCreated() throws Exception {
        String body = "{\"name\":\"John Doe\",\"firstName\":\"John\",\"lastName\":\"Doe\","
            + "\"username\":\"johndoe\",\"password\":\"password123\","
            + "\"email\":\"john@example.com\",\"phone\":\"0123456789\"}";
        ResponseDTO response = new ResponseDTO(MessageConstants.SUCCESS_STATUS, MessageConstants.CREATING_NEW_USER_SUCCESS);

        when(userService.createNewUser(any(UserDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/user/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isCreated());
    }

    @Test
    void createUser_withMissingRequiredField_returnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/v1/user/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void retrieveUserInfo_withValidId_returnsOk() throws Exception {
        ResponseDTO response = new ResponseDTO(MessageConstants.SUCCESS_STATUS, MessageConstants.FETCHING_USER_INFO_SUCCESS);
        when(userService.getUserInfo(1L)).thenReturn(response);

        mockMvc.perform(get("/api/v1/user/1"))
            .andExpect(status().isOk());
    }

    @Test
    void editUser_withMatchingIds_returnsOk() throws Exception {
        UpdateUserRequestDTO dto = buildUpdateUserRequestDTO(1L);
        ResponseDTO response = new ResponseDTO(MessageConstants.SUCCESS_STATUS, MessageConstants.UPDATING_USER_SUCCESS);

        when(userService.editUser(anyLong(), any(UpdateUserRequestDTO.class))).thenReturn(response);

        mockMvc.perform(put("/api/v1/user/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isOk());
    }

    @Test
    void editUser_withMismatchedIds_returnsBadRequest() throws Exception {
        UpdateUserRequestDTO dto = buildUpdateUserRequestDTO(99L);

        mockMvc.perform(put("/api/v1/user/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void retrieveUserList_returnsOk() throws Exception {
        ResponseDTO response = new ResponseDTO(MessageConstants.SUCCESS_STATUS, MessageConstants.FETCHING_LIST_OF_USERS_SUCCESS);
        when(userService.getListOfUsers(isNull(), anyInt(), anyInt(), anyString(), anyString(), isNull()))
            .thenReturn(response);

        mockMvc.perform(get("/api/v1/user/list"))
            .andExpect(status().isOk());
    }

    @Test
    void retrieveListNameOfUsers_returnsOk() throws Exception {
        ResponseDTO response = new ResponseDTO(MessageConstants.SUCCESS_STATUS, MessageConstants.FETCHING_LIST_OF_NAMES_USERS_SUCCESS);
        when(userService.retrieveListNameOfUsers()).thenReturn(response);

        mockMvc.perform(get("/api/v1/user/list/name"))
            .andExpect(status().isOk());
    }

    @Test
    void retrieveUserRole_returnsOk() throws Exception {
        when(securityService.getCurrentUsername()).thenReturn("testuser");
        when(userService.getUserPkByUsername("testuser")).thenReturn(1L);
        when(userService.getUserRole(1L)).thenReturn(List.of("USER", "ADMIN"));

        mockMvc.perform(get("/api/v1/user/role"))
            .andExpect(status().isOk());
    }

    // --- Helper methods ---

    private UpdateUserRequestDTO buildUpdateUserRequestDTO(Long pk) {
        UpdateUserRequestDTO dto = new UpdateUserRequestDTO();
        dto.setPk(pk);
        dto.setName("Updated Name");
        dto.setFirstName("Updated");
        dto.setLastName("Name");
        dto.setEmail("updated@example.com");
        dto.setPhone("0987654321");
        return dto;
    }
}
