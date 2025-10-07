package com.tdbang.crm.specifications;

import com.tdbang.crm.enums.SearchOperation;

public class SearchCriteria {

    private String key;
    private SearchOperation operation;
    private Object value;
    private boolean orPredicate;

    public SearchCriteria(final String orPredicate, final String key, final SearchOperation operation, final Object value) {
        this.orPredicate = orPredicate != null && orPredicate.equals(SearchOperation.OR_PREDICATE_FLAG);
        this.key = key;
        this.operation = operation;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public SearchOperation getOperation() {
        return operation;
    }

    public Object getValue() {
        return value;
    }

    public boolean isOrPredicate() {
        return orPredicate;
    }

}
