package com.tdbang.crm.controllers;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.tdbang.crm.dtos.UserDTO;

@RestController
@RequestMapping("/api/v1/user")
public class UserController extends BaseController {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('SCOPE_api:read')")
    public MappingJacksonValue retrieveUserInfo(@RequestParam(value = "pk", required = false) Long pk) {
        LOGGER.info("Start retrieveUser");
        Long userPk = pk == null ? getPkUserLogged() : pk;
        UserDTO userInfo = userService.getUserInfo(userPk);
        LOGGER.info("End retrieveUser");
        return new MappingJacksonValue(userInfo);
    }

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('SCOPE_api:write')")
    public void createUser(@RequestBody @Valid UserDTO userDTO) {
        LOGGER.info("Start createUser");
        userService.createNewUser(userDTO);
        LOGGER.info("End createUser");
    }

    @GetMapping("/list")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('SCOPE_api:read')")
    public MappingJacksonValue retrieveUserList(@RequestParam(defaultValue = "0") int pageNumber,
                                                @RequestParam(defaultValue = "10") int pageSize) {
        LOGGER.info("Start retrieveUserList");
        Map<String, Object> userInfo = userService.getListOfUsers(pageNumber, pageSize);
        LOGGER.info("End retrieveUserList");
        return new MappingJacksonValue(userInfo);
    }
}
