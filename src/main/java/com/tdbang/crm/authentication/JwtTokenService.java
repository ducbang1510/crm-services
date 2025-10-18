/*
 * Copyright © 2025 by tdbang.
 * All rights reserved.
 */

package com.tdbang.crm.authentication;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Service;

import com.tdbang.crm.services.UserService;

@Log4j2
@Service
@RequiredArgsConstructor
public class JwtTokenService {
    private final JwtDecoder jwtDecoder;
    private final UserService userService;

    public Long verifyTokenAndGetUserPk(String token) {
        try {
            Jwt jwt = jwtDecoder.decode(token);
            String username = jwt.getSubject();
            return userService.getUserPkByUsername(username);
        } catch (JwtException e) {
            log.error("Error while verify token and get username {}", e.getMessage());
        }
        return null;
    }
}
