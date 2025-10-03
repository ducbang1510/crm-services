package com.tdbang.crm.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.tdbang.crm.dtos.nativequerydto.ContactQueryDTO;
import com.tdbang.crm.entities.Contact;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Long> {
    public static final String SELECT_LIST_CONTACTS = "SELECT c.pk AS pk, c.contact_name AS contactName, c.salutation AS salutation, c.mobile_phone AS mobilePhone, c.email AS email, c.organization AS organization,"
            + " c.lead_src AS leadSrc, assignTo.name AS nameUserAssignedTo, assignTo.pk AS userFkAssignedTo, creator.name AS creatorName, creator.pk AS creatorFk,"
            + " c.address AS address, c.description AS description, c.created_on AS createdOn, c.updated_on AS updatedOn"
            + " FROM contact c"
            + " LEFT JOIN user assignTo ON c.assigned_to = assignTo.pk"
            + " LEFT JOIN user creator ON c.creator = creator.pk";

    @Query(value = SELECT_LIST_CONTACTS
            , countQuery = "SELECT COUNT(pk) FROM contact", nativeQuery = true)
    Page<ContactQueryDTO> getContactsPageable(Pageable pageable);

    @Query(value = SELECT_LIST_CONTACTS, nativeQuery = true)
    List<ContactQueryDTO> getAllContacts();
}
