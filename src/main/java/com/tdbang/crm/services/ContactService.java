package com.tdbang.crm.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.extern.log4j.Log4j2;
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
import com.tdbang.crm.exceptions.GenericException;
import com.tdbang.crm.mappers.ContactMapper;
import com.tdbang.crm.repositories.JpaContactRepository;
import com.tdbang.crm.repositories.JpaUserRepository;
import com.tdbang.crm.repositories.custom.CustomRepository;
import com.tdbang.crm.specifications.SpecificationFilterUtil;
import com.tdbang.crm.specifications.builders.ContactSpecificationBuilder;
import com.tdbang.crm.specifications.builders.SpecificationBuilder;
import com.tdbang.crm.utils.AppConstants;
import com.tdbang.crm.utils.AppUtils;
import com.tdbang.crm.utils.MessageConstants;

@Log4j2
@Service
public class ContactService extends AbstractService<Contact> {

    @Autowired
    private JpaContactRepository jpaContactRepository;
    @Autowired
    private JpaUserRepository jpaUserRepository;
    @Autowired
    private ContactMapper contactMapper;

    public ContactService(SpecificationFilterUtil<Contact> filterUtil, CustomRepository<Contact> repository) {
        super(filterUtil, repository);
    }

    public ResponseDTO getListOfContact(String filter, int pageSize, int pageNumber, String sortColumn, String sortOrder,
                                        String fields) {
        ResponseDTO result;
        try {
            List<ContactDTO> contactDTOList = new ArrayList<>();

            Map<String, Object> resultMapQuery = get(filter, pageSize, pageNumber, sortColumn, sortOrder, AppUtils.convertFields(fields));
            List<Contact> results = contactMapper.mapRecordList(resultMapQuery);
            for(Contact r: results) {
                contactDTOList.add(contactMapper.mappingContactEntityToContactDTO(r));
            }

            resultMapQuery.replace(AppConstants.RECORD_LIST_KEY, contactDTOList);
            if (pageSize == 0)
                resultMapQuery.remove(AppConstants.TOTAL_RECORD_KEY);
            result = new ResponseDTO(MessageConstants.SUCCESS_STATUS, MessageConstants.FETCHING_LIST_OF_CONTACTS_SUCCESS, resultMapQuery);
        } catch (Exception e) {
            result = new ResponseDTO(MessageConstants.ERROR_STATUS, MessageConstants.FETCHING_LIST_OF_CONTACTS_ERROR);
        }

        return result;
    }

    public ResponseDTO getListOfContactWithNonDynamicFilter(Integer pageNumber, Integer pageSize, String contactName) {
        ResponseDTO result;
        try {
            if (pageNumber != null && pageSize != null) {
                Map<String, Object> resultMap = new HashMap<>();
                Pageable pageable = PageRequest.of(pageNumber, pageSize);
                Page<ContactQueryDTO> contactQueryDTOPage = jpaContactRepository.getContactsPageable(contactName, pageable);
                resultMap.put(AppConstants.RECORD_LIST_KEY, contactMapper.mappingToListContactDTO(contactQueryDTOPage.getContent()));
                resultMap.put(AppConstants.TOTAL_RECORD_KEY, contactQueryDTOPage.getTotalElements());
                result = new ResponseDTO(MessageConstants.SUCCESS_STATUS, MessageConstants.FETCHING_LIST_OF_CONTACTS_SUCCESS, resultMap);
            } else {
                List<ContactQueryDTO> contactQueryDTOs = jpaContactRepository.getAllContacts(contactName);
                result = new ResponseDTO(MessageConstants.SUCCESS_STATUS, MessageConstants.FETCHING_LIST_OF_CONTACTS_SUCCESS,
                        contactMapper.mappingToListContactDTO(contactQueryDTOs));
            }
        } catch (Exception e) {
            result = new ResponseDTO(MessageConstants.ERROR_STATUS, MessageConstants.FETCHING_LIST_OF_CONTACTS_ERROR);
        }

        return result;
    }

    public ResponseDTO createNewContact(ContactDTO contactDTO, Long creatorFk) {
        ResponseDTO result;
        User creatorUser = jpaUserRepository.findUserByPk(creatorFk);
        try {
            User assignedTo = jpaUserRepository.getUsersByNames(contactDTO.getAssignedTo()).get(0);
            Contact saveContact = contactMapper.mappingContactDTOToEntity(contactDTO, creatorUser, assignedTo, true);
            jpaContactRepository.save(saveContact);
            result = new ResponseDTO(MessageConstants.SUCCESS_STATUS, MessageConstants.CREATING_NEW_CONTACT_SUCCESS);
        } catch (Exception e) {
            throw new GenericException(HttpStatus.BAD_REQUEST, "CREATING_NEW_CONTACT_ERROR", MessageConstants.CREATING_NEW_CONTACT_ERROR);
        }
        return result;
    }

