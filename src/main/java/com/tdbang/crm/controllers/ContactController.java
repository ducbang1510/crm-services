package com.tdbang.crm.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.tdbang.crm.dtos.ResponseDTO;
import com.tdbang.crm.services.ContactService;

@RestController
@RequestMapping("/api/v1/contact")
public class ContactController extends BaseController {
    private static Logger LOGGER = LoggerFactory.getLogger(ContactController.class);

    @Autowired
    private ContactService contactService;

    @GetMapping("/list")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('ADMIN', 'USER')")
    public MappingJacksonValue retrieveContactList(@RequestParam(required = false) Integer pageNumber,
                                                   @RequestParam(required = false) Integer pageSize) {
        LOGGER.info("Start retrieveContactList");
        ResponseDTO listOfContact = contactService.getListOfContact(pageNumber, pageSize);
        LOGGER.info("End retrieveContactList");
        return new MappingJacksonValue(listOfContact);
    }
}
