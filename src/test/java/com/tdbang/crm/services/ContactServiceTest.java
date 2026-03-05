/*
 * Copyright © 2025 by tdbang.
 * All rights reserved.
 */

package com.tdbang.crm.services;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;

import com.tdbang.crm.dtos.ContactDTO;
import com.tdbang.crm.dtos.DashboardDTO;
import com.tdbang.crm.dtos.ResponseDTO;
import com.tdbang.crm.dtos.nativequerydto.ContactQueryDTO;
import com.tdbang.crm.dtos.nativequerydto.DashboardQueryDTO;
import com.tdbang.crm.entities.Contact;
import com.tdbang.crm.entities.User;
import com.tdbang.crm.enums.LeadSource;
import com.tdbang.crm.enums.Salutation;
import com.tdbang.crm.exceptions.CRMException;
import com.tdbang.crm.mappers.ContactMapper;
import com.tdbang.crm.repositories.ContactRepository;
import com.tdbang.crm.repositories.UserRepository;
import com.tdbang.crm.repositories.custom.CustomRepository;
import com.tdbang.crm.specifications.SpecificationFilterUtil;
import com.tdbang.crm.utils.MessageConstants;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ContactServiceTest {

    @Mock
    private ContactRepository contactRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ContactMapper contactMapper;

    @Mock
    private NotificationService notificationService;

    @Mock
    private SocketEventService socketEventService;

    @Mock
    private EmailService emailService;

    @Mock
    private SpecificationFilterUtil<Contact> filterUtil;

    @Mock
    private CustomRepository<Contact> customRepository;

    private ContactService contactService;

    @BeforeEach
    void setUp() {
        contactService = new ContactService(filterUtil, customRepository);
        ReflectionTestUtils.setField(contactService, "contactRepository", contactRepository);
        ReflectionTestUtils.setField(contactService, "userRepository", userRepository);
        ReflectionTestUtils.setField(contactService, "contactMapper", contactMapper);
        ReflectionTestUtils.setField(contactService, "notificationService", notificationService);
        ReflectionTestUtils.setField(contactService, "socketEventService", socketEventService);
        ReflectionTestUtils.setField(contactService, "emailService", emailService);
    }

    @Test
    void getContactDetails_withValidId_returnsSuccess() {
        ContactQueryDTO dto = mock(ContactQueryDTO.class);
        ContactDTO contactDTO = new ContactDTO();
        contactDTO.setPk(1L);

        when(contactRepository.getContactDetailsByPk(1L)).thenReturn(dto);
        when(contactMapper.mappingContactQueryDTOToContactDTO(dto)).thenReturn(contactDTO);

        ResponseDTO result = contactService.getContactDetails(1L);

        assertNotNull(result);
        assertEquals(MessageConstants.SUCCESS_STATUS, result.getStatus());
        assertEquals(MessageConstants.FETCHING_CONTACT_SUCCESS, result.getMsg());
    }

    @Test
    void getContactDetails_withNullId_returnsEmptyResponseWithoutCallingRepo() {
        ResponseDTO result = contactService.getContactDetails(null);

        assertNotNull(result);
        verify(contactRepository, times(0)).getContactDetailsByPk(anyLong());
    }

    @Test
    void createNewContact_withAssignedToUserFk_returnsSuccess() {
        Long creatorFk = 1L;
        ContactDTO contactDTO = buildContactDTO();
        contactDTO.setAssignedToUserFk(2L);

        User creator = buildUser(1L, "Creator");
        User assigned = buildUser(2L, "AssignedUser");
        Contact savedContact = new Contact();
        savedContact.setPk(10L);

        when(userRepository.findUserByPk(creatorFk)).thenReturn(creator);
        when(userRepository.findUserByPk(2L)).thenReturn(assigned);
        when(contactRepository.save(any(Contact.class))).thenReturn(savedContact);

        ResponseDTO result = contactService.createNewContact(contactDTO, creatorFk);

        assertNotNull(result);
        assertEquals(MessageConstants.SUCCESS_STATUS, result.getStatus());
        assertEquals(MessageConstants.CREATING_NEW_CONTACT_SUCCESS, result.getMsg());
    }

    @Test
    void createNewContact_withRepositoryException_throwsCRMException() {
        Long creatorFk = 1L;
        ContactDTO contactDTO = buildContactDTO();
        contactDTO.setAssignedToUserFk(2L);

        User creator = buildUser(1L, "Creator");
        User assigned = buildUser(2L, "AssignedUser");

        when(userRepository.findUserByPk(creatorFk)).thenReturn(creator);
        when(userRepository.findUserByPk(2L)).thenReturn(assigned);
        when(contactRepository.save(any(Contact.class))).thenThrow(new RuntimeException("DB error"));

        CRMException ex = assertThrows(CRMException.class,
            () -> contactService.createNewContact(contactDTO, creatorFk));

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
    }

    @Test
    void updateContactDetails_withSameCreator_returnsSuccess() {
        Long contactPk = 1L;
        Long creatorFk = 1L;
        ContactDTO contactDTO = buildContactDTO();
        contactDTO.setAssignedToUserFk(2L);

        User creator = buildUser(1L, "Creator");
        User assigned = buildUser(2L, "AssignedUser");
        Contact existing = buildContact(contactPk, creator);

        when(contactRepository.findByPk(contactPk)).thenReturn(Optional.of(existing));
        when(userRepository.findUserByPk(2L)).thenReturn(assigned);

        ResponseDTO result = contactService.updateContactDetails(contactPk, creatorFk, contactDTO);

        assertNotNull(result);
        assertEquals(MessageConstants.SUCCESS_STATUS, result.getStatus());
    }

    @Test
    void updateContactDetails_withDifferentCreator_throwsForbidden() {
        Long contactPk = 1L;
        User owner = buildUser(1L, "Owner");
        Contact existing = buildContact(contactPk, owner);

        when(contactRepository.findByPk(contactPk)).thenReturn(Optional.of(existing));

        CRMException ex = assertThrows(CRMException.class,
            () -> contactService.updateContactDetails(contactPk, 99L, new ContactDTO()));

        assertEquals(HttpStatus.FORBIDDEN, ex.getStatus());
    }

    @Test
    void updateContactDetails_withNotFoundContact_throwsNotFound() {
        when(contactRepository.findByPk(anyLong())).thenReturn(Optional.empty());

        CRMException ex = assertThrows(CRMException.class,
            () -> contactService.updateContactDetails(1L, 1L, new ContactDTO()));

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
    }

    @Test
    void deleteContactDetails_withSameCreator_returnsSuccess() {
        User creator = buildUser(1L, "Creator");
        Contact contact = buildContact(1L, creator);

        when(contactRepository.findByPk(1L)).thenReturn(Optional.of(contact));

        ResponseDTO result = contactService.deleteContactDetails(1L, 1L);

        assertEquals(MessageConstants.SUCCESS_STATUS, result.getStatus());
        assertEquals(MessageConstants.DELETING_CONTACT_SUCCESS, result.getMsg());
        verify(contactRepository, times(1)).delete(contact);
    }

    @Test
    void deleteContactDetails_withDifferentCreator_throwsForbidden() {
        User owner = buildUser(1L, "Owner");
        Contact contact = buildContact(1L, owner);

        when(contactRepository.findByPk(anyLong())).thenReturn(Optional.of(contact));

        CRMException ex = assertThrows(CRMException.class,
            () -> contactService.deleteContactDetails(1L, 99L));

        assertEquals(HttpStatus.FORBIDDEN, ex.getStatus());
    }

    @Test
    void deleteContactDetails_withNotFoundContact_throwsNotFound() {
        when(contactRepository.findByPk(anyLong())).thenReturn(Optional.empty());

        CRMException ex = assertThrows(CRMException.class,
            () -> contactService.deleteContactDetails(1L, 1L));

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
    }

    @Test
    void deleteContacts_withAllOwnContacts_returnsSuccess() {
        Long creatorFk = 1L;
        List<Long> ids = List.of(1L, 2L);
        User creator = buildUser(1L, "Creator");
        Contact c1 = buildContact(1L, creator);
        Contact c2 = buildContact(2L, creator);

        when(contactRepository.getContactsByContactPks(ids)).thenReturn(List.of(c1, c2));

        ResponseDTO result = contactService.deleteContacts(ids, creatorFk);

        assertEquals(MessageConstants.SUCCESS_STATUS, result.getStatus());
        assertEquals(MessageConstants.DELETING_LIST_OF_CONTACTS_SUCCESS, result.getMsg());
    }

    @Test
    void deleteContacts_withOtherCreatorContact_throwsForbidden() {
        Long creatorFk = 1L;
        List<Long> ids = List.of(1L, 2L);
        User creator = buildUser(1L, "Creator");
        User other = buildUser(2L, "Other");
        Contact c1 = buildContact(1L, creator);
        Contact c2 = buildContact(2L, other);

        when(contactRepository.getContactsByContactPks(ids)).thenReturn(List.of(c1, c2));

        CRMException ex = assertThrows(CRMException.class,
            () -> contactService.deleteContacts(ids, creatorFk));

        assertEquals(HttpStatus.FORBIDDEN, ex.getStatus());
    }

    @Test
    void retrieveSalutationEnumOfContact_returnsAllValues() {
        List<Salutation> result = contactService.retrieveSalutationEnumOfContact();

        assertNotNull(result);
        assertEquals(Salutation.values().length, result.size());
    }

    @Test
    void retrieveLeadSourceEnumOfContact_returnsAllValues() {
        List<LeadSource> result = contactService.retrieveLeadSourceEnumOfContact();

        assertNotNull(result);
        assertEquals(LeadSource.values().length, result.size());
    }

    @Test
    void retrieveContactDashboardByLeadSource_returnsGroupedData() {
        DashboardQueryDTO dto = buildDashboardQueryDTO(0, 5L);
        when(contactRepository.countContactGroupByLeadSource()).thenReturn(List.of(dto));

        ResponseDTO result = contactService.retrieveContactDashboardByLeadSource();

        assertNotNull(result);
        assertEquals(MessageConstants.SUCCESS_STATUS, result.getStatus());
        @SuppressWarnings("unchecked")
        List<DashboardDTO> data = (List<DashboardDTO>) result.getData();
        assertEquals(1, data.size());
        assertEquals(LeadSource.values()[0].getName(), data.get(0).getId());
        assertEquals(5L, data.get(0).getCount());
    }

    @Test
    void getListOfContactName_returnsNamesFromRepository() {
        ContactQueryDTO dto = mock(ContactQueryDTO.class);
        ContactDTO contactDTO = new ContactDTO();
        contactDTO.setContactName("John Doe");

        when(contactRepository.getAllContacts(null)).thenReturn(List.of(dto));
        when(contactMapper.mappingToListContactDTO(any())).thenReturn(List.of(contactDTO));

        ResponseDTO result = contactService.getListOfContactName();

        assertNotNull(result);
        assertEquals(MessageConstants.SUCCESS_STATUS, result.getStatus());
        @SuppressWarnings("unchecked")
        List<String> names = (List<String>) result.getData();
        assertEquals(1, names.size());
        assertEquals("John Doe", names.get(0));
    }

    @Test
    void getListOfContact_withNoPageSize_returnsAllContacts() {
        Contact contact = new Contact();
        contact.setPk(1L);
        ContactDTO contactDTO = new ContactDTO();
        contactDTO.setContactName("John");

        when(customRepository.findAll(any(Sort.class), any())).thenReturn(List.of());
        when(contactMapper.mapRecordList(any())).thenReturn(List.of(contact));
        when(contactMapper.mappingContactEntityToContactDTO(contact)).thenReturn(contactDTO);

        ResponseDTO result = contactService.getListOfContact(null, 0, 0, "pk", "ASC", null);

        assertNotNull(result);
        assertEquals(MessageConstants.SUCCESS_STATUS, result.getStatus());
        assertEquals(MessageConstants.FETCHING_LIST_OF_CONTACTS_SUCCESS, result.getMsg());
    }

    @Test
    void getListOfContact_withPageSize_returnsPagedContacts() {
        Page<Map<String, Object>> page = new PageImpl<>(List.of(), PageRequest.of(0, 10), 0);

        when(customRepository.findAll(any(Pageable.class), any())).thenReturn(page);
        when(contactMapper.mapRecordList(any())).thenReturn(List.of());

        ResponseDTO result = contactService.getListOfContact(null, 10, 0, "pk", "ASC", null);

        assertNotNull(result);
        assertEquals(MessageConstants.SUCCESS_STATUS, result.getStatus());
        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) result.getData();
        assertNotNull(data.get("totalRecord"));
    }

    @Test
    void getListOfContactWithNonDynamicFilter_withPagination_returnsPagedResult() {
        ContactQueryDTO dto = mock(ContactQueryDTO.class);
        ContactDTO contactDTO = buildContactDTO();
        Page<ContactQueryDTO> page = new PageImpl<>(List.of(dto));

        when(contactRepository.getContactsPageable(anyString(), any(Pageable.class))).thenReturn(page);
        when(contactMapper.mappingToListContactDTO(any())).thenReturn(List.of(contactDTO));

        ResponseDTO result = contactService.getListOfContactWithNonDynamicFilter(0, 10, "John");

        assertNotNull(result);
        assertEquals(MessageConstants.SUCCESS_STATUS, result.getStatus());
        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) result.getData();
        assertNotNull(data.get("totalRecord"));
    }

    @Test
    void getListOfContactWithNonDynamicFilter_withoutPagination_returnsAllContacts() {
        ContactQueryDTO dto = mock(ContactQueryDTO.class);
        ContactDTO contactDTO = buildContactDTO();

        when(contactRepository.getAllContacts("John")).thenReturn(List.of(dto));
        when(contactMapper.mappingToListContactDTO(any())).thenReturn(List.of(contactDTO));

        ResponseDTO result = contactService.getListOfContactWithNonDynamicFilter(null, null, "John");

        assertNotNull(result);
        assertEquals(MessageConstants.SUCCESS_STATUS, result.getStatus());
        assertNotNull(result.getData());
    }

    // --- Helper methods ---

    private ContactDTO buildContactDTO() {
        ContactDTO dto = new ContactDTO();
        dto.setContactName("John Doe");
        dto.setSalutation("Mr.");
        dto.setOrganization("OrgA");
        dto.setAssignedTo("AssignedUser");
        return dto;
    }

    private User buildUser(Long pk, String name) {
        User user = new User();
        user.setPk(pk);
        user.setName(name);
        return user;
    }

    private Contact buildContact(Long pk, User creator) {
        Contact contact = new Contact();
        contact.setPk(pk);
        contact.setCreator(creator);
        return contact;
    }

    private DashboardQueryDTO buildDashboardQueryDTO(int id, long count) {
        return new DashboardQueryDTO() {
            @Override
            public Integer getId() {
                return id;
            }

            @Override
            public Long getCount() {
                return count;
            }
        };
    }
}
