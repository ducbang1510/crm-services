package com.tdbang.crm.mappers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.stereotype.Component;

import com.tdbang.crm.dtos.UpdateUserRequestDTO;
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

    public User mappingUserDTOToUserEntity(UserDTO userDTO) {
        User userEntity = new User();
        userEntity.setUsername(userDTO.getUsername());
        userEntity.setName(userDTO.getName());
        userEntity.setPassword(PasswordEncoderFactories.createDelegatingPasswordEncoder().encode(userDTO.getPassword()));
        userEntity.setEmail(userDTO.getEmail());
        userEntity.setPhone(userDTO.getPhone());
        userEntity.setIsActive(userDTO.getIsActive() != null && userDTO.getIsActive());
        userEntity.setIsAdmin(userDTO.getIsAdmin() != null && userDTO.getIsAdmin());
        return userEntity;
    }

    public void mappingUpdateUserRequestDTOToUserEntity(UpdateUserRequestDTO updateUserRequestDTO, User userEntity) {
        userEntity.setPk(updateUserRequestDTO.getPk());
        userEntity.setName(updateUserRequestDTO.getName());
        userEntity.setEmail(updateUserRequestDTO.getEmail());
        userEntity.setPhone(updateUserRequestDTO.getPhone());
        if (updateUserRequestDTO.getIsActive() != null)
            userEntity.setIsActive(updateUserRequestDTO.getIsActive());
        if (updateUserRequestDTO.getIsAdmin() != null)
            userEntity.setIsAdmin(updateUserRequestDTO.getIsAdmin());
        userEntity.setUpdatedOn(new Date());
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
