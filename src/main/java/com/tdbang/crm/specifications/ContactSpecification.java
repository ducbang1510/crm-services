package com.tdbang.crm.specifications;

import java.util.Set;

import org.springframework.data.jpa.domain.Specification;

import com.tdbang.crm.entities.Contact;

public class ContactSpecification extends AbstractSpecification<Contact> implements Specification<Contact> {

    private static final long serialVersionUID = 1L;

    protected static final Set<String> DATE_FIELDS = Set.of("dob", "createdOn", "updatedOn");

    protected static final Set<String> ENUM_FIELDS = Set.of("salutation", "leadSrc");

    public ContactSpecification(SearchCriteria criteria) {
        super(criteria);
    }

    @Override
    protected boolean isDateField() {
        return DATE_FIELDS.contains(getCriteria().getKey());
    }

    @Override
    protected boolean isEnumField() {
        return ENUM_FIELDS.contains(getCriteria().getKey());
    }

}
