/*
 * Copyright © 2026 by tdbang.
 * All rights reserved.
 */

package com.tdbang.crm.batch.email;

import java.util.Calendar;
import java.util.Map;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.batch.item.database.JpaPagingItemReader;

import com.tdbang.crm.entities.Contact;

/**
 * Reads {@link Contact} records that have not been updated within the
 * configured inactivity window and have an assigned user with a valid email.
 */
public class ContactReminderItemReader extends JpaPagingItemReader<Contact> {

    private static final String QUERY =
        "SELECT c FROM Contact c"
            + " WHERE c.updatedOn < :cutoffDate"
            + " AND c.assignedTo IS NOT NULL"
            + " AND c.assignedTo.email IS NOT NULL"
            + " ORDER BY c.pk ASC";

    private static final int PAGE_SIZE = 10;

    public ContactReminderItemReader(EntityManagerFactory emf, int inactivityDays) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, -inactivityDays);

        setName("contactReminderItemReader");
        setEntityManagerFactory(emf);
        setQueryString(QUERY);
        setParameterValues(Map.of("cutoffDate", cal.getTime()));
        setPageSize(PAGE_SIZE);
        setSaveState(false);
    }
}
