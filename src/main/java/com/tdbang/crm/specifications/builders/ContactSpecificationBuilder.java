package com.tdbang.crm.specifications.builders;

import org.springframework.data.jpa.domain.Specification;

import com.tdbang.crm.entities.Contact;
import com.tdbang.crm.specifications.ContactSpecification;

public class ContactSpecificationBuilder extends SpecificationBuilder<Contact> {

    @Override
    public Specification<Contact> build() {
        if (getParams().isEmpty()) {
            return null;
        }
        Specification<Contact> result = new ContactSpecification(getParams().get(0));
        for (int i = 1; i < getParams().size(); i++) {
            result = getParams().get(i).isOrPredicate()
                    ? Specification.anyOf(result, new ContactSpecification(getParams().get(i)))
                    : Specification.allOf(result, new ContactSpecification(getParams().get(i)));
        }
        return result;
    }


}
