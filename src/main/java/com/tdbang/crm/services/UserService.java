package com.tdbang.crm.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
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

    public Long getUserPkByUsername(String username) {
        return userRepository.getUserPkByUsername(username);
    }

    public Map<String, Object> getListOfUsers(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<UserDTO> userDTOPage = userRepository.getUsersPageable(pageable);
        Map<String, Object> result = new HashMap<>();
        result.put("userList", userDTOPage.getContent());
        result.put("totalRecord", userDTOPage.getTotalElements());

        return result;
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
            userDTO.setIsAdmin(user.getIsAdmin());
            userDTO.setCreatedTime(user.getCreatedOn());
        } else {
            throw new GenericException(HttpStatus.NOT_FOUND, "FETCHING_USER_BY_ID_ERROR", "Error while fetching user by ID");
        }
        return userDTO;
    }

    public void createNewUser(UserDTO userDTO) {
        User saveUser = mappingUserDTOToUserEntity(userDTO, true);
        try {
            userRepository.save(saveUser);
        } catch (Exception e) {
            throw new GenericException(HttpStatus.BAD_REQUEST, "CREATING_NEW_USER_ERROR", "Error while creating user");
        }
    }

    private User mappingUserDTOToUserEntity(UserDTO userDTO, boolean isCreateNew) {
        User userEntity = new User();
        if (isCreateNew)
            userEntity.setUsername(userDTO.getUsername());
        userEntity.setName(userDTO.getName());
        userEntity.setPassword(PasswordEncoderFactories.createDelegatingPasswordEncoder().encode(userDTO.getPassword()));
        userEntity.setEmail(userDTO.getEmail());
        userEntity.setPhone(userDTO.getPhone());
        userEntity.setIsActive(userDTO.getIsActive());
        userEntity.setIsAdmin(userDTO.getIsAdmin());
        return userEntity;
    }
}
