package com.tdbang.crm.specifications;

import java.util.Date;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

public abstract class AbstractSpecification<T> implements Specification<T> {

    private static final long serialVersionUID = 1L;

    private SearchCriteria criteria;

    public AbstractSpecification(SearchCriteria criteria) {
        this.criteria = criteria;
    }

    @Override
    public jakarta.persistence.criteria.Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
        Predicate result = null;
        switch (getCriteria().getOperation()) {
            case EQUALITY:
                if (!isDateField()) {
                    result = builder.equal(root.get(getCriteria().getKey()), value());
                } else {
                    result = builder.equal(root.get(getCriteria().getKey()), valueToDate());
                }
                break;
            case NEGATION:
                if (!isDateField()) {
                    result = builder.notEqual(root.get(getCriteria().getKey()), value());
                } else {
                    result = builder.notEqual(root.get(getCriteria().getKey()), valueToDate());
                }
                break;
            case GREATER_THAN:
                if (!isDateField()) {
                    result = builder.greaterThan(root.get(getCriteria().getKey()), valueToString());
                } else {
                    result = builder.greaterThanOrEqualTo(root.get(getCriteria().getKey()), valueToDate());
                }
                break;
            case LESS_THAN:
                if (!isDateField()) {
                    result = builder.lessThan(root.get(getCriteria().getKey()), valueToString());
                } else {
                    result = builder.lessThan(root.get(getCriteria().getKey()), valueToDate());
                }
                break;
            case LIKE:
                if (!isDateField()) {
                    result = builder.like(root.get(getCriteria().getKey()), valueToString());
                }
                break;
            case STARTS_WITH:
                if (!isDateField()) {
                    result = builder.like(root.get(getCriteria().getKey()), valueToString() + "%");
                }
                break;
            case ENDS_WITH:
                if (!isDateField()) {
                    result = builder.like(root.get(getCriteria().getKey()), "%" + valueToString());
                }
                break;
            case CONTAINS:
                if (!isDateField()) {
                    result = builder.like(root.get(getCriteria().getKey()), "%" + valueToString() + "%");
                }
                break;
            default:
        }
        return result;
    }

    private Date valueToDate() {
        return new Date(Long.valueOf(getCriteria().getValue().toString()));
    }

    private String valueToString() {
        return getCriteria().getValue().toString();
    }

    private Object value() {
        return getCriteria().getValue();
    }

    protected abstract boolean isDateField();

    public SearchCriteria getCriteria() {
        return criteria;
    }

}
