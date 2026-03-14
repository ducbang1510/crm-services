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

import com.tdbang.crm.dtos.ResponseDTO;
import com.tdbang.crm.dtos.TaskDTO;
import com.tdbang.crm.services.SecurityService;
import com.tdbang.crm.services.TaskService;
import com.tdbang.crm.services.UserService;
import com.tdbang.crm.utils.MessageConstants;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
    value = TaskController.class,
    excludeAutoConfiguration = {SecurityAutoConfiguration.class, SecurityFilterAutoConfiguration.class}
)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TaskService taskService;

    @MockitoBean
    private SecurityService securityService;

    @MockitoBean
    private UserService userService;

    @Test
    void createTask_withValidBody_returnsCreated() throws Exception {
        TaskDTO dto = buildTaskDTO();
        ResponseDTO response = new ResponseDTO(MessageConstants.SUCCESS_STATUS, MessageConstants.CREATING_TASK_SUCCESS);

        when(securityService.getCurrentUsername()).thenReturn("testuser");
        when(userService.getUserPkByUsername("testuser")).thenReturn(1L);
        when(taskService.createTask(any(TaskDTO.class), anyLong())).thenReturn(response);

        mockMvc.perform(post("/api/v1/task")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isCreated());
    }

    @Test
    void createTask_withMissingRequiredField_returnsBadRequest() throws Exception {
        TaskDTO dto = new TaskDTO();

        mockMvc.perform(post("/api/v1/task")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void getTaskDetails_returnsOk() throws Exception {
        ResponseDTO response = new ResponseDTO(MessageConstants.SUCCESS_STATUS, MessageConstants.FETCHING_TASK_SUCCESS);
        when(taskService.getTaskDetails(1L)).thenReturn(response);

        mockMvc.perform(get("/api/v1/task/1"))
            .andExpect(status().isOk());
    }

    @Test
    void updateTask_withValidBody_returnsOk() throws Exception {
        TaskDTO dto = buildTaskDTO();
        ResponseDTO response = new ResponseDTO(MessageConstants.SUCCESS_STATUS, MessageConstants.UPDATING_TASK_SUCCESS);

        when(securityService.getCurrentUsername()).thenReturn("testuser");
        when(userService.getUserPkByUsername("testuser")).thenReturn(1L);
        when(taskService.updateTask(anyLong(), any(TaskDTO.class), anyLong())).thenReturn(response);

        mockMvc.perform(put("/api/v1/task/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isOk());
    }

    @Test
    void deleteTask_returnsOk() throws Exception {
        ResponseDTO response = new ResponseDTO(MessageConstants.SUCCESS_STATUS, MessageConstants.DELETING_TASK_SUCCESS);

        when(securityService.getCurrentUsername()).thenReturn("testuser");
        when(userService.getUserPkByUsername("testuser")).thenReturn(1L);
        when(taskService.deleteTask(anyLong(), anyLong())).thenReturn(response);

        mockMvc.perform(delete("/api/v1/task/1"))
            .andExpect(status().isOk());
    }

    @Test
    void listMyTasks_returnsOk() throws Exception {
        ResponseDTO response = new ResponseDTO(MessageConstants.SUCCESS_STATUS, MessageConstants.FETCHING_LIST_OF_TASKS_SUCCESS);

        when(securityService.getCurrentUsername()).thenReturn("testuser");
        when(userService.getUserPkByUsername("testuser")).thenReturn(1L);
        when(taskService.listMyTasks(anyLong(), anyInt(), anyInt())).thenReturn(response);

        mockMvc.perform(get("/api/v1/task/my")
                .param("pageNumber", "0")
                .param("pageSize", "10"))
            .andExpect(status().isOk());
    }

    @Test
    void listTasksByEntity_returnsOk() throws Exception {
        ResponseDTO response = new ResponseDTO(MessageConstants.SUCCESS_STATUS, MessageConstants.FETCHING_LIST_OF_TASKS_SUCCESS);
        when(taskService.listTasksByEntity(anyString(), anyLong())).thenReturn(response);

        mockMvc.perform(get("/api/v1/task/list")
                .param("entityType", "CONTACT")
                .param("entityFk", "1"))
            .andExpect(status().isOk());
    }

    @Test
    void getTaskSummary_returnsOk() throws Exception {
        ResponseDTO response = new ResponseDTO(MessageConstants.SUCCESS_STATUS, MessageConstants.FETCHING_TASK_SUMMARY_SUCCESS);

        when(securityService.getCurrentUsername()).thenReturn("testuser");
        when(userService.getUserPkByUsername("testuser")).thenReturn(1L);
        when(taskService.getTaskSummary(anyLong())).thenReturn(response);

        mockMvc.perform(get("/api/v1/task/summary"))
            .andExpect(status().isOk());
    }

    @Test
    void listTaskTypes_returnsOk() throws Exception {
        mockMvc.perform(get("/api/v1/task/type"))
            .andExpect(status().isOk());
    }

    @Test
    void listTaskStatuses_returnsOk() throws Exception {
        mockMvc.perform(get("/api/v1/task/status"))
            .andExpect(status().isOk());
    }

    @Test
    void listTaskPriorities_returnsOk() throws Exception {
        mockMvc.perform(get("/api/v1/task/priority"))
            .andExpect(status().isOk());
    }

    // --- Helper methods ---

    private TaskDTO buildTaskDTO() {
        TaskDTO dto = new TaskDTO();
        dto.setTitle("Follow up with client");
        dto.setTaskType("Todo");
        dto.setEntityType("CONTACT");
        dto.setEntityFk(1L);
        dto.setAssignedToUserFk(1L);
        dto.setPriority("Medium");
        return dto;
    }
}
