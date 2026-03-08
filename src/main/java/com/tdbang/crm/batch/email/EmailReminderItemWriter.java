/*
 * Copyright © 2026 by tdbang.
 * All rights reserved.
 */

package com.tdbang.crm.batch.email;

import java.util.Date;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import com.tdbang.crm.dtos.EmailReminderContext;
import com.tdbang.crm.entities.EmailJob;
import com.tdbang.crm.enums.EmailJobStatus;
import com.tdbang.crm.repositories.EmailJobRepository;

/**
 * Sends each {@link EmailReminderContext} as an HTML reminder email and updates
 * the corresponding {@link EmailJob} record to {@code SENT} or {@code FAILED}.
 *
 * <p>{@link JavaMailSender} is injected directly (rather than delegating to
 * {@code EmailService.sendHtmlEmail}) because that method is {@code @Async} and
 * would prevent synchronous status tracking within the batch step.
 */
@Log4j2
@Component
@RequiredArgsConstructor
public class EmailReminderItemWriter implements ItemWriter<EmailReminderContext> {

    private final JavaMailSender mailSender;
    private final EmailJobRepository emailJobRepository;

    @Override
    public void write(Chunk<? extends EmailReminderContext> chunk) {
        for (EmailReminderContext ctx : chunk) {
            try {
                sendHtml(ctx.getRecipientEmail(), ctx.getSubject(), ctx.getBody());
                markSent(ctx.getEmailJobPk());
                log.info("Email sent to {} for {} pk={}",
                    ctx.getRecipientEmail(), ctx.getEntityType(), ctx.getEntityPk());
            } catch (Exception e) {
                markFailed(ctx.getEmailJobPk(), e.getMessage());
                log.error("Failed to send email to {} for {} pk={}: {}",
                    ctx.getRecipientEmail(), ctx.getEntityType(), ctx.getEntityPk(),
                    e.getMessage(), e);
            }
        }
    }

    private void sendHtml(String to, String subject, String html) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, "utf-8");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(html, true);
        mailSender.send(message);
    }

    private void markSent(Long emailJobPk) {
        emailJobRepository.findById(emailJobPk).ifPresent(job -> {
            job.setStatus(EmailJobStatus.SENT);
            job.setSentAt(new Date());
            emailJobRepository.save(job);
        });
    }

    private void markFailed(Long emailJobPk, String errorMessage) {
        emailJobRepository.findById(emailJobPk).ifPresent(job -> {
            job.setStatus(EmailJobStatus.FAILED);
            String truncated = errorMessage != null && errorMessage.length() > 500
                ? errorMessage.substring(0, 500) : errorMessage;
            job.setErrorMessage(truncated);
            emailJobRepository.save(job);
        });
    }
}
