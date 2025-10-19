/*
 * Copyright © 2025 by tdbang.
 * All rights reserved.
 */

package com.tdbang.crm.specifications.builders;

import org.springframework.data.jpa.domain.Specification;

import com.tdbang.crm.entities.User;
import com.tdbang.crm.specifications.UserSpecification;

public class UserSpecificationBuilder extends SpecificationBuilder<User> {
    @Override
    public Specification<User> build() {
        if (getParams().isEmpty()) {
            return null;
        }
        Specification<User> result = new UserSpecification(getParams().get(0));
        for (int i = 1; i < getParams().size(); i++) {
            result = getParams().get(i).isOrPredicate()
                ? Specification.anyOf(result, new UserSpecification(getParams().get(i)))
                : Specification.allOf(result, new UserSpecification(getParams().get(i)));
        }
        return result;
    }
}
