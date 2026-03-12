/*
 * Copyright © 2026 by tdbang.
 * All rights reserved.
 */

package com.tdbang.crm.batch.email;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.batch.item.database.JpaPagingItemReader;

import com.tdbang.crm.entities.SalesOrder;
import com.tdbang.crm.enums.SalesOrderStatus;

/**
 * Reads {@link SalesOrder} records in actionable statuses (CREATED or APPROVED)
 * that have not been updated within the configured inactivity window and have
 * an assigned user with a valid email.
 */
public class SalesOrderReminderItemReader extends JpaPagingItemReader<SalesOrder> {

    private static final String QUERY =
        "SELECT s FROM SalesOrder s"
            + " WHERE s.status IN :statuses"
            + " AND s.updatedOn < :cutoffDate"
            + " AND s.assignedTo IS NOT NULL"
            + " AND s.assignedTo.email IS NOT NULL"
            + " ORDER BY s.pk ASC";

    private static final int PAGE_SIZE = 10;

    public SalesOrderReminderItemReader(EntityManagerFactory emf, int inactivityDays) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, -inactivityDays);

        List<SalesOrderStatus> statuses =
            List.of(SalesOrderStatus.CREATED, SalesOrderStatus.APPROVED);

        setName("salesOrderReminderItemReader");
        setEntityManagerFactory(emf);
        setQueryString(QUERY);
        setParameterValues(Map.of("cutoffDate", cal.getTime(), "statuses", statuses));
        setPageSize(PAGE_SIZE);
        setSaveState(false);
    }
}
