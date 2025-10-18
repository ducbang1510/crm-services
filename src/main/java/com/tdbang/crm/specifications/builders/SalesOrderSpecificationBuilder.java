/*
 * Copyright © 2025 by tdbang.
 * All rights reserved.
 */

package com.tdbang.crm.specifications.builders;

import org.springframework.data.jpa.domain.Specification;

import com.tdbang.crm.entities.SalesOrder;
import com.tdbang.crm.specifications.SalesOrderSpecification;

public class SalesOrderSpecificationBuilder extends SpecificationBuilder<SalesOrder> {
    @Override
    public Specification<SalesOrder> build() {
        if (getParams().isEmpty()) {
            return null;
        }
        Specification<SalesOrder> result = new SalesOrderSpecification(getParams().get(0));
        for (int i = 1; i < getParams().size(); i++) {
            result = getParams().get(i).isOrPredicate()
                    ? Specification.anyOf(result, new SalesOrderSpecification(getParams().get(i)))
                    : Specification.allOf(result, new SalesOrderSpecification(getParams().get(i)));
        }
        return result;
    }
}
