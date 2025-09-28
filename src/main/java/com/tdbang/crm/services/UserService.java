package com.tdbang.crm.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.tdbang.crm.dtos.UserDTO;
import com.tdbang.crm.entities.User;
import com.tdbang.crm.exceptions.GenericException;
import com.tdbang.crm.repositories.UserRepository;

@Service
public class UserService implements UserDetailsService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);
    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .authorities("SCOPE_api:read", "SCOPE_api:write")
                .build();
    }

    public UserDTO getUserInfo(Long pk) {
        UserDTO userDTO = new UserDTO();
        User user = userRepository.findUserByPk(pk);
        if (user != null) {
            userDTO.setPk(user.getPk());
            userDTO.setName(user.getName());
            userDTO.setEmail(user.getEmail());
            userDTO.setPhone(user.getPhone());
            userDTO.setIsActive(user.getIsActive());
            userDTO.setIsManager(user.getIsManager());
            userDTO.setCreatedTime(user.getCreatedOn());
        } else {
            throw new GenericException(HttpStatus.NOT_FOUND, "FETCHING_USER_BY_ID_ERROR", "Error while fetching user by ID");
        }
        return userDTO;
    }
}
