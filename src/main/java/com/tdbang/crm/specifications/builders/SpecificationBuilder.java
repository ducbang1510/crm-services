package com.tdbang.crm.specifications.builders;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.tdbang.crm.enums.SearchOperation;
import com.tdbang.crm.specifications.SearchCriteria;

public abstract class SpecificationBuilder <T> {
    private final List<SearchCriteria> params;

    public SpecificationBuilder() {
        params = new ArrayList<>();
    }

    public final SpecificationBuilder<T> with(String key, String operation, Object value, String prefix,
                                              String suffix) {
        return with(null, key, operation, value, prefix, suffix);
    }

    public final SpecificationBuilder<T> with(String orPredicate, String key, String operation, Object value,
                                              String prefix, String suffix) {
        SearchOperation op = SearchOperation.getSimpleOperation(operation.charAt(0));
        if (null != op) {
            if (op == SearchOperation.EQUALITY) { // the operation may be complex operation
                boolean startWithAsterisk = prefix != null && prefix.contains(SearchOperation.ZERO_OR_MORE_REGEX);
                boolean endWithAsterisk = suffix != null && suffix.contains(SearchOperation.ZERO_OR_MORE_REGEX);
                if (startWithAsterisk && endWithAsterisk) {
                    op = SearchOperation.CONTAINS;
                } else if (startWithAsterisk) {
                    op = SearchOperation.ENDS_WITH;
                } else if (endWithAsterisk) {
                    op = SearchOperation.STARTS_WITH;
                }
            }
            SearchCriteria searchCriteria = new SearchCriteria(orPredicate, key, op, value);
            params.add(searchCriteria);
        }
        return this;
    }

    public List<SearchCriteria> getParams() {
        return params;
    }

    public abstract Specification<T> build();
}
