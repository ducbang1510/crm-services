package com.tdbang.crm.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.tdbang.crm.dtos.ResponseDTO;
import com.tdbang.crm.dtos.UserDTO;
import com.tdbang.crm.entities.User;
import com.tdbang.crm.exceptions.GenericException;
import com.tdbang.crm.mappers.UserMapper;
import com.tdbang.crm.repositories.JpaUserRepository;
import com.tdbang.crm.utils.AppConstants;
import com.tdbang.crm.utils.MessageConstants;

@Log4j2
@Service
public class UserService implements UserDetailsService {
    @Autowired
    private JpaUserRepository jpaUserRepository;

    @Autowired
    private SecurityService securityService;

    @Autowired
    private UserMapper userMapper;

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

    public Long getUserPkByUsername(String username) {
        return jpaUserRepository.getUserPkByUsername(username);
    }

    public ResponseDTO getListOfUsers(Integer pageNumber, Integer pageSize) {
        ResponseDTO result = new ResponseDTO(MessageConstants.SUCCESS_STATUS, MessageConstants.FETCHING_LIST_OF_NAMES_USERS_SUCCESS);
        if (pageNumber != null && pageSize != null) {
            Pageable pageable = PageRequest.of(pageNumber, pageSize);
            Page<UserDTO> userDTOPage = jpaUserRepository.getUsersPageable(pageable);
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put(AppConstants.RECORD_LIST_KEY, userDTOPage.getContent());
            resultMap.put(AppConstants.TOTAL_RECORD_KEY, userDTOPage.getTotalElements());
            result.setData(resultMap);
        } else {
            List<UserDTO> userDTOs = jpaUserRepository.getAllUsers();
            result.setData(userDTOs);
        }

        return result;
    }

    public ResponseDTO getUserInfo(Long pk) {
        ResponseDTO result;
        UserDTO userDTO = new UserDTO();
        User user = jpaUserRepository.findUserByPk(pk);
        if (user != null) {
            userDTO.setPk(user.getPk());
            userDTO.setName(user.getName());
            userDTO.setEmail(user.getEmail());
            userDTO.setPhone(user.getPhone());
            userDTO.setIsActive(user.getIsActive());
            userDTO.setIsAdmin(user.getIsAdmin());
            userDTO.setCreatedTime(user.getCreatedOn());
        } else {
            throw new GenericException(HttpStatus.NOT_FOUND, "FETCHING_USER_BY_ID_ERROR", MessageConstants.FETCHING_USER_BY_ID_ERROR);
        }
        result = new ResponseDTO(MessageConstants.SUCCESS_STATUS, MessageConstants.FETCHING_USER_INFO_SUCCESS, userDTO);
        return result;
    }

    public ResponseDTO createNewUser(UserDTO userDTO) {
        ResponseDTO result;
        try {
            User saveUser = userMapper.mappingUserDTOToUserEntity(userDTO, true);
            jpaUserRepository.save(saveUser);
            result = new ResponseDTO(MessageConstants.SUCCESS_STATUS, MessageConstants.CREATING_NEW_USER_SUCCESS);
        } catch (Exception e) {
            throw new GenericException(HttpStatus.BAD_REQUEST, "CREATING_NEW_USER_ERROR", "Error while creating user");
        }
        return result;
    }

    public ResponseDTO retrieveListNameOfUsers() {
        ResponseDTO result = new ResponseDTO(MessageConstants.SUCCESS_STATUS, MessageConstants.FETCHING_LIST_OF_NAMES_USERS_SUCCESS);

        List<UserDTO> userDTOs = jpaUserRepository.getAllUsers();
        List<String> nameOfUsers = userDTOs.stream().map(UserDTO::getName).toList();
        result.setData(nameOfUsers);

        return result;
    }
}
