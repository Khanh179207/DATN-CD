package poly.edu.service;

import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;
import poly.edu.dao.EmailJobDAO;
import poly.edu.dao.EmailJobRecipientDAO;
import poly.edu.entity.AdminAuditLog;
import poly.edu.entity.EmailJob;
import poly.edu.entity.EmailJobRecipient;

import java.time.Instant;
import java.util.List;
import java.util.Properties;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmailJobWorkerTest {

    @Mock
    private EmailJobDAO jobDAO;

    @Mock
    private EmailJobRecipientDAO recipientDAO;

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private AdminAuditLogService auditLog;

    @InjectMocks
    private EmailJobWorker worker;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(worker, "mailFrom", "admin@gomet.vn");
    }

    @Test
    @DisplayName("processPendingJobs marks job SENT when all recipients are delivered")
    void processPendingJobs_marksJobSent() {
        EmailJob job = EmailJob.builder()
                .id(10L)
                .senderAdminId(99)
                .subject("System maintenance")
                .bodyHtml("<p>Hello</p>")
                .recipientCount(1)
                .status(EmailJob.STATUS_QUEUED)
                .build();
        EmailJobRecipient recipient = EmailJobRecipient.builder()
                .id(20L)
                .job(job)
                .email("chef@gomet.vn")
                .status(EmailJobRecipient.STATUS_QUEUED)
                .build();

        when(jobDAO.findByStatus(EmailJob.STATUS_QUEUED)).thenReturn(List.of(job));
        when(jobDAO.claimJob(eq(10L), any(Instant.class))).thenReturn(1);
        when(recipientDAO.findByJobIdAndStatus(10L, EmailJobRecipient.STATUS_QUEUED)).thenReturn(List.of(recipient));
        when(mailSender.createMimeMessage()).thenReturn(new MimeMessage(Session.getInstance(new Properties())));

        worker.processPendingJobs();

        verify(recipientDAO).updateRecipientStatus(eq(20L), eq(EmailJobRecipient.STATUS_SENT), any(Instant.class), isNull());
        verify(jobDAO).updateJobResult(eq(10L), eq(EmailJob.STATUS_SENT), eq(1), eq(0), isNull(), any(Instant.class));
        verify(auditLog).log(eq(99), eq(AdminAuditLog.EMAIL_JOB_SENT), eq(AdminAuditLog.TARGET_EMAIL_JOB), eq("10"), anyMap());
    }

    @Test
    @DisplayName("processPendingJobs marks job FAILED when every recipient send fails")
    void processPendingJobs_marksJobFailed() {
        EmailJob job = EmailJob.builder()
                .id(11L)
                .senderAdminId(100)
                .subject("Newsletter")
                .bodyText("Plain text body")
                .recipientCount(1)
                .status(EmailJob.STATUS_QUEUED)
                .build();
        EmailJobRecipient recipient = EmailJobRecipient.builder()
                .id(21L)
                .job(job)
                .email("chef@gomet.vn")
                .status(EmailJobRecipient.STATUS_QUEUED)
                .build();

        when(jobDAO.findByStatus(EmailJob.STATUS_QUEUED)).thenReturn(List.of(job));
        when(jobDAO.claimJob(eq(11L), any(Instant.class))).thenReturn(1);
        when(recipientDAO.findByJobIdAndStatus(11L, EmailJobRecipient.STATUS_QUEUED)).thenReturn(List.of(recipient));
        when(mailSender.createMimeMessage()).thenReturn(new MimeMessage(Session.getInstance(new Properties())));
        doThrow(new RuntimeException("SMTP rejected recipient")).when(mailSender).send(any(MimeMessage.class));

        worker.processPendingJobs();

        verify(recipientDAO).updateRecipientStatus(eq(21L), eq(EmailJobRecipient.STATUS_FAILED), isNull(), anyString());
        verify(jobDAO).updateJobResult(eq(11L), eq(EmailJob.STATUS_FAILED), eq(0), eq(1), eq("1 recipient(s) failed"), any(Instant.class));
        verify(auditLog).log(eq(100), eq(AdminAuditLog.EMAIL_JOB_FAILED), eq(AdminAuditLog.TARGET_EMAIL_JOB), eq("11"), anyMap());
    }
}