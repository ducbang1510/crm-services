/*
 * Copyright © 2026 by tdbang.
 * All rights reserved.
 */

package com.tdbang.crm.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.tdbang.crm.dtos.AiChatRequestDTO;
import com.tdbang.crm.dtos.AiChatResponseDTO;
import com.tdbang.crm.dtos.ResponseDTO;
import com.tdbang.crm.services.AiProxyService;
import com.tdbang.crm.utils.MessageConstants;

@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/ai")
@Tag(name = "AI Chat APIs")
public class AiProxyController extends BaseController {

    private final AiProxyService aiProxyService;

    @PostMapping("/chat")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER', 'STAFF')")
    public MappingJacksonValue chat(@RequestBody @Valid AiChatRequestDTO request) {
        log.info("Start AI chat: session={}", request.getSessionId());
        Long userPk = getPkUserLogged();

        AiChatResponseDTO response = aiProxyService.chat(
                request.getSessionId(), userPk, request.getMessage());

        ResponseDTO responseDTO = new ResponseDTO(
                MessageConstants.SUCCESS_STATUS, MessageConstants.AI_CHAT_SUCCESS, response);
        log.info("End AI chat: session={}", request.getSessionId());
        return new MappingJacksonValue(responseDTO);
    }
}
