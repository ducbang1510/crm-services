/*
 * Copyright © 2025 by tdbang.
 * All rights reserved.
 */

package com.tdbang.crm.filters;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

import com.tdbang.crm.commons.AuditAction;
import com.tdbang.crm.infrastructures.AuditContextHolder;
import com.tdbang.crm.infrastructures.AuditLogInfo;

@Log4j2
@RequiredArgsConstructor
@Component
@Order(2)
public class AuditLogFilter extends OncePerRequestFilter {
    private static final String[] AUTH_WHITELIST = {
        //Swagger API
        "/swagger-resources",
        "/swagger-resources/**",
        "/configuration/ui",
        "/configuration/security",
        "/swagger-ui.html",
        "/webjars/**",
        "/v3/api-docs/**",
        "/swagger-ui/**",
        "/login",
        "/css/**", "/js/**", "/images/**",
        "/socket.io/**",
        "/actuator/**",
        "/oauth2", "/login"
    };

    private final HandlerMappingIntrospector handlerMappingIntrospector;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("Do AuditLog Filter");
        HandlerExecutionChain handlerChain = null;
        try {
            handlerChain = Objects.requireNonNull(handlerMappingIntrospector.getMatchableHandlerMapping(request)).getHandler(request);
        } catch (Exception ignored) {
            log.error("Audit failed");
        }
        AuditLogInfo auditLogInfo = new AuditLogInfo();

        if (handlerChain != null) {
            Object handler = handlerChain.getHandler();
            if (handler instanceof HandlerMethod handlerMethod) {
                AuditAction auditAction = handlerMethod.getMethodAnnotation(AuditAction.class);
                if (auditAction != null) {
                    auditLogInfo.setRequestUrl(request.getRequestURI());
                    auditLogInfo.setAction(auditAction.value());
                    auditLogInfo.setDescription(auditAction.description());
                }
            }
        }
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        auditLogInfo.setUsername(auth != null ? auth.getName() : "system");

        AuditContextHolder.setCurrentAudit(auditLogInfo);
        try {
            // Continue filter chain
            filterChain.doFilter(request, response);
        } finally {
            AuditContextHolder.clearCurrentAudit();
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        List<String> ignoredPathStrings = List.of(AUTH_WHITELIST);
        List<RequestMatcher> ignoredPathMatchers = ignoredPathStrings.stream()
            .map((String pattern) -> PathPatternRequestMatcher.withDefaults().matcher(pattern))
            .collect(Collectors.toList());
        RequestMatcher ignoredPaths = new OrRequestMatcher(ignoredPathMatchers);
        return  ignoredPaths.matches(request);
    }
}
