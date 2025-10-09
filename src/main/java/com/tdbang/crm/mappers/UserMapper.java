package com.tdbang.crm.mappers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.stereotype.Component;

import com.tdbang.crm.dtos.UserDTO;
import com.tdbang.crm.entities.User;
import com.tdbang.crm.utils.AppConstants;

@Component
public class UserMapper {

    @Autowired
    private ModelMapper modelMapper;

    public List<User> mapRecordList(Map<String, Object> resultMap) {
        List<User> users = new ArrayList<>();

        // Extract the "recordList" value
        Object recordsObj = resultMap.get(AppConstants.RECORD_LIST_KEY);
        if (recordsObj instanceof List<?>) {
            List<?> recordList = (List<?>) recordsObj;

            for (Object obj : recordList) {
                if (obj instanceof Map) {
                    Map<String, Object> userMap = (Map<String, Object>) obj;

                    // Map to User
                    User user = modelMapper.map(userMap, User.class);
                    users.add(user);
                }
            }
        }

        return users;
    }

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

    public UserDTO mappingUserEntityToUserDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setPk(user.getPk());
        userDTO.setUsername(user.getUsername());
        userDTO.setName(user.getName());
        userDTO.setEmail(user.getEmail());
        userDTO.setPhone(user.getPhone());
        userDTO.setIsActive(user.getIsActive());
        userDTO.setIsAdmin(user.getIsAdmin());
        return userDTO;
    }
}
