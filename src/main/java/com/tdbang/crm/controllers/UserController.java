package com.tdbang.crm.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.tdbang.crm.dtos.UserDTO;
import com.tdbang.crm.services.UserService;

@RestController
@RequestMapping("/api/v1/users")
public class UserController extends BaseController {
    private static Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('SCOPE_api:read')")
    public MappingJacksonValue retrieveUser(@RequestParam(value = "pk", required = false) Long pk) {
        LOGGER.info("Start retrieveUser");
        String username = getUserLogged();
        LOGGER.info("retrieveUser {}", username);
        UserDTO userInfo = new UserDTO(1L, "John Doe", "username", "password", "john@example.com",
                "123", true, true, new Date());
        LOGGER.info("End retrieveUser");
        return new MappingJacksonValue(userInfo);
    }
}