    public ResponseDTO getContactDetails(Long contactPk) {
        ResponseDTO result = new ResponseDTO();
        if (contactPk != null) {
            ContactQueryDTO contactQueryDTO = jpaContactRepository.getContactDetailsByPk(contactPk);
            result = new ResponseDTO(MessageConstants.SUCCESS_STATUS, MessageConstants.FETCHING_CONTACT_SUCCESS,
                    contactMapper.mappingContactQueryDTOToContactDTO(contactQueryDTO));
        }

        return result;
    }

    public ResponseDTO updateContactDetails(Long contactPk, Long creatorFk, ContactDTO contactDTO) {
        ResponseDTO result;
        Contact updatedContact = jpaContactRepository.findByPk(contactPk)
                .orElseThrow(() -> new GenericException(HttpStatus.NOT_FOUND, "CONTACT_NOT_FOUND", "Contact not found"));
        if (updatedContact.getCreator().getPk().equals(creatorFk)) {
            User assignedTo = jpaUserRepository.getUsersByNames(contactDTO.getAssignedTo()).get(0);
            updatedContact = contactMapper.mappingContactDTOToEntity(contactDTO, null, assignedTo, false);
            jpaContactRepository.save(updatedContact);
            result = new ResponseDTO(MessageConstants.SUCCESS_STATUS, MessageConstants.UPDATING_CONTACT_SUCCESS);
        } else {
            throw new GenericException(HttpStatus.METHOD_NOT_ALLOWED, "USER_NOT_THE_CREATOR", "User is not the creator");
        }
        return result;
    }

    public ResponseDTO deleteContactDetails(Long contactPk, Long creatorFk) {
        ResponseDTO result;
        Contact deletedContact = jpaContactRepository.findByPk(contactPk)
                .orElseThrow(() -> new GenericException(HttpStatus.NOT_FOUND, "CONTACT_NOT_FOUND", "Contact not found"));
        if (deletedContact.getCreator().getPk().equals(creatorFk)) {
            jpaContactRepository.delete(deletedContact);
            result = new ResponseDTO(MessageConstants.SUCCESS_STATUS, MessageConstants.DELETING_CONTACT_SUCCESS);
        } else {
            throw new GenericException(HttpStatus.METHOD_NOT_ALLOWED, "USER_NOT_THE_CREATOR", "User is not the creator");
        }
        return result;
    }

    public ResponseDTO getListOfContactName() {
        ResponseDTO result = new ResponseDTO(MessageConstants.SUCCESS_STATUS, MessageConstants.FETCHING_LIST_OF_CONTACTS_SUCCESS);
        List<ContactQueryDTO> contactQueryDTOs = jpaContactRepository.getAllContacts(null);
        List<String> contactNames = contactMapper.mappingToListContactDTO(contactQueryDTOs).stream().map(ContactDTO::getContactName).toList();
        result.setData(contactNames);

        return result;
    }

    public ResponseDTO retrieveContactDashboardByLeadSource() {
        ResponseDTO result;
        List<DashboardQueryDTO> dashboardQueryDTOs = jpaContactRepository.countContactGroupByLeadSource();
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
        List<Contact> deletedListContacts = jpaContactRepository.getContactsByContactPks(contactPks);
        boolean hasOtherCreator = deletedListContacts.stream()
                .anyMatch(i -> !creatorFk.equals(i.getCreator().getPk()));
        if (!hasOtherCreator && contactPks.size() == deletedListContacts.size()) {
            jpaContactRepository.deleteAllById(deletedListContacts.stream().map(Contact::getPk).toList());
            result = new ResponseDTO(MessageConstants.SUCCESS_STATUS, MessageConstants.DELETING_LIST_OF_CONTACTS_SUCCESS);
        } else {
            throw new GenericException(HttpStatus.METHOD_NOT_ALLOWED, "USER_NOT_THE_CREATOR", "User is not the creator");
        }
        return result;
    }

    @Override
    protected String getProfileFields() {
        return "pk,contactName,mobilePhone,email,organization,address,description,createdOn,updatedOn,salutation,leadSrc,assignedTo,creator";
    }

    @Override
    protected String getDefaultSortColumn() {
        return "contactName";
    }

    @Override
    protected Class<Contact> getEntityClass() {
        return Contact.class;
    }

    @Override
    protected SpecificationBuilder<Contact> getSpecificationBuilder() {
        return new ContactSpecificationBuilder();
    }
}
