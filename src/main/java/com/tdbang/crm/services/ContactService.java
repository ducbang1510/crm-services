package com.tdbang.crm.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.tdbang.crm.dtos.ResponseDTO;
import com.tdbang.crm.dtos.nativequerydto.ContactQueryDTO;
import com.tdbang.crm.repositories.ContactRepository;
import com.tdbang.crm.utils.AppConstants;

@Service
public class ContactService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ContactService.class);
    private static final String FETCHING_LIST_OF_CONTACTS_SUCCESS = "Fetching list of contacts successfully!";
    @Autowired
    private ContactRepository contactRepository;

    public ResponseDTO getListOfContact(Integer pageNumber, Integer pageSize) {
        ResponseDTO result = new ResponseDTO(1, FETCHING_LIST_OF_CONTACTS_SUCCESS);
        if (pageNumber != null && pageSize != null) {
            Pageable pageable = PageRequest.of(pageNumber, pageSize);
            Page<ContactQueryDTO> contactQueryDTOPage = contactRepository.getContactsPageable(pageable);
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put(AppConstants.CONTACT_LIST, contactQueryDTOPage.getContent());
            resultMap.put(AppConstants.TOTAL_RECORD, contactQueryDTOPage.getTotalElements());
            result.setData(resultMap);
        } else {
            List<ContactQueryDTO> contactQueryDTOs = contactRepository.getAllContacts();
            result.setData(contactQueryDTOs);
        }

        return result;
    }
}
