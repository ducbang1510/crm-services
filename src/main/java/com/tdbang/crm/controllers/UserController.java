package com.tdbang.crm.controllers;

import java.util.Set;

import com.fasterxml.jackson.databind.ser.FilterProvider;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.tdbang.crm.dtos.ResponseDTO;
import com.tdbang.crm.dtos.UserDTO;

@Log4j2
@RestController
@RequestMapping("/api/v1/user")
@Tag(name = "CRM User APIs")
public class UserController extends BaseController {
    private static final String USER_DTO_FILTER = "UserDTOFilter";
    private static final Set<String> EXCLUDE_USER_FIELDS = Set.of("username", "password");

    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('ADMIN', 'USER')")
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

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('ADMIN')")
    public MappingJacksonValue createUser(@RequestBody @Valid UserDTO userDTO) {
        log.info("Start createUser");
        ResponseDTO responseDTO = userService.createNewUser(userDTO);
        log.info("End createUser");
        return new MappingJacksonValue(responseDTO);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('ADMIN')")
    public MappingJacksonValue retrieveUserInfo(@PathVariable Long id) {
        log.info("Start retrieveUserInfo");
        FilterProvider filters = buildFilterProvider(USER_DTO_FILTER, EXCLUDE_USER_FIELDS);

        ResponseDTO userInfo = userService.getUserInfo(id);
        MappingJacksonValue mappingJacksonValue = new MappingJacksonValue(userInfo);
        mappingJacksonValue.setFilters(filters);
        log.info("End retrieveUserInfo");
        return mappingJacksonValue;
    }

    @GetMapping("/list")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('ADMIN')")
    public MappingJacksonValue retrieveUserList(@RequestParam(required = false) Integer pageNumber,
                                                @RequestParam(required = false) Integer pageSize) {
        log.info("Start retrieveUserList");
        FilterProvider filters = buildFilterProvider(USER_DTO_FILTER, EXCLUDE_USER_FIELDS);

        ResponseDTO listOfUsers = userService.getListOfUsers(pageNumber, pageSize);
        MappingJacksonValue mappingJacksonValue = new MappingJacksonValue(listOfUsers);
        mappingJacksonValue.setFilters(filters);
        log.info("End retrieveUserList");
        return mappingJacksonValue;
    }

    @GetMapping("/list/name")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('ADMIN', 'USER')")
    public MappingJacksonValue retrieveListNameOfUsers() {
        log.info("Start retrieveListNameOfUsers");
        ResponseDTO listOfNameUsers = userService.retrieveListNameOfUsers();
        MappingJacksonValue mappingJacksonValue = new MappingJacksonValue(listOfNameUsers);
        log.info("End retrieveListNameOfUsers");
        return mappingJacksonValue;
    }
}
