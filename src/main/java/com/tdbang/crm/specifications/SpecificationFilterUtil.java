package com.tdbang.crm.specifications;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.tdbang.crm.enums.SearchOperation;
import com.tdbang.crm.specifications.builders.SpecificationBuilder;

@Component
public class SpecificationFilterUtil<S> {

    public static final String FILTER_SEPARATOR = ",";

    public SpecificationBuilder<S> withFilter(SpecificationBuilder<S> builder, String filter) {
        String[] filters = filter.split(FILTER_SEPARATOR);
        for (String s : filters) {
            String operator = findOperator(s);
            if (null != operator) {
                String key = s.split(operator)[0].trim();
                String value = s.split(operator)[1].trim();
                builder.with(orPredicate(key) ? SearchOperation.OR_PREDICATE_FLAG : StringUtils.EMPTY, stripOrPredicate(key), operator,
                        stripPrefixSuffix(value), hasPrefix(value) ? SearchOperation.ZERO_OR_MORE_REGEX : StringUtils.EMPTY,
                        hasSuffix(value) ? SearchOperation.ZERO_OR_MORE_REGEX : StringUtils.EMPTY);
            }
        }
        return builder;
    }

    private boolean orPredicate(String key) {
        return key.startsWith(SearchOperation.OR_PREDICATE_FLAG);
    }

    private String stripOrPredicate(String key) {
        if (orPredicate(key)) {
            return key.substring(1);
        } else {
            return key;
        }
    }

    private String stripPrefixSuffix(String value) {
        if (hasPrefix(value)) {
            value = value.substring(1);
        }
        if (hasSuffix(value)) {
            value = value.substring(0, value.length() - 1);
        }
        return value;
    }

    private boolean hasPrefix(String value) {
        return value.startsWith(SearchOperation.ZERO_OR_MORE_REGEX);
    }

    private boolean hasSuffix(String value) {
        return value.endsWith(SearchOperation.ZERO_OR_MORE_REGEX);
    }

    private String findOperator(String filter) {
        for (int i = 0; i < SearchOperation.SIMPLE_OPERATION_SET.length; i++) {
            int operationIndex = filter.indexOf(SearchOperation.SIMPLE_OPERATION_SET[i]);
            if (operationIndex != -1) {
                return (SearchOperation.SIMPLE_OPERATION_SET[i]);
            }
        }
        return null;
    }

}
