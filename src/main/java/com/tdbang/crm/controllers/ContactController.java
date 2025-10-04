package com.tdbang.crm.controllers;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.tdbang.crm.dtos.ContactDTO;
import com.tdbang.crm.dtos.ResponseDTO;
import com.tdbang.crm.services.ContactService;

@RestController
@RequestMapping("/api/v1/contact")
public class ContactController extends BaseController {
    private static Logger LOGGER = LoggerFactory.getLogger(ContactController.class);

    @Autowired
    private ContactService contactService;

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('ADMIN', 'USER')")
    public MappingJacksonValue createContact(@RequestBody @Valid ContactDTO contactDTO) {
        LOGGER.info("Start createContact");
        ResponseDTO responseDTO = contactService.createNewContact(contactDTO, getPkUserLogged());
        LOGGER.info("End createContact");
        return new MappingJacksonValue(responseDTO);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('ADMIN', 'USER')")
    public MappingJacksonValue retrieveContactDetails(@PathVariable Long id) {
        LOGGER.info("Start retrieveContactDetails");
        ResponseDTO orderDetails = contactService.getContactDetails(id);
        LOGGER.info("End retrieveContactDetails");
        return new MappingJacksonValue(orderDetails);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('ADMIN', 'USER')")
    public MappingJacksonValue updateContactDetails(@PathVariable Long id,
                                                    @RequestBody @Valid ContactDTO contactDTO) {
        LOGGER.info("Start updateContactDetails");
        ResponseDTO responseDTO = contactService.updateContactDetails(id, getPkUserLogged(), contactDTO);
        LOGGER.info("End updateContactDetails");
        return new MappingJacksonValue(responseDTO);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('ADMIN', 'USER')")
    public MappingJacksonValue deleteContactDetails(@PathVariable Long id) {
        LOGGER.info("Start deleteContactDetails");
        ResponseDTO responseDTO = contactService.deleteContactDetails(id, getPkUserLogged());
        LOGGER.info("End deleteContactDetails");
        return new MappingJacksonValue(responseDTO);
    }

    @GetMapping("/list")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('ADMIN', 'USER')")
    public MappingJacksonValue retrieveContactList(@RequestParam(required = false) Integer pageNumber,
                                                   @RequestParam(required = false) Integer pageSize) {
        LOGGER.info("Start retrieveContactList");
        ResponseDTO listOfContact = contactService.getListOfContact(pageNumber, pageSize, null);
        LOGGER.info("End retrieveContactList");
        return new MappingJacksonValue(listOfContact);
    }

    @GetMapping("/list/contact-name")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('ADMIN', 'USER')")
    public MappingJacksonValue retrieveContactNameList() {
        LOGGER.info("Start retrieveContactNameList");
        ResponseDTO listOfContactName = contactService.getListOfContactName();
        LOGGER.info("End retrieveContactNameList");
        return new MappingJacksonValue(listOfContactName);
    }

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('ADMIN', 'USER')")
    public MappingJacksonValue retrieveContactListWithFilter(@RequestParam(required = false) Integer pageNumber,
                                                             @RequestParam(required = false) Integer pageSize,
                                                             @RequestParam(required = false) String contactName) {
        LOGGER.info("Start retrieveContactListWithFilter");
        ResponseDTO listOfContact = contactService.getListOfContact(pageNumber, pageSize, contactName);
        LOGGER.info("End retrieveContactListWithFilter");
        return new MappingJacksonValue(listOfContact);
    }
}
