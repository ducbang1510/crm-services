/*
 * Copyright © 2025 by tdbang.
 * All rights reserved.
 */

package com.tdbang.crm.mappers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.tdbang.crm.dtos.ContactDTO;
import com.tdbang.crm.dtos.nativequerydto.ContactQueryDTO;
import com.tdbang.crm.entities.Contact;
import com.tdbang.crm.entities.User;
import com.tdbang.crm.enums.LeadSource;
import com.tdbang.crm.enums.Salutation;
import com.tdbang.crm.utils.AppConstants;

@Component
public class ContactMapper {

    @Autowired
    private ModelMapper modelMapper;

    public List<Contact> mapRecordList(Map<String, Object> resultMap) {
        List<Contact> contacts = new ArrayList<>();

        // Extract the "recordList" value
        Object recordsObj = resultMap.get(AppConstants.RECORD_LIST_KEY);
        if (recordsObj instanceof List<?>) {
            List<?> recordList = (List<?>) recordsObj;

            for (Object obj : recordList) {
                if (obj instanceof Map) {
                    Map<String, Object> contactMap = (Map<String, Object>) obj;

                    // Convert nested maps for creator and assignedTo
                    if (contactMap.containsKey("creator") && contactMap.get("creator") instanceof Map) {
                        User creator = modelMapper.map(contactMap.get("creator"), User.class);
                        contactMap.put("creator", creator);
                    }

                    if (contactMap.containsKey("assignedTo") && contactMap.get("assignedTo") instanceof Map) {
                        User assignedTo = modelMapper.map(contactMap.get("assignedTo"), User.class);
                        contactMap.put("assignedTo", assignedTo);
                    }

                    // Map to Contact
                    Contact contact = modelMapper.map(contactMap, Contact.class);
                    contacts.add(contact);
                }
            }
        }

        return contacts;
    }

    public List<ContactDTO> mappingToListContactDTO(List<ContactQueryDTO> contactQueryDTOList) {
        List<ContactDTO> contactDTOList = new ArrayList<>();
        for (ContactQueryDTO contactQueryDTO : contactQueryDTOList) {
            ContactDTO contactDTO = mappingContactQueryDTOToContactDTO(contactQueryDTO);
            contactDTOList.add(contactDTO);
        }
        return contactDTOList;
    }

    public ContactDTO mappingContactQueryDTOToContactDTO(ContactQueryDTO contactQueryDTO) {
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

    public Contact mappingContactDTOToEntity(ContactDTO contactDTO, User creatorUser, User userAssignedTo, boolean isCreateNew) {
        Contact contact = new Contact();
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

    public ContactDTO mappingContactEntityToContactDTO(Contact contact) {
        ContactDTO contactDTO = new ContactDTO();
        contactDTO.setPk(contact.getPk());
        contactDTO.setContactName(contact.getContactName());
        contactDTO.setSalutation(contact.getSalutation() != null ? contact.getSalutation().getName() : null);
        contactDTO.setMobilePhone(contact.getMobilePhone());
        contactDTO.setEmail(contact.getEmail());
        contactDTO.setOrganization(contact.getOrganization());
        contactDTO.setDob(contact.getDob());
        contactDTO.setLeadSrc(contact.getLeadSrc() != null ? contact.getLeadSrc().getName() : null);
        contactDTO.setAssignedTo(contact.getAssignedTo() != null ? contact.getAssignedTo().getName() : null);
        contactDTO.setCreator(contact.getCreator() != null ? contact.getCreator().getName() : null);
        contactDTO.setAddress(contact.getAddress());
        contactDTO.setDescription(contact.getDescription());
        contactDTO.setCreatedTime(contact.getCreatedOn());
        contactDTO.setUpdatedTime(contact.getUpdatedOn());
        return contactDTO;
    }
}
