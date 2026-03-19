/*
 * Copyright © 2026 by tdbang.
 * All rights reserved.
 */

package com.tdbang.crm.controllers;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.tdbang.crm.config.ApiKeyAuthFilter;
import com.tdbang.crm.dtos.AiChatResponseDTO;
import com.tdbang.crm.services.AiProxyService;
import com.tdbang.crm.services.SecurityService;
import com.tdbang.crm.services.UserService;
import com.tdbang.crm.utils.MessageConstants;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
    value = AiProxyController.class,
    excludeAutoConfiguration = {SecurityAutoConfiguration.class, SecurityFilterAutoConfiguration.class},
    excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = ApiKeyAuthFilter.class)
)
class AiProxyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AiProxyService aiProxyService;

    @MockitoBean
    private SecurityService securityService;

    @MockitoBean
    private UserService userService;

    @Test
    void chat_returnsOk() throws Exception {
        when(securityService.getCurrentUsername()).thenReturn("admin");
        when(userService.getUserPkByUsername("admin")).thenReturn(1L);

        AiChatResponseDTO agentResponse = new AiChatResponseDTO();
        agentResponse.setSessionId("s1");
        agentResponse.setMessage("Pipeline looks good");
        agentResponse.setModel("qwen3:8b");
        agentResponse.setToolsUsed(List.of("get_pipeline_summary"));

        when(aiProxyService.chat(anyString(), anyLong(), anyString())).thenReturn(agentResponse);

        mockMvc.perform(post("/api/v1/ai/chat")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"sessionId\":\"s1\",\"message\":\"pipeline status?\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value(MessageConstants.SUCCESS_STATUS))
            .andExpect(jsonPath("$.msg").value(MessageConstants.AI_CHAT_SUCCESS))
            .andExpect(jsonPath("$.data.sessionId").value("s1"))
            .andExpect(jsonPath("$.data.message").value("Pipeline looks good"));
    }

    @Test
    void chat_withBlankMessage_returnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/v1/ai/chat")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"sessionId\":\"s1\",\"message\":\"\"}"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void chat_withMissingSessionId_returnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/v1/ai/chat")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"message\":\"hello\"}"))
            .andExpect(status().isBadRequest());
    }
}
