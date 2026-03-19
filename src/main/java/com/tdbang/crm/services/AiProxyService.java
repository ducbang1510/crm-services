/*
 * Copyright © 2026 by tdbang.
 * All rights reserved.
 */

package com.tdbang.crm.services;

import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import com.tdbang.crm.dtos.AiChatResponseDTO;
import com.tdbang.crm.entities.User;
import com.tdbang.crm.exceptions.CRMException;
import com.tdbang.crm.repositories.UserRepository;
import com.tdbang.crm.utils.MessageConstants;

@Log4j2
@Service
@RequiredArgsConstructor
public class AiProxyService {

    private final RestClient aiAgentRestClient;
    private final UserRepository userRepository;
    private final UserService userService;

    public AiChatResponseDTO chat(String sessionId, Long userPk, String message) {
        User user = userRepository.findUserByPk(userPk);
        if (user == null) {
            throw new CRMException(HttpStatus.NOT_FOUND, MessageConstants.NOT_FOUND_CODE,
                    "User not found");
        }

        List<String> roles = userService.getUserRole(userPk);

        Map<String, Object> payload = Map.of(
                "session_id", sessionId,
                "user_pk", userPk,
                "user_name", user.getName(),
                "user_roles", roles,
                "message", message
        );

        try {
            log.info("Proxying chat to AI agent: session={}, user={}", sessionId, userPk);

            AiChatResponseDTO response = aiAgentRestClient.post()
                    .uri("/api/v1/chat")
                    .body(payload)
                    .retrieve()
                    .body(AiChatResponseDTO.class);

            log.info("AI agent responded: session={}, tools={}", sessionId,
                    response != null ? response.getToolsUsed() : "null");

            return response;

        } catch (ResourceAccessException e) {
            log.error("AI agent unreachable: {}", e.getMessage());
            throw new CRMException(HttpStatus.SERVICE_UNAVAILABLE, MessageConstants.ERROR_CODE,
                    MessageConstants.AI_AGENT_UNAVAILABLE);
        } catch (RestClientResponseException e) {
            log.error("AI agent error: status={}, body={}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new CRMException(HttpStatus.valueOf(e.getStatusCode().value()), MessageConstants.ERROR_CODE,
                    MessageConstants.AI_CHAT_ERROR);
        }
    }
}
