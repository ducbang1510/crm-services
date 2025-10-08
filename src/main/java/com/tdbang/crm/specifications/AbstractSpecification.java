package com.tdbang.crm.specifications;

import java.util.Date;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

public abstract class AbstractSpecification<T> implements Specification<T> {

    private static final long serialVersionUID = 1L;

    private final SearchCriteria criteria;

    public AbstractSpecification(SearchCriteria criteria) {
        this.criteria = criteria;
    }

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
        Predicate result = null;
        Path<?> path = getPath(root, criteria.getKey());
        switch (criteria.getOperation()) {
            case EQUALITY:
                if (isEnumField()) {
                    result = builder.equal(path, valueToEnum(path));
                } else if (!isDateField()) {
                    result = builder.equal(path, value());
                } else {
                    result = builder.equal(path, valueToDate());
                }
                break;
            case NEGATION:
                if (isEnumField()) {
                    result = builder.notEqual(path, valueToEnum(path));
                } else if (!isDateField()) {
                    result = builder.notEqual(path, value());
                } else {
                    result = builder.notEqual(path, valueToDate());
                }
                break;
            case GREATER_THAN:
                if (!isDateField()) {
                    result = builder.greaterThan(root.get(criteria.getKey()), valueToString());
                } else {
                    result = builder.greaterThanOrEqualTo(root.get(criteria.getKey()), valueToDate());
                }
                break;
            case LESS_THAN:
                if (!isDateField()) {
                    result = builder.lessThan(root.get(criteria.getKey()), valueToString());
                } else {
                    result = builder.lessThan(root.get(criteria.getKey()), valueToDate());
                }
                break;
            case LIKE:
                if (!isDateField()) {
                    result = builder.like(root.get(criteria.getKey()), valueToString());
                }
                break;
            case STARTS_WITH:
                if (!isDateField()) {
                    result = builder.like(root.get(criteria.getKey()), valueToString() + "%");
                }
                break;
            case ENDS_WITH:
                if (!isDateField()) {
                    result = builder.like(root.get(criteria.getKey()), "%" + valueToString());
                }
                break;
            case CONTAINS:
                if (!isDateField()) {
                    result = builder.like(root.get(criteria.getKey()), "%" + valueToString() + "%");
                }
                break;
            default:
        }
        return result;
    }

    /**
     * Support nested paths
     */
    private Path<?> getPath(From<?, ?> root, String fieldName) {
        if (!fieldName.contains(".")) {
            return root.get(fieldName);
        }

        String[] parts = fieldName.split("\\.");
        Path<?> path = root;
        for (String part : parts) {
            if (path.get(part) instanceof From) {
                path = ((From<?, ?>) path).join(part, JoinType.LEFT);
            } else {
                path = path.get(part);
            }
        }
        return path;
    }

    private Enum valueToEnum(Path<?> path) {
        Class<?> type = path.getJavaType();
        @SuppressWarnings("unchecked")
        Class<? extends Enum<?>> enumType = (Class<? extends Enum<?>>) type;
        return  Enum.valueOf((Class) enumType, criteria.getValue().toString().toUpperCase());
    }

    private Date valueToDate() {
        return new Date(Long.parseLong(criteria.getValue().toString()));
    }

    private String valueToString() {
        return criteria.getValue().toString();
    }

    private Object value() {
        return criteria.getValue();
    }

    protected abstract boolean isDateField();

    protected abstract boolean isEnumField();

    public SearchCriteria getCriteria() {
        return criteria;
    }

}
