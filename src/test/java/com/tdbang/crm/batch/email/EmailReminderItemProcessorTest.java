/*
 * Copyright © 2026 by tdbang.
 * All rights reserved.
 */

package com.tdbang.crm.batch.email;

import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.tdbang.crm.config.CrmBatchProperties;
import com.tdbang.crm.dtos.EmailReminderContext;
import com.tdbang.crm.entities.Contact;
import com.tdbang.crm.entities.EmailJob;
import com.tdbang.crm.entities.SalesOrder;
import com.tdbang.crm.entities.User;
import com.tdbang.crm.enums.EmailEntityType;
import com.tdbang.crm.enums.EmailJobStatus;
import com.tdbang.crm.enums.SalesOrderStatus;
import com.tdbang.crm.repositories.EmailJobRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmailReminderItemProcessorTest {

    @Mock
    private EmailJobRepository emailJobRepository;

    @Mock
    private CrmBatchProperties batchProperties;

    @Mock
    private TemplateEngine templateEngine;

    private EmailReminderItemProcessor processor;

    private CrmBatchProperties.EmailJobProperties emailProps;

    @BeforeEach
    void setUp() {
        emailProps = mock(CrmBatchProperties.EmailJobProperties.class);
        // lenient: the unsupported-type test returns early before batchProperties is used
        lenient().when(batchProperties.getEmail()).thenReturn(emailProps);
        processor = new EmailReminderItemProcessor(emailJobRepository, batchProperties, templateEngine);
    }

    // -------------------------------------------------------------------------
    // Contact tests
    // -------------------------------------------------------------------------

    @Test
    void process_contact_alreadySentRecently_returnsNull() {
        when(emailProps.getInactivityDaysContact()).thenReturn(7);
        when(emailJobRepository.existsByEntityTypeAndEntityPkAndStatusAndCreatedOnAfter(
            eq(EmailEntityType.CONTACT), anyLong(), eq(EmailJobStatus.SENT), any(Date.class)))
            .thenReturn(true);

        Contact contact = buildContact(1L, "Alice", "ACME", "mgr@example.com", "Manager");

        EmailReminderContext result = processor.process(contact);

        assertNull(result);
        verify(emailJobRepository, never()).save(any());
    }

    @Test
    void process_contact_notRecentlySent_returnsContext() {
        when(emailProps.getInactivityDaysContact()).thenReturn(7);
        when(emailJobRepository.existsByEntityTypeAndEntityPkAndStatusAndCreatedOnAfter(
            any(), anyLong(), any(), any(Date.class))).thenReturn(false);
        when(templateEngine.process(eq("email/contact-reminder"), any(Context.class)))
            .thenReturn("<html>contact body</html>");

        EmailJob savedJob = new EmailJob();
        savedJob.setPk(10L);
        when(emailJobRepository.save(any(EmailJob.class))).thenReturn(savedJob);

        Contact contact = buildContact(1L, "Alice", "ACME", "mgr@example.com", "Manager");

        EmailReminderContext result = processor.process(contact);

        assertNotNull(result);
        assertEquals(EmailEntityType.CONTACT, result.getEntityType());
        assertEquals(1L, result.getEntityPk());
        assertEquals("mgr@example.com", result.getRecipientEmail());
        assertEquals("Manager", result.getRecipientName());
        assertEquals("Follow-up reminder: Alice", result.getSubject());
        assertEquals("<html>contact body</html>", result.getBody());
        assertEquals(10L, result.getEmailJobPk());
    }

    // -------------------------------------------------------------------------
    // SalesOrder tests
    // -------------------------------------------------------------------------

    @Test
    void process_salesOrder_notRecentlySent_returnsContext() {
        when(emailProps.getInactivityDaysOrder()).thenReturn(3);
        when(emailJobRepository.existsByEntityTypeAndEntityPkAndStatusAndCreatedOnAfter(
            any(), anyLong(), any(), any(Date.class))).thenReturn(false);
        when(templateEngine.process(eq("email/sales-order-reminder"), any(Context.class)))
            .thenReturn("<html>order body</html>");

        EmailJob savedJob = new EmailJob();
        savedJob.setPk(20L);
        when(emailJobRepository.save(any(EmailJob.class))).thenReturn(savedJob);

        SalesOrder order = buildSalesOrder(5L, "Deal #001", "rep@example.com", "Sales Rep");

        EmailReminderContext result = processor.process(order);

        assertNotNull(result);
        assertEquals(EmailEntityType.SALES_ORDER, result.getEntityType());
        assertEquals(5L, result.getEntityPk());
        assertEquals("rep@example.com", result.getRecipientEmail());
        assertEquals("Sales Rep", result.getRecipientName());
        assertEquals("Follow-up reminder: Deal #001", result.getSubject());
        assertEquals("<html>order body</html>", result.getBody());
        assertEquals(20L, result.getEmailJobPk());
    }

    // -------------------------------------------------------------------------
    // Unsupported type
    // -------------------------------------------------------------------------

    @Test
    void process_unsupportedType_returnsNull() {
        EmailReminderContext result = processor.process("some random string");

        assertNull(result);
        verify(emailJobRepository, never()).save(any());
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private Contact buildContact(Long pk, String name, String org, String email, String assignedName) {
        User assignedTo = new User();
        assignedTo.setEmail(email);
        assignedTo.setName(assignedName);

        Contact contact = new Contact();
        contact.setPk(pk);
        contact.setContactName(name);
        contact.setOrganization(org);
        contact.setAssignedTo(assignedTo);
        return contact;
    }

    private SalesOrder buildSalesOrder(Long pk, String subject, String email, String assignedName) {
        User assignedTo = new User();
        assignedTo.setEmail(email);
        assignedTo.setName(assignedName);

        SalesOrder order = new SalesOrder();
        order.setPk(pk);
        order.setSubject(subject);
        order.setStatus(SalesOrderStatus.CREATED);
        order.setAssignedTo(assignedTo);
        return order;
    }
}
