package com.tdbang.crm.controllers;

import org.springframework.beans.factory.annotation.Autowired;

import com.tdbang.crm.services.SecurityService;

public class BaseController {

    @Autowired
    SecurityService securityService;

    protected String getUserLogged() {
        return securityService.getCurrentUsername();
    }

}
