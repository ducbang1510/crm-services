/*
 * Copyright © 2025 by tdbang.
 * All rights reserved.
 */

package com.tdbang.crm.specifications;

import java.util.Set;

import org.springframework.data.jpa.domain.Specification;

import com.tdbang.crm.entities.User;

public class UserSpecification extends AbstractSpecification<User> implements Specification<User> {

    private static final long serialVersionUID = 1L;

    protected static final Set<String> DATE_FIELDS = Set.of("createdOn", "updatedOn");

    protected static final Set<String> ENUM_FIELDS = Set.of();

    protected static final Set<String> BOOLEAN_FIELDS = Set.of("isAdmin", "isStaff", "isActive");

    public UserSpecification(SearchCriteria criteria) {
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

    @Override
    protected boolean isBooleanField() {
        return BOOLEAN_FIELDS.contains(getCriteria().getKey());
    }

}
