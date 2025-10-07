package com.tdbang.crm.services;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.naming.directory.InvalidSearchFilterException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.tdbang.crm.repositories.custom.CustomRepository;
import com.tdbang.crm.specifications.SpecificationFilterUtil;
import com.tdbang.crm.specifications.builders.SpecificationBuilder;

@Service
public abstract class AbstractService<S> {

    public static final String TOTAL_RECORD_KEY = "totalRecords";
    public static final String RECORDS_KEY = "records";

    private com.tdbang.crm.specifications.SpecificationFilterUtil<S> filterUtil;

    private CustomRepository<S> repository;

    public AbstractService(SpecificationFilterUtil<S> filterUtil, CustomRepository<S> repository) {
        this.filterUtil = filterUtil;
        this.repository = repository;
    }

    /**
     * Get all records matching the provided parameters
     *
     * @param filter     The comma separated filters to be applied
     * @param pageSize   The requested page size
     * @param pageNumber The requested page number
     * @param sortColumn The column to sort on
     * @param sortOrder  The sort order
     *                   {@link org.springframework.data.domain.Sort.Direction}
     * @param fields     The fields to be returned in the records
     * @return The records matching the provided parameters
     * @throws InvalidSearchFilterException
     */
    public Map<String, Object> get(String filter, int pageSize, int pageNumber, String sortColumn, String sortOrder,
                                   Set<String> fields) {
        if (pageSize == 0) {
            return getResponse(filter, sortColumn, sortOrder, null == fields ? new HashSet<>() : fields);
        } else {
            return getPagedResponse(filter, pageSize, pageNumber, sortColumn, sortOrder,
                    null == fields ? new HashSet<>() : fields);
        }
    }

    private List<Map<String, Object>> find(Set<String> fields) {
        return repository.findAll(getFields(fields));
    }

    private List<Map<String, Object>> find(Specification<S> specification, Set<String> fields) {
        return repository.findAll(specification, getFields(fields));
    }

    private List<Map<String, Object>> find(Sort sort, Set<String> fields) {
        return repository.findAll(sort, getFields(fields));
    }

    private List<Map<String, Object>> find(Specification<S> specification, Sort sort, Set<String> fields) {
        return repository.findAll(specification, sort, getFields(fields));
    }

    private Page<Map<String, Object>> find(Pageable pageable, Set<String> fields) {
        return repository.findAll(pageable, getFields(fields));
    }

    private Page<Map<String, Object>> find(Specification<S> specification, Pageable pageable, Set<String> fields) {
        return repository.findAll(specification, pageable, getFields(fields));
    }

    private Specification<S> buildSpecification(String filter) {
        return filterUtil.withFilter(getSpecificationBuilder(), filter).build();
    }

    private Pageable getPageable(int pageSize, int pageNumber, String sortColumn, String sortOrder) {
        Pageable pageable = null;
        if (StringUtils.hasLength(sortColumn)) {
            pageable = PageRequest.of(pageNumber, pageSize, getSort(sortColumn, sortOrder));
        } else {
            pageable = PageRequest.of(pageNumber, pageSize);
        }
        return pageable;
    }

    private Sort getSort(String sortColumn, String sortOrder) {
        if (StringUtils.hasLength(sortOrder)
                && StringUtils.startsWithIgnoreCase(sortOrder, Sort.Direction.DESC.name())) {
            return Sort.by(getSortColumn(sortColumn)).descending();
        } else {
            return Sort.by(getSortColumn(sortColumn));
        }
    }

    private Set<String> getFields(Set<String> fields) {
        return (null == fields || fields.isEmpty()) ? getProfileFieldsSet() : fields;
    }

    private Set<String> getProfileFieldsSet() {
        return Arrays.stream(getProfileFields().split(",")).map(String::trim).collect(Collectors.toSet());
    }

    private Map<String, Object> getPagedResponse(String filter, int pageSize, int pageNumber, String sortColumn,
                                                 String sortOrder, Set<String> fields) throws IllegalArgumentException {
        Map<String, Object> result = new HashMap<String, Object>();
        Pageable pageable = getPageable(pageSize, pageNumber, sortColumn, sortOrder);
        Page<Map<String, Object>> records = find(filter, pageable, fields);
        result.put(RECORDS_KEY, records.getContent());
        result.put(TOTAL_RECORD_KEY, records.getTotalElements());
        return result;
    }

    private Map<String, Object> getResponse(String filter, String sortColumn, String sortOrder, Set<String> fields) {
        Map<String, Object> result = new HashMap<String, Object>();
        List<Map<String, Object>> records;
        if (StringUtils.hasLength(sortColumn)) {
            Sort sort = getSort(sortColumn, sortOrder);
            records = find(filter, sort, fields);
        } else {
            records = find(filter, fields);
        }
        result.put(RECORDS_KEY, records);
        result.put(TOTAL_RECORD_KEY, records.size());
        return result;
    }

    private Page<Map<String, Object>> find(String filter, Pageable pageable, Set<String> fields) {
        Page<Map<String, Object>> records;
        if (StringUtils.hasLength(filter)) {
            Specification<S> specification = build(filter);
            records = find(specification, pageable, fields);
        } else {
            records = find(pageable, fields);
        }
        return records;
    }

    private List<Map<String, Object>> find(String filter, Sort sort, Set<String> fields) {
        List<Map<String, Object>> records;
        if (StringUtils.hasLength(filter)) {
            Specification<S> specification = build(filter);
            records = find(specification, sort, fields);
        } else {
            records = find(sort, fields);
        }
        return records;
    }

    private List<Map<String, Object>> find(String filter, Set<String> fields) {
        List<Map<String, Object>> records;
        if (StringUtils.hasLength(filter)) {
            Specification<S> specification = build(filter);
            records = find(specification, fields);
        } else {
            records = find(fields);
        }
        return records;
    }

    private String getSortColumn(String sortColumn) {
        return (Arrays.asList(getEntityClass().getDeclaredFields()).stream()
                .anyMatch(f -> f.getName().equals(sortColumn)) ? sortColumn : getDefaultSortColumn());
    }

    private Specification<S> build(String filter) {
        Specification<S> specification = buildSpecification(filter);
        if (null == specification) {
            throw new IllegalArgumentException("Invalid filter specified: " + filter);
        }
        return specification;
    }

    protected abstract String getProfileFields();

    protected abstract String getDefaultSortColumn();

    protected abstract Class<S> getEntityClass();

    protected abstract SpecificationBuilder<S> getSpecificationBuilder();

}
