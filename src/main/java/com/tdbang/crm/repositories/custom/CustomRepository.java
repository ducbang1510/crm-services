/*
 * Copyright © 2025 by tdbang.
 * All rights reserved.
 */

package com.tdbang.crm.repositories.custom;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Tuple;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Selection;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.jpa.domain.Specification;

/**
 * Custom Repository.
 */
public abstract class CustomRepository<S> {
    private final EntityManager entityManager;

    protected CustomRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public List<S> findAllEntityResult(Set<String> fields) {
        CriteriaQuery<Tuple> query = entityManager.getCriteriaBuilder().createTupleQuery();
        Root<S> root = getTupleRoot(query);
        addSelection(query, root, fields);
        return getEntityResults(query, fields);
    }

    public List<S> findAllEntityResult(Specification<S> specification, Set<String> fields) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> query = builder.createTupleQuery();
        Root<S> root = getTupleRoot(query);
        addSelection(query, root, fields);
        addPredicate(specification, root, builder, query);
        return getEntityResults(query, fields);
    }

    public List<S> findAllEntityResult(Sort sort, Set<String> fields) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> query = builder.createTupleQuery();
        Root<S> root = getTupleRoot(query);
        orderBy(query, root, builder, sort);
        addSelection(query, root, fields);
        return getEntityResults(query, fields);
    }

    public List<S> findAllEntityResult(Specification<S> specification, Sort sort, Set<String> fields) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> query = builder.createTupleQuery();
        Root<S> root = getTupleRoot(query);
        orderBy(query, root, builder, sort);
        addSelection(query, root, fields);
        addPredicate(specification, root, builder, query);
        return getEntityResults(query, fields);
    }

    public Page<S> findAllEntityResult(Pageable pageable, Set<String> fields) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> countQuery = builder.createQuery(Long.class);
        Root<S> countQueryRoot = getLongRoot(countQuery);
        addSelection(countQuery, countQueryRoot, builder);

        CriteriaQuery<Tuple> query = builder.createTupleQuery();
        Root<S> queryRoot = getTupleRoot(query);
        addSelection(query, queryRoot, fields);
        orderBy(query, queryRoot, builder, pageable.getSort());
        return getEntityResults(query, countQuery, pageable, fields);
    }

    public Page<S> findAllEntityResult(Specification<S> specification, Pageable pageable, Set<String> fields) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> countQuery = builder.createQuery(Long.class);
        Root<S> countQueryRoot = getLongRoot(countQuery);
        addPredicate(specification, countQueryRoot, builder, countQuery);
        addSelection(countQuery, countQueryRoot, builder);

        CriteriaQuery<Tuple> query = builder.createTupleQuery();
        Root<S> queryRoot = getTupleRoot(query);
        addSelection(query, queryRoot, fields);
        addPredicate(specification, queryRoot, builder, query);
        orderBy(query, queryRoot, builder, pageable.getSort());
        return getEntityResults(query, countQuery, pageable, fields);
    }

    public List<Map<String, Object>> findAll(Set<String> fields) {
        CriteriaQuery<Tuple> query = entityManager.getCriteriaBuilder().createTupleQuery();
        Root<S> root = getTupleRoot(query);
        addSelection(query, root, fields);
        return getResults(query, fields);
    }

    public List<Map<String, Object>> findAll(Specification<S> specification, Set<String> fields) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> query = builder.createTupleQuery();
        Root<S> root = getTupleRoot(query);
        addSelection(query, root, fields);
        addPredicate(specification, root, builder, query);
        return getResults(query, fields);
    }

    public Page<Map<String, Object>> findAll(Pageable pageable, Set<String> fields) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> countQuery = builder.createQuery(Long.class);
        Root<S> countQueryRoot = getLongRoot(countQuery);
        addSelection(countQuery, countQueryRoot, builder);

        CriteriaQuery<Tuple> query = builder.createTupleQuery();
        Root<S> queryRoot = getTupleRoot(query);
        addSelection(query, queryRoot, fields);
        orderBy(query, queryRoot, builder, pageable.getSort());
        return getResults(query, countQuery, pageable, fields);
    }

    public List<Map<String, Object>> findAll(Sort sort, Set<String> fields) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> query = builder.createTupleQuery();
        Root<S> root = getTupleRoot(query);
        orderBy(query, root, builder, sort);
        addSelection(query, root, fields);
        return getResults(query, fields);
    }

    public List<Map<String, Object>> findAll(Specification<S> specification, Sort sort, Set<String> fields) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> query = builder.createTupleQuery();
        Root<S> root = getTupleRoot(query);
        orderBy(query, root, builder, sort);
        addSelection(query, root, fields);
        addPredicate(specification, root, builder, query);
        return getResults(query, fields);
    }

    public Page<Map<String, Object>> findAll(Specification<S> specification, Pageable pageable, Set<String> fields) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> countQuery = builder.createQuery(Long.class);
        Root<S> countQueryRoot = getLongRoot(countQuery);
        addPredicate(specification, countQueryRoot, builder, countQuery);
        addSelection(countQuery, countQueryRoot, builder);

        CriteriaQuery<Tuple> query = builder.createTupleQuery();
        Root<S> queryRoot = getTupleRoot(query);
        addSelection(query, queryRoot, fields);
        addPredicate(specification, queryRoot, builder, query);
        orderBy(query, queryRoot, builder, pageable.getSort());
        return getResults(query, countQuery, pageable, fields);
    }

    private void addPredicate(Specification<S> specification, Root<S> root, CriteriaBuilder builder,
                              CriteriaQuery<?> query) {
        Predicate predicate = specification.toPredicate(root, query, builder);
        if (predicate != null) {
            query.where(predicate);
        }
    }

    private void orderBy(CriteriaQuery<Tuple> query, Root<S> root, CriteriaBuilder builder, Sort sort) {
        if (!sort.toList().isEmpty()) {
            Order firstOrder = sort.toList().get(0);
            String sortProperty = firstOrder.getProperty();
            if (firstOrder.getDirection().isAscending()) {
                query.orderBy(builder.asc(root.get(sortProperty)));
            } else {
                query.orderBy(builder.desc(root.get(sortProperty)));
            }
        }
    }

    private void addSelection(CriteriaQuery<Tuple> query, Root<S> root, Set<String> fields) {
        List<Selection<Object>> selections = createSelectionList(root, fields);
        query.multiselect(selections.toArray(new Selection[0]));
    }

    private void addSelection(CriteriaQuery<Long> query, Root<S> root, CriteriaBuilder builder) {
        query.select(builder.count(root));
    }

    private List<Map<String, Object>> getResults(CriteriaQuery<Tuple> query, Set<String> fields) {
        List<Map<String, Object>> results = new ArrayList<>();
        try {
            List<Tuple> result = entityManager.createQuery(query).getResultList();
            result.stream().forEach((record) -> {
                Map<String, Object> resultMap = new HashMap<>();
                fields.forEach(f -> resultMap.put(f, record.get(f)));
                results.add(resultMap);
            });
        } catch (Exception e) {
            throw new DataAccessResourceFailureException(e.getMessage());
        }
        return results;
    }

    private Page<Map<String, Object>> getResults(CriteriaQuery<Tuple> query, CriteriaQuery<Long> countQuery,
                                                 Pageable page, Set<String> fields) {
        List<Map<String, Object>> results = new ArrayList<>();
        try {
            Long count = entityManager.createQuery(countQuery).getSingleResult();
            List<Tuple> result = entityManager.createQuery(query)
                .setFirstResult(page.getPageNumber() * page.getPageSize()).setMaxResults(page.getPageSize())
                .getResultList();
            result.stream().forEach((record) -> {
                Map<String, Object> resultMap = new HashMap<>();
                fields.forEach(f -> resultMap.put(f, record.get(f)));
                results.add(resultMap);
            });
            return new PageImpl<>(results, page, count);
        } catch (Exception e) {
            throw new DataAccessResourceFailureException(e.getMessage());
        }
    }

    private List<S> getEntityResults(CriteriaQuery<Tuple> query, Set<String> fields) {
        try {
            Class<S> entityType = getEntityClass();
            List<Tuple> tuples = entityManager.createQuery(query).getResultList();
            List<S> results = new ArrayList<>();

            for (Tuple tuple : tuples) {
                S entity = entityType.getDeclaredConstructor().newInstance();
                for (String field : fields) {
                    Object value = tuple.get(field);
                    if (value != null) {
                        setFieldValue(entity, field, value);
                    }
                }
                results.add(entity);
            }
            return results;

        } catch (Exception e) {
            throw new DataAccessResourceFailureException(e.getMessage(), e);
        }
    }

    private Page<S> getEntityResults(CriteriaQuery<Tuple> query, CriteriaQuery<Long> countQuery,
                                     Pageable page, Set<String> fields) {
        List<S> results = new ArrayList<>();
        try {
            Class<S> entityType = getEntityClass();
            Long count = entityManager.createQuery(countQuery).getSingleResult();
            List<Tuple> tuples = entityManager.createQuery(query)
                .setFirstResult(page.getPageNumber() * page.getPageSize()).setMaxResults(page.getPageSize())
                .getResultList();

            for (Tuple tuple : tuples) {
                S entity = entityType.getDeclaredConstructor().newInstance();
                for (String field : fields) {
                    Object value = tuple.get(field);
                    if (value != null) {
                        setFieldValue(entity, field, value);
                    }
                }
                results.add(entity);
            }
            return new PageImpl<>(results, page, count);
        } catch (Exception e) {
            throw new DataAccessResourceFailureException(e.getMessage());
        }
    }

    private void setFieldValue(S target, String fieldName, Object value) {
        try {
            Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            // skip if field not found or cannot set (optional: log warning)
        }
    }

    private List<Selection<Object>> createSelectionList(Root<S> root, Set<String> fields) {
        List<Selection<Object>> selections = new ArrayList<>();
        fields.forEach(f -> selections.add(createSelection(root, f, f)));
        return selections;
    }

    private Selection<Object> createSelection(Root<S> root, String attribute, String alias) {
        return root.get(attribute).alias(alias);
    }

    protected abstract Root<S> getTupleRoot(CriteriaQuery<Tuple> query);

    protected abstract Root<S> getLongRoot(CriteriaQuery<Long> query);

    protected abstract Class<S> getEntityClass();

}
