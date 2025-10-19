/*
 * Copyright © 2025 by tdbang.
 * All rights reserved.
 */

package com.tdbang.crm.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.tdbang.crm.dtos.ChangePasswordRequestDTO;
import com.tdbang.crm.dtos.ResponseDTO;
import com.tdbang.crm.dtos.UpdateUserRequestDTO;
import com.tdbang.crm.dtos.UserDTO;
import com.tdbang.crm.entities.User;
import com.tdbang.crm.exceptions.CRMException;
import com.tdbang.crm.mappers.UserMapper;
import com.tdbang.crm.repositories.UserRepository;
import com.tdbang.crm.repositories.custom.CustomRepository;
import com.tdbang.crm.specifications.SpecificationFilterUtil;
import com.tdbang.crm.specifications.builders.SpecificationBuilder;
import com.tdbang.crm.specifications.builders.UserSpecificationBuilder;
import com.tdbang.crm.utils.AppConstants;
import com.tdbang.crm.utils.AppUtils;
import com.tdbang.crm.utils.MessageConstants;

@Log4j2
@Service
public class UserService extends AbstractService<User> {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public UserService(SpecificationFilterUtil<User> filterUtil, CustomRepository<User> repository) {
        super(filterUtil, repository);
    }

    public Long getUserPkByUsername(String username) {
        return userRepository.getUserPkByUsername(username);
    }

    public ResponseDTO getListOfUsers(Integer pageNumber, Integer pageSize) {
        ResponseDTO result = new ResponseDTO(MessageConstants.SUCCESS_STATUS, MessageConstants.FETCHING_LIST_OF_NAMES_USERS_SUCCESS);
        if (pageNumber != null && pageSize != null) {
            Pageable pageable = PageRequest.of(pageNumber, pageSize);
            Page<UserDTO> userDTOPage = userRepository.getUsersPageable(pageable);
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put(AppConstants.RECORD_LIST_KEY, userDTOPage.getContent());
            resultMap.put(AppConstants.TOTAL_RECORD_KEY, userDTOPage.getTotalElements());
            result.setData(resultMap);
        } else {
            List<UserDTO> userDTOs = userRepository.getAllUsers();
            result.setData(userDTOs);
        }

        return result;
    }

    public ResponseDTO getListOfUsers(String filter, int pageSize, int pageNumber, String sortColumn, String sortOrder,
                                      String fields) {
        ResponseDTO result;
        try {
            List<UserDTO> userDTOList = new ArrayList<>();

            Map<String, Object> resultMapQuery = get(filter, pageSize, pageNumber, sortColumn, sortOrder, AppUtils.convertFields(fields));
            List<User> results = userMapper.mapRecordList(resultMapQuery);
            for (User r : results) {
                userDTOList.add(userMapper.mappingUserEntityToUserDTO(r));
            }

            resultMapQuery.replace(AppConstants.RECORD_LIST_KEY, userDTOList);
            if (pageSize == 0) {
                result = new ResponseDTO(MessageConstants.SUCCESS_STATUS, MessageConstants.FETCHING_LIST_OF_USERS_SUCCESS, userDTOList);
            } else {
                result = new ResponseDTO(MessageConstants.SUCCESS_STATUS, MessageConstants.FETCHING_LIST_OF_USERS_SUCCESS, resultMapQuery);
            }
        } catch (Exception e) {
            throw new CRMException(HttpStatus.INTERNAL_SERVER_ERROR, MessageConstants.INTERNAL_ERROR_CODE, MessageConstants.INTERNAL_ERROR_MESSAGE, e.getMessage());
        }

        return result;
    }

