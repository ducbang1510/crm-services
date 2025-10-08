package com.tdbang.crm.mappers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.stereotype.Component;

import com.tdbang.crm.dtos.UserDTO;
import com.tdbang.crm.entities.User;

@Component
public class UserMapper {

    @Autowired
    private ModelMapper modelMapper;

    public User mappingUserDTOToUserEntity(UserDTO userDTO, boolean isCreateNew) {
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
