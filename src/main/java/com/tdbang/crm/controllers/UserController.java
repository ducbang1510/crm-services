package com.tdbang.crm.controllers;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

@RestController
@RequestMapping("/api/v1/user")
public class UserController extends BaseController {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('ADMIN', 'USER')")
    public MappingJacksonValue retrieveUserProfile() {
        LOGGER.info("Start retrieveUserProfile");
        Long userPk = getPkUserLogged();
        ResponseDTO userProfile = userService.getUserInfo(userPk);
        LOGGER.info("End retrieveUserProfile");
        return new MappingJacksonValue(userProfile);
    }

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('ADMIN')")
    public void createUser(@RequestBody @Valid UserDTO userDTO) {
        LOGGER.info("Start createUser");
        userService.createNewUser(userDTO);
        LOGGER.info("End createUser");
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('ADMIN')")
    public MappingJacksonValue retrieveUserInfo(@PathVariable Long id) {
        LOGGER.info("Start retrieveUserInfo");
        ResponseDTO userInfo = userService.getUserInfo(id);
        LOGGER.info("End retrieveUserInfo");
        return new MappingJacksonValue(userInfo);
    }

    @GetMapping("/list")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('ADMIN')")
    public MappingJacksonValue retrieveUserList(@RequestParam(required = false) Integer pageNumber,
                                                @RequestParam(required = false) Integer pageSize) {
        LOGGER.info("Start retrieveUserList");
        ResponseDTO listOfUsers = userService.getListOfUsers(pageNumber, pageSize);
        LOGGER.info("End retrieveUserList");
        return new MappingJacksonValue(listOfUsers);
    }
}
