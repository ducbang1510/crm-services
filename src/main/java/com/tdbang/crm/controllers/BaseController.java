package com.tdbang.crm.controllers;

import java.util.Set;

import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import org.springframework.beans.factory.annotation.Autowired;

import com.tdbang.crm.services.SecurityService;
import com.tdbang.crm.services.UserService;

public class BaseController {

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

    protected FilterProvider buildFilterProvider(String filterName, Set<String> filterFields) {
        SimpleBeanPropertyFilter filter = SimpleBeanPropertyFilter.serializeAllExcept(filterFields);
        return new SimpleFilterProvider().addFilter(filterName, filter);
    }

}
