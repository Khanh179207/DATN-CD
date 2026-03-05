package poly.edu.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import poly.edu.dao.EmailJobDAO;
import poly.edu.dao.EmailJobRecipientDAO;
import poly.edu.entity.AdminAuditLog;
import poly.edu.entity.EmailJob;
import poly.edu.entity.EmailJobRecipient;

import jakarta.mail.internet.MimeMessage;
import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * Background worker that processes QUEUED email jobs.
 *
 * Safety:
 * - Atomic status transition QUEUED → SENDING (UPDATE WHERE status = QUEUED)
 * - Idempotent: only processes QUEUED recipients
 * - Errors per recipient are stored as FAILED, job completes with partial send
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmailJobWorker {

    private final EmailJobDAO          jobDAO;
    private final EmailJobRecipientDAO recipientDAO;
    private final JavaMailSender       mailSender;
    private final AdminAuditLogService auditLog;

    /** The authenticated SMTP sender address (must match spring.mail.username for Gmail). */
    @Value("${spring.mail.username}")
    private String mailFrom;

    private static final int BATCH_SIZE = 50;

    /**
     * Poll every 10 seconds for QUEUED jobs.
     */
    @Scheduled(fixedDelay = 10_000)
    public void processPendingJobs() {
        List<EmailJob> queued = jobDAO.findByStatus(EmailJob.STATUS_QUEUED);
        if (queued.isEmpty()) return;

        for (EmailJob job : queued) {
            try {
                processJob(job);
            } catch (Exception e) {
                log.error("Unexpected failure processing email job {}: {}", job.getId(), e.getMessage(), e);
            }
        }
    }

    private void processJob(EmailJob job) {
        // Atomic claim: QUEUED → SENDING
        int claimed = jobDAO.claimJob(job.getId(), Instant.now());
        if (claimed == 0) {
            log.debug("Job {} already claimed by another worker, skipping", job.getId());
            return;
        }

        log.info("Processing email job {} — subject: '{}', recipients: {}",
            job.getId(), job.getSubject(), job.getRecipientCount());

        List<EmailJobRecipient> recipients = recipientDAO.findByJobIdAndStatus(
            job.getId(), EmailJobRecipient.STATUS_QUEUED);

        int sent   = 0;
        int failed = 0;

        for (EmailJobRecipient recipient : recipients) {
            try {
                sendToRecipient(job, recipient);
                recipientDAO.updateRecipientStatus(
                    recipient.getId(), EmailJobRecipient.STATUS_SENT, Instant.now(), null);
                sent++;
            } catch (Exception e) {
                log.warn("Failed to send email to {} for job {}: {}",
                    recipient.getEmail(), job.getId(), e.getMessage());
                recipientDAO.updateRecipientStatus(
                    recipient.getId(), EmailJobRecipient.STATUS_FAILED, null, truncate(e.getMessage(), 500));
                failed++;
            }
        }

        String finalStatus = (failed == 0)
            ? EmailJob.STATUS_SENT
            : (sent == 0 ? EmailJob.STATUS_FAILED : EmailJob.STATUS_SENT); // partial = SENT with failedCount

        String failReason = failed > 0 ? failed + " recipient(s) failed" : null;

        jobDAO.updateJobResult(job.getId(), finalStatus, sent, failed, failReason, Instant.now());

        log.info("Job {} complete — sent: {}, failed: {}, status: {}",
            job.getId(), sent, failed, finalStatus);

        auditLog.log(
            job.getSenderAdminId(),
            failed == 0 ? AdminAuditLog.EMAIL_JOB_SENT : AdminAuditLog.EMAIL_JOB_FAILED,
            AdminAuditLog.TARGET_EMAIL_JOB, String.valueOf(job.getId()),
            Map.of("sent", sent, "failed", failed, "status", finalStatus)
        );
    }

    private void sendToRecipient(EmailJob job, EmailJobRecipient recipient) throws Exception {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        // From address MUST match the authenticated SMTP account (e.g. Gmail rejects mismatched senders)
        helper.setFrom(mailFrom, "GoMet Admin");
        helper.setTo(recipient.getEmail());
        helper.setSubject("[GoMet] " + job.getSubject());

        boolean hasHtml = job.getBodyHtml() != null && !job.getBodyHtml().isBlank();
        boolean hasText = job.getBodyText() != null && !job.getBodyText().isBlank();

        if (hasHtml && hasText) {
            helper.setText(job.getBodyText(), job.getBodyHtml());
        } else if (hasHtml) {
            helper.setText(wrapHtml(job.getSubject(), job.getBodyHtml()), true);
        } else {
            helper.setText(job.getBodyText(), false);
        }

        mailSender.send(message);
    }

    /** Wrap plain HTML body in a minimal branded email shell. */
    private String wrapHtml(String subject, String body) {
                return String.format(
                                "<!DOCTYPE html>"
                                                + "<html lang=\"vi\">"
                                                + "<head><meta charset=\"UTF-8\"><title>%s</title></head>"
                                                + "<body style=\"margin:0;padding:0;background:#F9F5F0;font-family:Helvetica,Arial,sans-serif;\">"
                                                + "<table width=\"100%%\" cellpadding=\"0\" cellspacing=\"0\" style=\"background:#F9F5F0;padding:40px 0;\">"
                                                + "<tr><td align=\"center\">"
                                                + "<table width=\"600\" cellpadding=\"0\" cellspacing=\"0\" style=\"background:#fff;border-radius:16px;overflow:hidden;box-shadow:0 4px 24px rgba(0,0,0,.07);\">"
                                                + "<tr><td style=\"background:#1C1917;padding:24px 40px;text-align:center;\">"
                                                + "<h1 style=\"margin:0;color:#EA580C;font-size:24px;letter-spacing:4px;font-weight:900;\">GOMET.</h1>"
                                                + "</td></tr>"
                                                + "<tr><td style=\"padding:32px 40px;\">%s</td></tr>"
                                                + "<tr><td style=\"background:#F5F5F4;padding:16px 40px;text-align:center;font-size:12px;color:#A8A29E;\">"
                                                + "© 2026 GoMet Culinary Community"
                                                + "</td></tr>"
                                                + "</table>"
                                                + "</td></tr>"
                                                + "</table>"
                                                + "</body></html>",
                                subject, body
                );
    }

    private String truncate(String s, int max) {
        if (s == null) return null;
        return s.length() <= max ? s : s.substring(0, max);
    }
}
