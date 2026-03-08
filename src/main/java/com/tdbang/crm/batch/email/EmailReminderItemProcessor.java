/*
 * Copyright © 2026 by tdbang.
 * All rights reserved.
 */

package com.tdbang.crm.batch.email;

import java.util.Calendar;
import java.util.Date;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.tdbang.crm.config.CrmBatchProperties;
import com.tdbang.crm.dtos.EmailReminderContext;
import com.tdbang.crm.entities.Contact;
import com.tdbang.crm.entities.EmailJob;
import com.tdbang.crm.entities.SalesOrder;
import com.tdbang.crm.enums.EmailEntityType;
import com.tdbang.crm.enums.EmailJobStatus;
import com.tdbang.crm.repositories.EmailJobRepository;

/**
 * Converts a {@link Contact} or {@link SalesOrder} item into an
 * {@link EmailReminderContext} ready to be sent by the writer.
 *
 * <p>Anti-spam guard: returns {@code null} (skip) if a 'SENT' reminder already
 * exists for the entity within its configured inactivity window.
 * Otherwise, persists a PENDING {@link EmailJob} record and populates the
 * context so the writer can update it after the send attempt.
 */
@Log4j2
@Component
@RequiredArgsConstructor
public class EmailReminderItemProcessor implements ItemProcessor<Object, EmailReminderContext> {

    private final EmailJobRepository emailJobRepository;
    private final CrmBatchProperties batchProperties;
    private final TemplateEngine templateEngine;

    @Override
    public EmailReminderContext process(Object item) {
        EmailReminderContext ctx = buildContext(item);
        if (ctx == null) {
            return null;
        }
        if (isAlreadySentRecently(ctx)) {
            return null;
        }
        ctx.setEmailJobPk(savePendingJob(ctx));
        return ctx;
    }

    private EmailReminderContext buildContext(Object item) {
        if (item instanceof Contact c) {
            return buildContactContext(c);
        } else if (item instanceof SalesOrder s) {
            return buildSalesOrderContext(s);
        }
        log.warn("Unsupported item type for email reminder: {}", item.getClass().getName());
        return null;
    }

    private EmailReminderContext buildContactContext(Contact contact) {
        EmailReminderContext ctx = new EmailReminderContext();
        ctx.setEntityType(EmailEntityType.CONTACT);
        ctx.setEntityPk(contact.getPk());
        ctx.setRecipientEmail(contact.getAssignedTo().getEmail());
        ctx.setRecipientName(contact.getAssignedTo().getName());
        ctx.setSubject("Follow-up reminder: " + contact.getContactName());
        ctx.setBody(renderContactTemplate(contact));
        return ctx;
    }

    private EmailReminderContext buildSalesOrderContext(SalesOrder order) {
        EmailReminderContext ctx = new EmailReminderContext();
        ctx.setEntityType(EmailEntityType.SALES_ORDER);
        ctx.setEntityPk(order.getPk());
        ctx.setRecipientEmail(order.getAssignedTo().getEmail());
        ctx.setRecipientName(order.getAssignedTo().getName());
        ctx.setSubject("Follow-up reminder: " + order.getSubject());
        ctx.setBody(renderSalesOrderTemplate(order));
        return ctx;
    }

    private boolean isAlreadySentRecently(EmailReminderContext ctx) {
        int days = ctx.getEntityType() == EmailEntityType.CONTACT
            ? batchProperties.getEmail().getInactivityDaysContact()
            : batchProperties.getEmail().getInactivityDaysOrder();
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, -days);
        boolean sent = emailJobRepository.existsByEntityTypeAndEntityPkAndStatusAndCreatedOnAfter(
            ctx.getEntityType(), ctx.getEntityPk(), EmailJobStatus.SENT, cal.getTime());
        if (sent) {
            log.info("Skipping {} pk={}: reminder already sent within {} days",
                ctx.getEntityType(), ctx.getEntityPk(), days);
        }
        return sent;
    }

    private Long savePendingJob(EmailReminderContext ctx) {
        EmailJob job = new EmailJob();
        job.setEntityType(ctx.getEntityType());
        job.setEntityPk(ctx.getEntityPk());
        job.setRecipientEmail(ctx.getRecipientEmail());
        job.setRecipientName(ctx.getRecipientName());
        job.setSubject(ctx.getSubject());
        job.setStatus(EmailJobStatus.PENDING);
        job.setScheduledAt(new Date());
        return emailJobRepository.save(job).getPk();
    }

    private String renderContactTemplate(Contact contact) {
        Context ctx = new Context();
        ctx.setVariable("contactName", contact.getContactName());
        ctx.setVariable("organization", contact.getOrganization());
        ctx.setVariable("assignedToName", contact.getAssignedTo().getName());
        return templateEngine.process("email/contact-reminder", ctx);
    }

    private String renderSalesOrderTemplate(SalesOrder order) {
        Context ctx = new Context();
        ctx.setVariable("subject", order.getSubject());
        ctx.setVariable("status", order.getStatus().getName());
        ctx.setVariable("total", order.getTotal());
        ctx.setVariable("assignedToName", order.getAssignedTo().getName());
        return templateEngine.process("email/sales-order-reminder", ctx);
    }
}