    public ResponseDTO getUserInfo(Long pk) {
        ResponseDTO result;
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
            throw new CRMException(HttpStatus.NOT_FOUND, MessageConstants.NOT_FOUND_CODE, MessageConstants.NOT_FOUND_MESSAGE);
        }
        result = new ResponseDTO(MessageConstants.SUCCESS_STATUS, MessageConstants.FETCHING_USER_INFO_SUCCESS, userDTO);
        return result;
    }

    public ResponseDTO createNewUser(UserDTO userDTO) {
        ResponseDTO result;
        try {
            User saveUser = userMapper.mappingUserDTOToUserEntity(userDTO);
            userRepository.save(saveUser);
            result = new ResponseDTO(MessageConstants.SUCCESS_STATUS, MessageConstants.CREATING_NEW_USER_SUCCESS);
        } catch (Exception e) {
            throw new CRMException(HttpStatus.BAD_REQUEST, MessageConstants.BAD_REQUEST_CODE, MessageConstants.CREATING_NEW_USER_ERROR, e.getMessage());
        }
        return result;
    }

    public ResponseDTO editUser(Long pk, UpdateUserRequestDTO updateUserRequestDTO) {
        ResponseDTO result;
        User user = userRepository.findUserByPk(pk);
        if (user == null) {
            throw new CRMException(HttpStatus.NOT_FOUND, MessageConstants.NOT_FOUND_CODE, MessageConstants.NOT_FOUND_MESSAGE);
        }
        try {
            userMapper.mappingUpdateUserRequestDTOToUserEntity(updateUserRequestDTO, user);
            userRepository.save(user);
            result = new ResponseDTO(MessageConstants.SUCCESS_STATUS, MessageConstants.UPDATING_USER_SUCCESS);
        } catch (Exception e) {
            throw new CRMException(HttpStatus.BAD_REQUEST, MessageConstants.BAD_REQUEST_CODE, MessageConstants.UPDATING_USER_ERROR, e.getMessage());
        }
        return result;
    }

    public ResponseDTO changePassword(Long pk, ChangePasswordRequestDTO changePasswordRequestDTO) {
        ResponseDTO result;
        User user = userRepository.findUserByPk(pk);
        if (!passwordEncoder.matches(changePasswordRequestDTO.getOldPassword(), user.getPassword())) {
            throw new CRMException(HttpStatus.BAD_REQUEST, MessageConstants.BAD_REQUEST_CODE, MessageConstants.INCORRECT_OLD_PASSWORD);
        }
        try {
            user.setPassword(passwordEncoder.encode(changePasswordRequestDTO.getNewPassword()));
            userRepository.save(user);
            result = new ResponseDTO(MessageConstants.SUCCESS_STATUS, MessageConstants.CHANGING_USER_PASSWORD_SUCCESS);
        } catch (Exception e) {
            throw new CRMException(HttpStatus.BAD_REQUEST, MessageConstants.BAD_REQUEST_CODE, MessageConstants.CHANGING_USER_PASSWORD_ERROR, e.getMessage());
        }
        return result;
    }

    public ResponseDTO retrieveListNameOfUsers() {
        ResponseDTO result = new ResponseDTO(MessageConstants.SUCCESS_STATUS, MessageConstants.FETCHING_LIST_OF_NAMES_USERS_SUCCESS);

        List<UserDTO> userDTOs = userRepository.getAllUsers();
        List<String> nameOfUsers = userDTOs.stream().map(UserDTO::getName).toList();
        result.setData(nameOfUsers);

        return result;
    }

    public List<String> getUserRole(Long pk) {
        User user = userRepository.findUserByPk(pk);
        List<String> roles = new ArrayList<>();
        if (user != null) {
            if (Boolean.TRUE.equals(user.getIsActive())) {
                roles.add(AppConstants.ROLE_USER);
                if (Boolean.TRUE.equals(user.getIsStaff())) {
                    roles.add(AppConstants.ROLE_STAFF);
                }
                if (Boolean.TRUE.equals(user.getIsAdmin())) {
                    roles.add(AppConstants.ROLE_ADMIN);
                }
            }
        } else {
            throw new CRMException(HttpStatus.NOT_FOUND, MessageConstants.NOT_FOUND_CODE, MessageConstants.NOT_FOUND_MESSAGE);
        }
        return roles;
    }

    @Override
    protected String getProfileFields() {
        return "pk,name,firstName,lastName,username,password,email,phone,isAdmin,isStaff,isActive,createdOn,updatedOn";
    }

    @Override
    protected String getDefaultSortColumn() {
        return "name";
    }

    @Override
    protected Class<User> getEntityClass() {
        return User.class;
    }

    @Override
    protected SpecificationBuilder<User> getSpecificationBuilder() {
        return new UserSpecificationBuilder();
    }
}
