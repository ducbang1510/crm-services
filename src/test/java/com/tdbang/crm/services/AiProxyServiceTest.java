/*
 * Copyright © 2026 by tdbang.
 * All rights reserved.
 */

package com.tdbang.crm.services;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import com.tdbang.crm.dtos.AiChatResponseDTO;
import com.tdbang.crm.entities.User;
import com.tdbang.crm.exceptions.CRMException;
import com.tdbang.crm.repositories.UserRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AiProxyServiceTest {

    @Mock
    private RestClient aiAgentRestClient;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserService userService;

    private AiProxyService aiProxyService;

    @BeforeEach
    void setUp() {
        aiProxyService = new AiProxyService(aiAgentRestClient, userRepository, userService);
    }

    private User mockUser() {
        User user = new User();
        user.setPk(1L);
        user.setName("Admin");
        return user;
    }

    private RestClient.RequestBodySpec mockRestClientPost() {
        RestClient.RequestBodyUriSpec postSpec = mock(RestClient.RequestBodyUriSpec.class);
        RestClient.RequestBodySpec bodySpec = mock(RestClient.RequestBodySpec.class);

        when(aiAgentRestClient.post()).thenReturn(postSpec);
        when(postSpec.uri(anyString())).thenReturn(bodySpec);
        when(bodySpec.body(any(Map.class))).thenReturn(bodySpec);

        return bodySpec;
    }

    @Test
    void chat_success() {
        when(userRepository.findUserByPk(1L)).thenReturn(mockUser());
        when(userService.getUserRole(1L)).thenReturn(List.of("ADMIN", "USER"));

        AiChatResponseDTO expectedResponse = new AiChatResponseDTO();
        expectedResponse.setSessionId("s1");
        expectedResponse.setMessage("Pipeline summary: 5 won");
        expectedResponse.setToolsUsed(List.of("get_pipeline_summary"));

        RestClient.RequestBodySpec bodySpec = mockRestClientPost();
        RestClient.ResponseSpec responseSpec = mock(RestClient.ResponseSpec.class);
        when(bodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(AiChatResponseDTO.class)).thenReturn(expectedResponse);

        AiChatResponseDTO result = aiProxyService.chat("s1", 1L, "pipeline?");

        assertNotNull(result);
        assertEquals("s1", result.getSessionId());
        assertEquals("Pipeline summary: 5 won", result.getMessage());
    }

    @Test
    void chat_userNotFound_throws404() {
        when(userRepository.findUserByPk(999L)).thenReturn(null);

        CRMException ex = assertThrows(CRMException.class,
                () -> aiProxyService.chat("s1", 999L, "hello"));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
    }

    @Test
    void chat_agentDown_throws503() {
        when(userRepository.findUserByPk(1L)).thenReturn(mockUser());
        when(userService.getUserRole(1L)).thenReturn(List.of("ADMIN"));

        RestClient.RequestBodySpec bodySpec = mockRestClientPost();
        when(bodySpec.retrieve()).thenThrow(new ResourceAccessException("Connection refused"));

        CRMException ex = assertThrows(CRMException.class,
                () -> aiProxyService.chat("s1", 1L, "hello"));
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, ex.getStatus());
    }

    @Test
    void chat_agentError_forwardsStatus() {
        when(userRepository.findUserByPk(1L)).thenReturn(mockUser());
        when(userService.getUserRole(1L)).thenReturn(List.of("ADMIN"));

        RestClient.RequestBodySpec bodySpec = mockRestClientPost();
        when(bodySpec.retrieve()).thenThrow(
                new RestClientResponseException("Bad Request", HttpStatus.BAD_REQUEST.value(),
                        "Bad Request", null, null, null));

        CRMException ex = assertThrows(CRMException.class,
                () -> aiProxyService.chat("s1", 1L, "hello"));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
    }
}
