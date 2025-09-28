package com.tdbang.crm.services;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

@Service
public class SecurityService implements InitializingBean {
    @Override
    public void afterPropertiesSet() throws Exception {
        // Set required properties
    }

    public String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Check if authentication is valid and not anonymous
        if (authentication == null || !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken) {
            throw new IllegalStateException("User not properly authenticated");
        }

        // Handle JWT authentication
        if (authentication.getPrincipal() instanceof Jwt jwt) {
            return jwt.getSubject(); // This should be the username
        }
        // Handle other authentication types
        else if (authentication.getPrincipal() instanceof String principal) {
            return principal;
        }
        // Handle UserDetails
        else if (authentication.getPrincipal() instanceof org.springframework.security.core.userdetails.User user) {
            return user.getUsername();
        }

        throw new IllegalStateException("Unknown principal type: " +
                (authentication.getPrincipal() != null ?
                        authentication.getPrincipal().getClass().getName() : "null"));
    }
}
