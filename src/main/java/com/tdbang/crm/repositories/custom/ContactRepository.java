package com.tdbang.crm.repositories.custom;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Tuple;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Repository;

import com.tdbang.crm.entities.Contact;

@Repository
public class ContactRepository extends CustomRepository<Contact> {
    protected ContactRepository(EntityManager entityManager) {
        super(entityManager);
    }

    @Override
    protected Root<Contact> getTupleRoot(CriteriaQuery<Tuple> query) {
        return query.from(Contact.class);
    }

    @Override
    protected Root<Contact> getLongRoot(CriteriaQuery<Long> query) {
        return query.from(Contact.class);
    }
}
