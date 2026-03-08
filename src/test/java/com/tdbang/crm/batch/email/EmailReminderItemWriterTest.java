/*
 * Copyright © 2026 by tdbang.
 * All rights reserved.
 */

package com.tdbang.crm.batch.email;

import java.util.List;
import java.util.Optional;

import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.item.Chunk;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;

import com.tdbang.crm.dtos.EmailReminderContext;
import com.tdbang.crm.entities.EmailJob;
import com.tdbang.crm.enums.EmailEntityType;
import com.tdbang.crm.enums.EmailJobStatus;
import com.tdbang.crm.repositories.EmailJobRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmailReminderItemWriterTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private EmailJobRepository emailJobRepository;

    @Captor
    private ArgumentCaptor<EmailJob> jobCaptor;

    private EmailReminderItemWriter writer;

    @BeforeEach
    void setUp() {
        writer = new EmailReminderItemWriter(mailSender, emailJobRepository);
    }

    @Test
    void write_sendSucceeds_updatesJobToSent() throws Exception {
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        EmailJob pendingJob = new EmailJob();
        pendingJob.setPk(1L);
        pendingJob.setStatus(EmailJobStatus.PENDING);
        when(emailJobRepository.findById(1L)).thenReturn(Optional.of(pendingJob));
        when(emailJobRepository.save(any(EmailJob.class))).thenAnswer(inv -> inv.getArgument(0));

        Chunk<EmailReminderContext> chunk = new Chunk<>(List.of(buildContext(1L)));
        writer.write(chunk);

        verify(mailSender).send(mimeMessage);
        verify(emailJobRepository).save(jobCaptor.capture());

        EmailJob saved = jobCaptor.getValue();
        assertEquals(EmailJobStatus.SENT, saved.getStatus());
        assertNotNull(saved.getSentAt());
        assertNull(saved.getErrorMessage());
    }

    @Test
    void write_sendFails_updatesJobToFailed() throws Exception {
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doThrow(new MailSendException("SMTP connection refused"))
            .when(mailSender).send(any(MimeMessage.class));

        EmailJob pendingJob = new EmailJob();
        pendingJob.setPk(2L);
        pendingJob.setStatus(EmailJobStatus.PENDING);
        when(emailJobRepository.findById(2L)).thenReturn(Optional.of(pendingJob));
        when(emailJobRepository.save(any(EmailJob.class))).thenAnswer(inv -> inv.getArgument(0));

        Chunk<EmailReminderContext> chunk = new Chunk<>(List.of(buildContext(2L)));
        writer.write(chunk);

        verify(emailJobRepository).save(jobCaptor.capture());

        EmailJob saved = jobCaptor.getValue();
        assertEquals(EmailJobStatus.FAILED, saved.getStatus());
        assertNotNull(saved.getErrorMessage());
    }

    @Test
    void write_multipleItems_processesEachIndependently() throws Exception {
        MimeMessage msg1 = mock(MimeMessage.class);
        MimeMessage msg2 = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(msg1, msg2);

        EmailJob job1 = new EmailJob(); job1.setPk(1L);
        EmailJob job2 = new EmailJob(); job2.setPk(2L);
        when(emailJobRepository.findById(1L)).thenReturn(Optional.of(job1));
        when(emailJobRepository.findById(2L)).thenReturn(Optional.of(job2));
        when(emailJobRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Chunk<EmailReminderContext> chunk = new Chunk<>(
            List.of(buildContext(1L), buildContext(2L)));
        writer.write(chunk);

        verify(mailSender, times(2)).send(any(MimeMessage.class));
        verify(emailJobRepository, times(2)).save(any(EmailJob.class));
    }

    private EmailReminderContext buildContext(long jobPk) {
        EmailReminderContext ctx = new EmailReminderContext();
        ctx.setEmailJobPk(jobPk);
        ctx.setEntityType(EmailEntityType.CONTACT);
        ctx.setEntityPk(100L);
        ctx.setRecipientEmail("user@example.com");
        ctx.setRecipientName("User");
        ctx.setSubject("Reminder");
        ctx.setBody("<html>reminder</html>");
        return ctx;
    }
}
