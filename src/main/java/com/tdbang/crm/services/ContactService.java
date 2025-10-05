package com.tdbang.crm.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.tdbang.crm.dtos.ContactDTO;
import com.tdbang.crm.dtos.DashboardDTO;
import com.tdbang.crm.dtos.ResponseDTO;
import com.tdbang.crm.dtos.nativequerydto.ContactQueryDTO;
import com.tdbang.crm.dtos.nativequerydto.DashboardQueryDTO;
import com.tdbang.crm.entities.Contact;
import com.tdbang.crm.entities.User;
import com.tdbang.crm.enums.LeadSource;
import com.tdbang.crm.enums.Salutation;
import com.tdbang.crm.exceptions.GenericException;
import com.tdbang.crm.repositories.ContactRepository;
import com.tdbang.crm.repositories.UserRepository;
import com.tdbang.crm.utils.AppConstants;
import com.tdbang.crm.utils.MessageConstants;

@Service
public class ContactService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ContactService.class);
    @Autowired
    private ContactRepository contactRepository;
    @Autowired
    private UserRepository userRepository;

    public ResponseDTO getListOfContact(Integer pageNumber, Integer pageSize, String contactName) {
        ResponseDTO result;
        try {
            if (pageNumber != null && pageSize != null) {
                Map<String, Object> resultMap = new HashMap<>();
                Pageable pageable = PageRequest.of(pageNumber, pageSize);
                Page<ContactQueryDTO> contactQueryDTOPage = contactRepository.getContactsPageable(contactName, pageable);
                resultMap.put(AppConstants.CONTACT_LIST, mappingToListContactDTO(contactQueryDTOPage.getContent()));
                resultMap.put(AppConstants.TOTAL_RECORD, contactQueryDTOPage.getTotalElements());
                result = new ResponseDTO(MessageConstants.SUCCESS_STATUS, MessageConstants.FETCHING_LIST_OF_CONTACTS_SUCCESS, resultMap);
            } else {
                List<ContactQueryDTO> contactQueryDTOs = contactRepository.getAllContacts(contactName);
                result = new ResponseDTO(MessageConstants.SUCCESS_STATUS, MessageConstants.FETCHING_LIST_OF_CONTACTS_SUCCESS,
                        mappingToListContactDTO(contactQueryDTOs));
            }
        } catch (Exception e) {
            result = new ResponseDTO(MessageConstants.ERROR_STATUS, MessageConstants.FETCHING_LIST_OF_CONTACTS_ERROR);
        }

        return result;
    }

    public ResponseDTO createNewContact(ContactDTO contactDTO, Long creatorFk) {
        ResponseDTO result;
        User creatorUser = userRepository.findUserByPk(creatorFk);
        try {
            Contact saveContact = mappingContactDTOToEntity(contactDTO, creatorUser, true);
            contactRepository.save(saveContact);
            result = new ResponseDTO(MessageConstants.SUCCESS_STATUS, MessageConstants.CREATING_NEW_CONTACT_SUCCESS);
        } catch (Exception e) {
            throw new GenericException(HttpStatus.BAD_REQUEST, "CREATING_NEW_CONTACT_ERROR", MessageConstants.CREATING_NEW_CONTACT_ERROR);
        }
        return result;
    }

    public ResponseDTO getContactDetails(Long contactPk) {
        ResponseDTO result = new ResponseDTO();
        if (contactPk != null) {
            ContactQueryDTO contactQueryDTO = contactRepository.getContactDetailsByPk(contactPk);
            result = new ResponseDTO(MessageConstants.SUCCESS_STATUS, MessageConstants.FETCHING_CONTACT_SUCCESS,
                    mappingContactQueryDTOToContactDTO(contactQueryDTO));
        }

        return result;
    }

    public ResponseDTO updateContactDetails(Long contactPk, Long creatorFk, ContactDTO contactDTO) {
        ResponseDTO result;
        Contact updatedContact = contactRepository.findByPk(contactPk)
                .orElseThrow(() -> new GenericException(HttpStatus.NOT_FOUND, "CONTACT_NOT_FOUND", "Contact not found"));
        if (updatedContact.getCreator().getPk().equals(creatorFk)) {
            updatedContact = mappingContactDTOToEntity(contactDTO, null, false);
            contactRepository.save(updatedContact);
            result = new ResponseDTO(MessageConstants.SUCCESS_STATUS, MessageConstants.UPDATING_CONTACT_SUCCESS);
        } else {
            throw new GenericException(HttpStatus.METHOD_NOT_ALLOWED, "USER_NOT_THE_CREATOR", "User is not the creator");
        }
        return result;
    }

    public ResponseDTO deleteContactDetails(Long contactPk, Long creatorFk) {
        ResponseDTO result;
        Contact deletedContact = contactRepository.findByPk(contactPk)
                .orElseThrow(() -> new GenericException(HttpStatus.NOT_FOUND, "CONTACT_NOT_FOUND", "Contact not found"));
        if (deletedContact.getCreator().getPk().equals(creatorFk)) {
            contactRepository.delete(deletedContact);
            result = new ResponseDTO(MessageConstants.SUCCESS_STATUS, MessageConstants.DELETING_CONTACT_SUCCESS);
        } else {
            throw new GenericException(HttpStatus.METHOD_NOT_ALLOWED, "USER_NOT_THE_CREATOR", "User is not the creator");
        }
        return result;
    }

    public ResponseDTO getListOfContactName() {
        ResponseDTO result = new ResponseDTO(MessageConstants.SUCCESS_STATUS, MessageConstants.FETCHING_LIST_OF_CONTACTS_SUCCESS);
        List<ContactQueryDTO> contactQueryDTOs = contactRepository.getAllContacts(null);
        List<String> contactNames = mappingToListContactDTO(contactQueryDTOs).stream().map(ContactDTO::getContactName).toList();
        result.setData(contactNames);

        return result;
    }

    public ResponseDTO retrieveContactDashboardByLeadSource() {
        ResponseDTO result;
        List<DashboardQueryDTO> dashboardQueryDTOs = contactRepository.countContactGroupByLeadSource();
        List<DashboardDTO> dashboardDTOs = new ArrayList<>();
        for (DashboardQueryDTO i : dashboardQueryDTOs) {
            DashboardDTO dashboardDTO = new DashboardDTO();
            dashboardDTO.setId(LeadSource.values()[i.getId()].getName());
            dashboardDTO.setCount(i.getCount());
            dashboardDTOs.add(dashboardDTO);
        }
        result = new ResponseDTO(MessageConstants.SUCCESS_STATUS, MessageConstants.COUNTING_NO_CONTACTS_BY_LEAD_SRC_SUCCESS, dashboardDTOs);

        return result;
    }

    public ResponseDTO deleteContacts(List<Long> contactPks, Long creatorFk) {
        ResponseDTO result;
        List<Contact> deletedListContacts = contactRepository.getContactsByContactPks(contactPks);
        boolean hasOtherCreator = deletedListContacts.stream()
                .anyMatch(i -> !creatorFk.equals(i.getCreator().getPk()));
        if (!hasOtherCreator && contactPks.size() == deletedListContacts.size()) {
            contactRepository.deleteAllById(deletedListContacts.stream().map(Contact::getPk).toList());
            result = new ResponseDTO(MessageConstants.SUCCESS_STATUS, MessageConstants.DELETING_LIST_OF_CONTACTS_SUCCESS);
        } else {
            throw new GenericException(HttpStatus.METHOD_NOT_ALLOWED, "USER_NOT_THE_CREATOR", "User is not the creator");
        }
        return result;
    }

    private List<ContactDTO> mappingToListContactDTO(List<ContactQueryDTO> contactQueryDTOList) {
        List<ContactDTO> contactDTOList = new ArrayList<>();
        for (ContactQueryDTO contactQueryDTO : contactQueryDTOList) {
            ContactDTO contactDTO = mappingContactQueryDTOToContactDTO(contactQueryDTO);
            contactDTOList.add(contactDTO);
        }
        return contactDTOList;
    }

    private ContactDTO mappingContactQueryDTOToContactDTO(ContactQueryDTO contactQueryDTO) {
        ContactDTO contactDTO = new ContactDTO();
        contactDTO.setPk(contactQueryDTO.getPk());
        contactDTO.setContactName(contactQueryDTO.getContactName());
        contactDTO.setSalutation(Salutation.values()[contactQueryDTO.getSalutation()].getName());
        contactDTO.setMobilePhone(contactQueryDTO.getMobilePhone());
        contactDTO.setEmail(contactQueryDTO.getEmail());
        contactDTO.setOrganization(contactQueryDTO.getOrganization());
        contactDTO.setDob(contactQueryDTO.getDateOfBirth());
        contactDTO.setLeadSrc(LeadSource.values()[contactQueryDTO.getLeadSrc()].getName());
        contactDTO.setAssignedTo(contactQueryDTO.getNameUserAssignedTo());
        contactDTO.setCreator(contactQueryDTO.getCreatorName());
        contactDTO.setAddress(contactQueryDTO.getAddress());
        contactDTO.setDescription(contactQueryDTO.getDescription());
        contactDTO.setCreatedTime(contactQueryDTO.getCreatedOn());
        contactDTO.setUpdatedTime(contactQueryDTO.getUpdatedOn());
        return contactDTO;
    }

    private Contact mappingContactDTOToEntity(ContactDTO contactDTO, User creatorUser, boolean isCreateNew) {
        Contact contact = new Contact();
        User userAssignedTo = userRepository.getUsersByNames(contactDTO.getAssignedTo()).get(0);
        contact.setPk(contactDTO.getPk());
        contact.setContactName(contactDTO.getContactName());
        contact.setSalutation(Salutation.fromName(contactDTO.getSalutation()));
        contact.setMobilePhone(contactDTO.getMobilePhone());
        contact.setEmail(contactDTO.getEmail());
        contact.setOrganization(contactDTO.getOrganization());
        contact.setLeadSrc(LeadSource.fromName(contactDTO.getLeadSrc()));
        contact.setAssignedTo(userAssignedTo);
        contact.setAddress(contactDTO.getAddress());
        contact.setDescription(contactDTO.getDescription());
        if (isCreateNew) {
            if (creatorUser != null)
                contact.setCreator(creatorUser);
        } else {
            contact.setUpdatedOn(new Date());
        }
        return contact;
    }
}
