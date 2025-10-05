package com.tdbang.crm.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.tdbang.crm.dtos.nativequerydto.ContactQueryDTO;
import com.tdbang.crm.dtos.nativequerydto.DashboardQueryDTO;
import com.tdbang.crm.entities.Contact;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Long> {
    public static final String SELECT_LIST_CONTACTS = "SELECT c.pk AS pk, c.contact_name AS contactName, c.salutation AS salutation, c.mobile_phone AS mobilePhone, c.email AS email, c.organization AS organization,"
            + " c.dob AS dateOfBirth, c.lead_src AS leadSrc, assignTo.name AS nameUserAssignedTo, assignTo.pk AS userFkAssignedTo, creator.name AS creatorName, creator.pk AS creatorFk,"
            + " c.address AS address, c.description AS description, c.created_on AS createdOn, c.updated_on AS updatedOn"
            + " FROM contact c"
            + " LEFT JOIN user assignTo ON c.assigned_to = assignTo.pk"
            + " LEFT JOIN user creator ON c.creator = creator.pk";

    public static final String CONTACT_NAME_FILTER = " WHERE :contactName IS NULL OR c.contact_name LIKE %:contactName%";

    @Query(value = SELECT_LIST_CONTACTS + CONTACT_NAME_FILTER
            , countQuery = "SELECT COUNT(c.pk) FROM contact c" + CONTACT_NAME_FILTER, nativeQuery = true)
    Page<ContactQueryDTO> getContactsPageable(String contactName, Pageable pageable);

    @Query(value = SELECT_LIST_CONTACTS + CONTACT_NAME_FILTER, nativeQuery = true)
    List<ContactQueryDTO> getAllContacts(String contactName);

    @Query(value = "SELECT c FROM Contact c WHERE c.contactName = :contactName")
    List<Contact> getContactsByContactName(String contactName);

    @Query(value = SELECT_LIST_CONTACTS + " WHERE c.pk = :contactPk", nativeQuery = true)
    ContactQueryDTO getContactDetailsByPk(Long contactPk);

    Optional<Contact> findByPk(Long pk);

    @Query(value = "SELECT c.lead_src AS id, COUNT(c.pk) AS count"
            + " FROM contact c GROUP BY c.lead_src", nativeQuery = true)
    List<DashboardQueryDTO> countContactGroupByLeadSource();

    @Query(value = "SELECT c FROM Contact c WHERE c.pk IN (:pks)")
    List<Contact> getContactsByContactPks(List<Long> pks);
}
