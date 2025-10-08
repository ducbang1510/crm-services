package com.tdbang.crm.specifications;

import java.util.Set;

import org.springframework.data.jpa.domain.Specification;

import com.tdbang.crm.entities.SalesOrder;

public class SalesOrderSpecification extends AbstractSpecification<SalesOrder> implements Specification<SalesOrder> {

    private static final long serialVersionUID = 1L;

    protected static final Set<String> DATE_FIELDS = Set.of("createdOn", "updatedOn");

    protected static final Set<String> ENUM_FIELDS = Set.of("status");

    public SalesOrderSpecification(SearchCriteria criteria) {
        super(criteria);
    }

    @Override
    protected boolean isDateField() {
        return false;
    }

    @Override
    protected boolean isEnumField() {
        return false;
    }
}
