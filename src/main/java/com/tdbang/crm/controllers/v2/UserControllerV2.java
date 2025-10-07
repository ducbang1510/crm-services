package com.tdbang.crm.controllers.v2;

import java.util.Set;

import com.fasterxml.jackson.databind.ser.FilterProvider;
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

import com.tdbang.crm.controllers.BaseController;
import com.tdbang.crm.dtos.ResponseDTO;
import com.tdbang.crm.dtos.UserDTO;

@RestController
@RequestMapping("/api/v2/user")
public class UserControllerV2 extends BaseController {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserControllerV2.class);
    private static final String USER_DTO_FILTER = "UserDTOFilter";
    private static final Set<String> EXCLUDE_USER_FIELDS = Set.of("username", "password");

    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('ADMIN', 'USER')")
    public MappingJacksonValue retrieveUserProfile() {
        LOGGER.info("Start retrieveUserProfile");
        Long userPk = getPkUserLogged();
        FilterProvider filters = buildFilterProvider(USER_DTO_FILTER, EXCLUDE_USER_FIELDS);

        ResponseDTO userProfile = userService.getUserInfo(userPk);
        MappingJacksonValue mappingJacksonValue = new MappingJacksonValue(userProfile);
        mappingJacksonValue.setFilters(filters);
        LOGGER.info("End retrieveUserProfile");
        return mappingJacksonValue;
    }

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('ADMIN')")
    public MappingJacksonValue createUser(@RequestBody @Valid UserDTO userDTO) {
        LOGGER.info("Start createUser");
        ResponseDTO responseDTO = userService.createNewUser(userDTO);
        LOGGER.info("End createUser");
        return new MappingJacksonValue(responseDTO);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('ADMIN')")
    public MappingJacksonValue retrieveUserInfo(@PathVariable Long id) {
        LOGGER.info("Start retrieveUserInfo");
        FilterProvider filters = buildFilterProvider(USER_DTO_FILTER, EXCLUDE_USER_FIELDS);

        ResponseDTO userInfo = userService.getUserInfo(id);
        MappingJacksonValue mappingJacksonValue = new MappingJacksonValue(userInfo);
        mappingJacksonValue.setFilters(filters);
        LOGGER.info("End retrieveUserInfo");
        return mappingJacksonValue;
    }

    @GetMapping("/list")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('ADMIN')")
    public MappingJacksonValue retrieveUserList(@RequestParam(required = false) Integer pageNumber,
                                                @RequestParam(required = false) Integer pageSize) {
        LOGGER.info("Start retrieveUserList");
        FilterProvider filters = buildFilterProvider(USER_DTO_FILTER, EXCLUDE_USER_FIELDS);

        ResponseDTO listOfUsers = userService.getListOfUsers(pageNumber, pageSize);
        MappingJacksonValue mappingJacksonValue = new MappingJacksonValue(listOfUsers);
        mappingJacksonValue.setFilters(filters);
        LOGGER.info("End retrieveUserList");
        return mappingJacksonValue;
    }

    @GetMapping("/list/name")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('ADMIN', 'USER')")
    public MappingJacksonValue retrieveListNameOfUsers() {
        LOGGER.info("Start retrieveListNameOfUsers");
        ResponseDTO listOfNameUsers = userService.retrieveListNameOfUsers();
        MappingJacksonValue mappingJacksonValue = new MappingJacksonValue(listOfNameUsers);
        LOGGER.info("End retrieveListNameOfUsers");
        return mappingJacksonValue;
    }
}
