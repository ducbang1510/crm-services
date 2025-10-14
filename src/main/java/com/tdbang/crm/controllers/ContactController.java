package com.tdbang.crm.controllers;

import java.util.List;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
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
import com.tdbang.crm.enums.LeadSource;
import com.tdbang.crm.enums.Salutation;
import com.tdbang.crm.services.ContactService;

@Log4j2
@RestController
@RequestMapping("/api/v1/contact")
@Tag(name = "CRM Contact APIs")
public class ContactController extends BaseController {

    @Autowired
    private ContactService contactService;

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    public MappingJacksonValue createContact(@RequestBody @Valid ContactDTO contactDTO) {
        log.info("Start createContact");
        ResponseDTO responseDTO = contactService.createNewContact(contactDTO, getPkUserLogged());
        log.info("End createContact");
        return new MappingJacksonValue(responseDTO);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    public MappingJacksonValue retrieveContactDetails(@PathVariable Long id) {
        log.info("Start retrieveContactDetails");
        ResponseDTO orderDetails = contactService.getContactDetails(id);
        log.info("End retrieveContactDetails");
        return new MappingJacksonValue(orderDetails);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    public MappingJacksonValue updateContactDetails(@PathVariable Long id,
                                                    @RequestBody @Valid ContactDTO contactDTO) {
        log.info("Start updateContactDetails");
        ResponseDTO responseDTO = contactService.updateContactDetails(id, getPkUserLogged(), contactDTO);
        log.info("End updateContactDetails");
        return new MappingJacksonValue(responseDTO);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    public MappingJacksonValue deleteContactDetails(@PathVariable Long id) {
        log.info("Start deleteContactDetails");
        ResponseDTO responseDTO = contactService.deleteContactDetails(id, getPkUserLogged());
        log.info("End deleteContactDetails");
        return new MappingJacksonValue(responseDTO);
    }

    @GetMapping("/list")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    public MappingJacksonValue retrieveContactList(
            @RequestParam(required = false) @Parameter(description = "Optional filter on fields", example = "contactName:John,organization:OrgName") String filter,
            @RequestParam(required = false) @Parameter(description = "Optional fields to be included in the response", example = "contactName,organization") String fields,
            @RequestParam(required = false, defaultValue = "0") int pageNumber,
            @RequestParam(required = false, defaultValue = "0") int pageSize,
            @RequestParam(required = false, defaultValue = "pk") String sortColumn,
            @RequestParam(required = false, defaultValue = "ASC") String sortOrder) {
        log.info("Start retrieveContactList");
        ResponseDTO listOfContact = contactService.getListOfContact(filter, pageSize, pageNumber, sortColumn, sortOrder, fields);
        log.info("End retrieveContactList");
        return new MappingJacksonValue(listOfContact);
    }

    @GetMapping("/list/contact-name")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    public MappingJacksonValue retrieveContactNameList() {
        log.info("Start retrieveContactNameList");
        ResponseDTO listOfContactName = contactService.getListOfContactName();
        log.info("End retrieveContactNameList");
        return new MappingJacksonValue(listOfContactName);
    }

    /**
     * @deprecated (This function will be removed, use retrieveContactList instead)
     */
    @Deprecated(since="1.1.0", forRemoval = true)
    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    public MappingJacksonValue retrieveContactListWithNonDynamicFilter(@RequestParam(required = false) Integer pageNumber,
                                                                       @RequestParam(required = false) Integer pageSize,
                                                                       @RequestParam(required = false) String contactName) {
        log.info("Start retrieveContactListWithNonDynamicFilter");
        ResponseDTO listOfContact = contactService.getListOfContactWithNonDynamicFilter(pageNumber, pageSize, contactName);
        log.info("End retrieveContactListWithNonDynamicFilter");
        return new MappingJacksonValue(listOfContact);
    }

    @GetMapping("/count/lead-source")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    public MappingJacksonValue retrieveContactDashboardByLeadSource() {
        log.info("Start retrieveContactDashboardByLeadSource");
        ResponseDTO responseDTO = contactService.retrieveContactDashboardByLeadSource();
        log.info("End retrieveContactDashboardByLeadSource");
        return new MappingJacksonValue(responseDTO);
    }

    @PostMapping("/delete")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    public MappingJacksonValue deleteContacts(@RequestBody List<Long> ids) {
        log.info("Start deleteContacts");
        ResponseDTO responseDTO = contactService.deleteContacts(ids, getPkUserLogged());
        log.info("End deleteContacts");
        return new MappingJacksonValue(responseDTO);
    }

    @GetMapping("/salutation")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    public MappingJacksonValue retrieveSalutationEnumOfContact() {
        log.info("Start retrieveSalutationEnumOfContact");
        List<Salutation> status = contactService.retrieveSalutationEnumOfContact();
        log.info("End retrieveSalutationEnumOfContact");
        return new MappingJacksonValue(status);
    }

    @GetMapping("/lead-source")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    public MappingJacksonValue retrieveLeadSourceEnumOfContact() {
        log.info("Start retrieveLeadSourceEnumOfContact");
        List<LeadSource> status = contactService.retrieveLeadSourceEnumOfContact();
        log.info("End retrieveLeadSourceEnumOfContact");
        return new MappingJacksonValue(status);
    }
}
