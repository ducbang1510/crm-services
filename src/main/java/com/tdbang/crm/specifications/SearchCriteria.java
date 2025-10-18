/*
 * Copyright © 2025 by tdbang.
 * All rights reserved.
 */

package com.tdbang.crm.specifications;

import lombok.Getter;

import com.tdbang.crm.enums.SearchOperation;

@Getter
public class SearchCriteria {

    private final String key;
    private final SearchOperation operation;
    private final Object value;
    private final boolean orPredicate;

    public SearchCriteria(final String orPredicate, final String key, final SearchOperation operation, final Object value) {
        this.orPredicate = orPredicate != null && orPredicate.equals(SearchOperation.OR_PREDICATE_FLAG);
        this.key = key;
        this.operation = operation;
        this.value = value;
    }

}
