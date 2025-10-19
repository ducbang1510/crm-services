/*
 * Copyright © 2025 by tdbang.
 * All rights reserved.
 */

package com.tdbang.crm.utils;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.mysql.cj.util.StringUtils;
import org.springframework.data.domain.Sort;

import com.tdbang.crm.commons.SortableFields;

public class AppUtils {

    private AppUtils() {
        throw new IllegalStateException("Utility class");
    }

    private static final String COMMA = ",";

    public static Set<String> convertFields(String fields) {
        Set<String> fieldsSet = new HashSet<>();
        if (null != fields) {
            fieldsSet = new HashSet<>(Arrays.asList(fields.split(COMMA)));
        }
        return fieldsSet;
    }

    public static Sort getPageableSort(Class<?> tclass, String methodName, String sortColumn, String sortOrder) {
        Sort sort = null;
        Method method = Arrays.stream(tclass.getDeclaredMethods()).filter(m -> methodName.equalsIgnoreCase(m.getName()))
            .findFirst().orElse(null);
        if (method != null && method.isAnnotationPresent(SortableFields.class)) {
            SortableFields sortableFields = method.getAnnotation(SortableFields.class);
            String sortValue = sortableFields.defaultColumn();
            if (!StringUtils.isNullOrEmpty(sortColumn) &&
                Arrays.asList(sortableFields.columns().split(",")).contains(sortColumn)) {
                sortValue = sortColumn;
            }

            sort = Sort.by(sortValue);

            if (!StringUtils.isNullOrEmpty(sortOrder) && StringUtils.startsWithIgnoreCase(sortOrder, "DESC")) {
                return Sort.by(sortValue).descending();
            }
        }

        return sort;
    }
}
