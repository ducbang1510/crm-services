package com.tdbang.crm.controllers;

import java.time.LocalDate;

import javax.naming.AuthenticationException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.web.WebAttributes;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@Tag(name = "authorization-server-endpoints")
public class OAuth2PagesController {
    @Autowired
    private OAuth2AuthorizationService authorizationService;

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER = "Bearer ";

    @GetMapping("/login")
    public String login(HttpServletRequest request, Model model) {
        model.addAttribute("appName", "CRM OAuth2 Server");
        model.addAttribute("year", LocalDate.now().getYear());

        String redirectUri = request.getParameter("redirect_uri");
        if (redirectUri != null) {
            model.addAttribute("redirectUri", redirectUri);
        }
        Object ex = request.getSession().getAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
        if (ex instanceof AuthenticationException) {
            model.addAttribute("errorMessage", ((AuthenticationException) ex).getMessage());
        }
        return "login";
    }

    @GetMapping("/logout-success")
    public String logoutSuccess(HttpServletRequest request) {
        return "logout-success";
    }

    @Operation(summary = "Logout endpoint", description = "Remove access token")
    @PostMapping("/oauth2/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        String authHeader = request.getHeader(AUTHORIZATION_HEADER);
        if (authHeader == null || !authHeader.startsWith(BEARER)) {
            return ResponseEntity.badRequest().body("No Bearer token found");
        }

        String token = authHeader.substring(7);

        OAuth2Authorization authorization = authorizationService.findByToken(token, null);
        if (authorization != null) {
            authorizationService.remove(authorization);
            return ResponseEntity.ok("Access token revoked successfully");
        }

        return ResponseEntity.badRequest().body("Invalid or unknown token");
    }
}
