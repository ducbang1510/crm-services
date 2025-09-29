package com.tdbang.crm.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;

import com.tdbang.crm.services.SecurityService;
import com.tdbang.crm.services.UserService;

public class BaseController {
    private static Logger LOGGER = LoggerFactory.getLogger(BaseController.class);

    @Autowired
    protected SecurityService securityService;

    @Autowired
    protected UserService userService;

    protected String getUserLogged() {
        return securityService.getCurrentUsername();
    }

    protected Long getPkUserLogged() {
        String loggedUsername = getUserLogged();
        return userService.getUserPkByUsername(loggedUsername);
    }

}
