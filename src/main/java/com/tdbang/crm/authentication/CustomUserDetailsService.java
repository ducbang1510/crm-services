package com.tdbang.crm.authentication;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.tdbang.crm.entities.User;
import com.tdbang.crm.repositories.JpaUserRepository;
import com.tdbang.crm.services.SecurityService;

@Log4j2
@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private JpaUserRepository jpaUserRepository;

    @Autowired
    private SecurityService securityService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = jpaUserRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .disabled(!user.getIsActive())
                .authorities(securityService.getGrantedAuthority(user))
                .build();
    }
}
