/*
 * Copyright © 2025 by tdbang.
 * All rights reserved.
 */

package com.tdbang.crm.services;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

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
import com.tdbang.crm.utils.AppConstants;
import com.tdbang.crm.utils.MessageConstants;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private SpecificationFilterUtil<User> filterUtil;

    @Mock
    private CustomRepository<User> customRepository;

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService(filterUtil, customRepository);
        ReflectionTestUtils.setField(userService, "userRepository", userRepository);
        ReflectionTestUtils.setField(userService, "userMapper", userMapper);
        ReflectionTestUtils.setField(userService, "passwordEncoder", passwordEncoder);
    }

    @Test
    void getUserPkByUsername_returnsId() {
        when(userRepository.getUserPkByUsername("testuser")).thenReturn(1L);

        Long result = userService.getUserPkByUsername("testuser");

        assertEquals(1L, result);
    }

    @Test
    void getUserInfo_withExistingUser_returnsSuccess() {
        Long pk = 1L;
        User user = buildUser(pk, "John Doe", "john@example.com", true, false, true);

        when(userRepository.findUserByPk(pk)).thenReturn(user);

        ResponseDTO result = userService.getUserInfo(pk);

        assertNotNull(result);
        assertEquals(MessageConstants.SUCCESS_STATUS, result.getStatus());
        assertEquals(MessageConstants.FETCHING_USER_INFO_SUCCESS, result.getMsg());
        UserDTO data = (UserDTO) result.getData();
        assertEquals(pk, data.getPk());
        assertEquals("John Doe", data.getName());
        assertEquals("john@example.com", data.getEmail());
    }

    @Test
    void getUserInfo_withNotFoundUser_throwsNotFound() {
        when(userRepository.findUserByPk(999L)).thenReturn(null);

        CRMException ex = assertThrows(CRMException.class,
            () -> userService.getUserInfo(999L));

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
    }

    @Test
    void createNewUser_withValidData_returnsSuccess() {
        UserDTO userDTO = buildUserDTO();
        User savedUser = buildUser(1L, "John Doe", "john@example.com", false, false, true);

        when(userMapper.mappingUserDTOToUserEntity(userDTO)).thenReturn(savedUser);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        ResponseDTO result = userService.createNewUser(userDTO);

        assertNotNull(result);
        assertEquals(MessageConstants.SUCCESS_STATUS, result.getStatus());
        assertEquals(MessageConstants.CREATING_NEW_USER_SUCCESS, result.getMsg());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void createNewUser_withRepositoryException_throwsCRMException() {
        UserDTO userDTO = buildUserDTO();
        User user = new User();

        when(userMapper.mappingUserDTOToUserEntity(userDTO)).thenReturn(user);
        when(userRepository.save(any(User.class))).thenThrow(new RuntimeException("DB error"));

        CRMException ex = assertThrows(CRMException.class,
            () -> userService.createNewUser(userDTO));

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
    }

    @Test
    void editUser_withExistingUser_returnsSuccess() {
        Long pk = 1L;
        UpdateUserRequestDTO updateDTO = buildUpdateUserRequestDTO(pk);
        User existing = buildUser(pk, "Old Name", "old@example.com", false, false, true);

        when(userRepository.findUserByPk(pk)).thenReturn(existing);
        when(userRepository.save(any(User.class))).thenReturn(existing);

        ResponseDTO result = userService.editUser(pk, updateDTO);

        assertNotNull(result);
        assertEquals(MessageConstants.SUCCESS_STATUS, result.getStatus());
        assertEquals(MessageConstants.UPDATING_USER_SUCCESS, result.getMsg());
    }

    @Test
    void editUser_withNotFoundUser_throwsNotFound() {
        when(userRepository.findUserByPk(999L)).thenReturn(null);

        CRMException ex = assertThrows(CRMException.class,
            () -> userService.editUser(999L, new UpdateUserRequestDTO()));

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
    }

    @Test
    void changePassword_withCorrectOldPassword_returnsSuccess() {
        Long pk = 1L;
        User user = buildUser(pk, "John", "john@example.com", false, false, true);
        user.setPassword("encoded_old_password");

        ChangePasswordRequestDTO dto = new ChangePasswordRequestDTO("oldPassword", "newPassword1");

        when(userRepository.findUserByPk(pk)).thenReturn(user);
        when(passwordEncoder.matches("oldPassword", "encoded_old_password")).thenReturn(true);
        when(passwordEncoder.encode("newPassword1")).thenReturn("encoded_new_password");
        when(userRepository.save(any(User.class))).thenReturn(user);

        ResponseDTO result = userService.changePassword(pk, dto);

        assertNotNull(result);
        assertEquals(MessageConstants.SUCCESS_STATUS, result.getStatus());
        assertEquals(MessageConstants.CHANGING_USER_PASSWORD_SUCCESS, result.getMsg());
    }

    @Test
    void changePassword_withIncorrectOldPassword_throwsBadRequest() {
        Long pk = 1L;
        User user = buildUser(pk, "John", "john@example.com", false, false, true);
        user.setPassword("encoded_old_password");

        ChangePasswordRequestDTO dto = new ChangePasswordRequestDTO("wrongPassword", "newPassword1");

        when(userRepository.findUserByPk(pk)).thenReturn(user);
        when(passwordEncoder.matches("wrongPassword", "encoded_old_password")).thenReturn(false);

        CRMException ex = assertThrows(CRMException.class,
            () -> userService.changePassword(pk, dto));

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
        assertEquals(MessageConstants.INCORRECT_OLD_PASSWORD, ex.getMessage());
    }

    @Test
    void retrieveListNameOfUsers_returnsNames() {
        UserDTO u1 = buildUserDTO();
        u1.setName("Alice");
        UserDTO u2 = buildUserDTO();
        u2.setName("Bob");

        when(userRepository.getAllUsers()).thenReturn(List.of(u1, u2));

        ResponseDTO result = userService.retrieveListNameOfUsers();

        assertNotNull(result);
        assertEquals(MessageConstants.SUCCESS_STATUS, result.getStatus());
        @SuppressWarnings("unchecked")
        List<String> names = (List<String>) result.getData();
        assertEquals(2, names.size());
        assertTrue(names.contains("Alice"));
        assertTrue(names.contains("Bob"));
    }

    @Test
    void getUserRole_withActiveAdminUser_returnsAdminAndUserRoles() {
        Long pk = 1L;
        User user = buildUser(pk, "Admin User", "admin@example.com", true, false, true);

        when(userRepository.findUserByPk(pk)).thenReturn(user);

        List<String> roles = userService.getUserRole(pk);

        assertNotNull(roles);
        assertTrue(roles.contains(AppConstants.ROLE_USER));
        assertTrue(roles.contains(AppConstants.ROLE_ADMIN));
    }

    @Test
    void getUserRole_withActiveStaffUser_returnsStaffAndUserRoles() {
        Long pk = 2L;
        User user = buildUser(pk, "Staff User", "staff@example.com", false, true, true);

        when(userRepository.findUserByPk(pk)).thenReturn(user);

        List<String> roles = userService.getUserRole(pk);

        assertNotNull(roles);
        assertTrue(roles.contains(AppConstants.ROLE_USER));
        assertTrue(roles.contains(AppConstants.ROLE_STAFF));
    }

    @Test
    void getUserRole_withActiveUserOnly_returnsOnlyUserRole() {
        Long pk = 3L;
        User user = buildUser(pk, "Regular User", "user@example.com", false, false, true);

        when(userRepository.findUserByPk(pk)).thenReturn(user);

        List<String> roles = userService.getUserRole(pk);

        assertNotNull(roles);
        assertEquals(1, roles.size());
        assertTrue(roles.contains(AppConstants.ROLE_USER));
    }

    @Test
    void getUserRole_withNotFoundUser_throwsNotFound() {
        when(userRepository.findUserByPk(999L)).thenReturn(null);

        CRMException ex = assertThrows(CRMException.class,
            () -> userService.getUserRole(999L));

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
    }

    @Test
    void getListOfUsers_withPagination_returnsPagedResult() {
        UserDTO u1 = buildUserDTO();
        Page<UserDTO> page = new PageImpl<>(List.of(u1));

        when(userRepository.getUsersPageable(any(Pageable.class))).thenReturn(page);

        ResponseDTO result = userService.getListOfUsers(0, 10);

        assertNotNull(result);
        assertEquals(MessageConstants.SUCCESS_STATUS, result.getStatus());
        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) result.getData();
        assertNotNull(data.get("totalRecord"));
    }

    @Test
    void getListOfUsers_withoutPagination_returnsAllUsers() {
        UserDTO u1 = buildUserDTO();

        when(userRepository.getAllUsers()).thenReturn(List.of(u1));

        ResponseDTO result = userService.getListOfUsers(null, null);

        assertNotNull(result);
        assertEquals(MessageConstants.SUCCESS_STATUS, result.getStatus());
        @SuppressWarnings("unchecked")
        List<UserDTO> data = (List<UserDTO>) result.getData();
        assertEquals(1, data.size());
    }

    @Test
    void getListOfUsers_withFilterAndNoPageSize_returnsAllUsers() {
        User user = buildUser(1L, "John Doe", "john@example.com", false, false, true);
        UserDTO userDTO = buildUserDTO();

        when(customRepository.findAll(any(Sort.class), any())).thenReturn(List.of());
        when(userMapper.mapRecordList(any())).thenReturn(List.of(user));
        when(userMapper.mappingUserEntityToUserDTO(user)).thenReturn(userDTO);

        ResponseDTO result = userService.getListOfUsers(null, 0, 0, "pk", "ASC", null);

        assertNotNull(result);
        assertEquals(MessageConstants.SUCCESS_STATUS, result.getStatus());
        assertEquals(MessageConstants.FETCHING_LIST_OF_USERS_SUCCESS, result.getMsg());
    }

    @Test
    void getListOfUsers_withFilterAndPageSize_returnsPagedUsers() {
        Page<Map<String, Object>> page = new PageImpl<>(List.of(), PageRequest.of(0, 10), 0);

        when(customRepository.findAll(any(Pageable.class), any())).thenReturn(page);
        when(userMapper.mapRecordList(any())).thenReturn(List.of());

        ResponseDTO result = userService.getListOfUsers(null, 10, 0, "pk", "ASC", null);

        assertNotNull(result);
        assertEquals(MessageConstants.SUCCESS_STATUS, result.getStatus());
        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) result.getData();
        assertNotNull(data.get("totalRecord"));
    }

    // --- Helper methods ---

    private User buildUser(Long pk, String name, String email, Boolean isAdmin, Boolean isStaff, Boolean isActive) {
        User user = new User();
        user.setPk(pk);
        user.setName(name);
        user.setEmail(email);
        user.setPhone("0123456789");
        user.setIsAdmin(isAdmin);
        user.setIsStaff(isStaff);
        user.setIsActive(isActive);
        return user;
    }

    private UserDTO buildUserDTO() {
        UserDTO dto = new UserDTO();
        dto.setName("John Doe");
        dto.setFirstName("John");
        dto.setLastName("Doe");
        dto.setUsername("johndoe");
        dto.setPassword("password123");
        dto.setEmail("john@example.com");
        dto.setPhone("0123456789");
        return dto;
    }

    private UpdateUserRequestDTO buildUpdateUserRequestDTO(Long pk) {
        UpdateUserRequestDTO dto = new UpdateUserRequestDTO();
        dto.setPk(pk);
        dto.setName("Updated Name");
        dto.setFirstName("Updated");
        dto.setLastName("Name");
        dto.setEmail("updated@example.com");
        dto.setPhone("0987654321");
        return dto;
    }
}
