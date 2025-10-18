/*
 * Copyright © 2025 by tdbang.
 * All rights reserved.
 */

package com.tdbang.crm.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import com.tdbang.crm.entities.User;
import com.tdbang.crm.utils.AppConstants;

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

    public List<GrantedAuthority> getGrantedAuthority(User user) {
        List<GrantedAuthority> groupAuthorities = new ArrayList<>();
        if (Boolean.TRUE.equals(user.getIsAdmin())) {
            groupAuthorities.add(new SimpleGrantedAuthority(AppConstants.ROLE_ADMIN));
        }
        if (Boolean.TRUE.equals(user.getIsStaff())) {
            groupAuthorities.add(new SimpleGrantedAuthority(AppConstants.ROLE_STAFF));
        }
        groupAuthorities.add(new SimpleGrantedAuthority(AppConstants.ROLE_USER));

        return groupAuthorities;
    }
}
