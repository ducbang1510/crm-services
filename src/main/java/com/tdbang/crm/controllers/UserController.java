/*
 * Copyright © 2025 by tdbang.
 * All rights reserved.
 */

package com.tdbang.crm.controllers;

import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.databind.ser.FilterProvider;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.tdbang.crm.commons.AuditAction;
import com.tdbang.crm.dtos.ChangePasswordRequestDTO;
import com.tdbang.crm.dtos.ResponseDTO;
import com.tdbang.crm.dtos.UpdateUserRequestDTO;
import com.tdbang.crm.dtos.UserDTO;
import com.tdbang.crm.exceptions.CRMException;
import com.tdbang.crm.utils.MessageConstants;

@Log4j2
@RestController
@RequestMapping("/api/v1/user")
@Tag(name = "CRM User APIs")
public class UserController extends BaseController {
    private static final String USER_DTO_FILTER = "UserDTOFilter";
    private static final Set<String> EXCLUDE_USER_FIELDS = Set.of("username", "password");

    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    public MappingJacksonValue retrieveUserProfile() {
        log.info("Start retrieveUserProfile");
        Long userPk = getPkUserLogged();
        FilterProvider filters = buildFilterProvider(USER_DTO_FILTER, EXCLUDE_USER_FIELDS);

        ResponseDTO userProfile = userService.getUserInfo(userPk);
        MappingJacksonValue mappingJacksonValue = new MappingJacksonValue(userProfile);
        mappingJacksonValue.setFilters(filters);
        log.info("End retrieveUserProfile");
        return mappingJacksonValue;
    }

    @PutMapping("")
    @AuditAction(value = "CHANGE_PASSWORD", description = "Update password of user")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    public MappingJacksonValue changePassword(@RequestBody @Valid ChangePasswordRequestDTO changePasswordRequestDTO) {
        log.info("Start changePassword");
        Long userPk = getPkUserLogged();
        ResponseDTO responseDTO = userService.changePassword(userPk, changePasswordRequestDTO);
        log.info("End changePassword");
        return new MappingJacksonValue(responseDTO);
    }

    @PostMapping("/create")
    @AuditAction(value = "CREATE_USER", description = "Create new user")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public MappingJacksonValue createUser(@RequestBody @Valid UserDTO userDTO) {
        log.info("Start createUser");
        ResponseDTO responseDTO = userService.createNewUser(userDTO);
        log.info("End createUser");
        return new MappingJacksonValue(responseDTO);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public MappingJacksonValue retrieveUserInfo(@PathVariable Long id) {
        log.info("Start retrieveUserInfo");
        FilterProvider filters = buildFilterProvider(USER_DTO_FILTER, EXCLUDE_USER_FIELDS);

        ResponseDTO userInfo = userService.getUserInfo(id);
        MappingJacksonValue mappingJacksonValue = new MappingJacksonValue(userInfo);
        mappingJacksonValue.setFilters(filters);
        log.info("End retrieveUserInfo");
        return mappingJacksonValue;
    }

    @PutMapping("/{id}")
    @AuditAction(value = "UPDATE_USER", description = "Update existing user")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public MappingJacksonValue editUser(@PathVariable Long id,
                                        @RequestBody @Valid UpdateUserRequestDTO updateUserRequestDTO) {
        log.info("Start editUser");
        if (!id.equals(updateUserRequestDTO.getPk())) {
            throw new CRMException(HttpStatus.BAD_REQUEST, MessageConstants.BAD_REQUEST_CODE, MessageConstants.UPDATING_USER_ERROR);
        }
        ResponseDTO responseDTO = userService.editUser(id, updateUserRequestDTO);
        log.info("End editUser");
        return new MappingJacksonValue(responseDTO);
    }

    @GetMapping("/list")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public MappingJacksonValue retrieveUserList(
        @RequestParam(required = false) @Parameter(description = "Optional filter on fields", example = "name:John") String filter,
        @RequestParam(required = false) @Parameter(description = "Optional fields to be included in the response", example = "pk,name") String fields,
        @RequestParam(required = false, defaultValue = "0") int pageNumber,
        @RequestParam(required = false, defaultValue = "0") int pageSize,
        @RequestParam(required = false, defaultValue = "pk") String sortColumn,
        @RequestParam(required = false, defaultValue = "ASC") String sortOrder) {
        log.info("Start retrieveUserList");
        FilterProvider filters = buildFilterProvider(USER_DTO_FILTER, EXCLUDE_USER_FIELDS);

        ResponseDTO listOfUsers = userService.getListOfUsers(filter, pageSize, pageNumber, sortColumn, sortOrder, fields);
        MappingJacksonValue mappingJacksonValue = new MappingJacksonValue(listOfUsers);
        mappingJacksonValue.setFilters(filters);
        log.info("End retrieveUserList");
        return mappingJacksonValue;
    }

    @GetMapping("/list/name")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    public MappingJacksonValue retrieveListNameOfUsers() {
        log.info("Start retrieveListNameOfUsers");
        ResponseDTO listOfNameUsers = userService.retrieveListNameOfUsers();
        MappingJacksonValue mappingJacksonValue = new MappingJacksonValue(listOfNameUsers);
        log.info("End retrieveListNameOfUsers");
        return mappingJacksonValue;
    }

    @GetMapping("/role")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyAuthority('ADMIN', 'STAFF', 'USER')")
    public MappingJacksonValue retrieveUserRole() {
        log.info("Start retrieveUserRole");
        Long userPk = getPkUserLogged();
        List<String> roles = userService.getUserRole(userPk);
        MappingJacksonValue mappingJacksonValue = new MappingJacksonValue(roles);
        log.info("End retrieveUserRole");
        return mappingJacksonValue;
    }
}
