package poly.edu.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import poly.edu.dao.AccountDAO;
import poly.edu.dao.EmailJobDAO;
import poly.edu.dao.EmailJobRecipientDAO;
import poly.edu.dto.EmailJobDTO;
import poly.edu.dto.EmailJobRequestDTO;
import poly.edu.entity.Account;
import poly.edu.entity.EmailJob;
import poly.edu.entity.EmailJobRecipient;

import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Manages the email job lifecycle: create draft → queue → (worker sends).
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmailJobService {

    private final EmailJobDAO          jobDAO;
    private final EmailJobRecipientDAO recipientDAO;
    private final AccountDAO           accountDAO;
    private final AdminRateLimitService rateLimitService;
    private final AdminAuditLogService  auditLog;

    // ── Create draft ─────────────────────────────────────────────────────────

    @Transactional
    public EmailJobDTO createDraft(EmailJobRequestDTO req, Integer adminId) {
        validateRequest(req);

        // Deduplicate and collect recipient emails
        Set<String> emails = new LinkedHashSet<>();
        List<Integer> userIds = new ArrayList<>();
        List<String> userEmails = new ArrayList<>();

        if (req.getRecipientUserIds() != null) {
            for (Integer uid : req.getRecipientUserIds()) {
                accountDAO.findById(uid).ifPresent(a -> {
                    emails.add(a.getEmail().toLowerCase());
                    userIds.add(a.getAccountID());
                    userEmails.add(a.getEmail());
                });
            }
        }
        if (req.getRecipientEmails() != null) {
            req.getRecipientEmails().forEach(e -> emails.add(e.toLowerCase()));
        }

        if (emails.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No valid recipients provided");
        }

        EmailJob job = EmailJob.builder()
            .senderAdminId(adminId)
            .subject(req.getSubject())
            .bodyHtml(req.getBodyHtml())
            .bodyText(req.getBodyText())
            .status(EmailJob.STATUS_DRAFT)
            .recipientCount(emails.size())
            .build();

        job = jobDAO.save(job);

        // Build recipients — try to link user_id when known
        List<EmailJobRecipient> recipients = new ArrayList<>();
        for (String email : emails) {
            // Attempt to find user_id for this email
            Integer userId = accountDAO.findByEmail(email)
                .map(Account::getAccountID)
                .orElse(null);
            recipients.add(EmailJobRecipient.builder()
                .job(job)
                .userId(userId)
                .email(email)
                .status(EmailJobRecipient.STATUS_QUEUED)
                .build());
        }
        recipientDAO.saveAll(recipients);

        auditLog.log(adminId, poly.edu.entity.AdminAuditLog.EMAIL_JOB_CREATED,
            poly.edu.entity.AdminAuditLog.TARGET_EMAIL_JOB, String.valueOf(job.getId()),
            java.util.Map.of("subject", req.getSubject(), "recipientCount", emails.size()));

        EmailJobDTO dto = EmailJobDTO.fromEntity(job);
        dto.setSenderAdminId(adminId);
        return dto;
    }

    // ── Queue ────────────────────────────────────────────────────────────────

    @Transactional
    public EmailJobDTO queueJob(Long jobId, Integer adminId) {
        // Rate-limit check per queue action
        rateLimitService.checkAndRecord(adminId);

        EmailJob job = jobDAO.findById(jobId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Email job not found: " + jobId));

        if (!job.getSenderAdminId().equals(adminId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not your job");
        }
        if (!EmailJob.STATUS_DRAFT.equals(job.getStatus())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                "Job cannot be queued from status: " + job.getStatus());
        }

        job.setStatus(EmailJob.STATUS_QUEUED);
        job.setQueuedAt(Instant.now());
        job.setUpdatedAt(Instant.now());
        jobDAO.save(job);

        auditLog.log(adminId, poly.edu.entity.AdminAuditLog.EMAIL_JOB_QUEUED,
            poly.edu.entity.AdminAuditLog.TARGET_EMAIL_JOB, String.valueOf(jobId),
            java.util.Map.of("subject", job.getSubject(), "recipientCount", job.getRecipientCount()));

        return EmailJobDTO.fromEntity(job);
    }

    // ── Cancel ───────────────────────────────────────────────────────────────

    @Transactional
    public EmailJobDTO cancelJob(Long jobId, Integer adminId) {
        EmailJob job = jobDAO.findById(jobId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Job not found"));
        if (!job.getSenderAdminId().equals(adminId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not your job");
        }
        if (!Set.of(EmailJob.STATUS_DRAFT, EmailJob.STATUS_QUEUED).contains(job.getStatus())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                "Cannot cancel job in status: " + job.getStatus());
        }
        job.setStatus(EmailJob.STATUS_CANCELED);
        job.setUpdatedAt(Instant.now());
        jobDAO.save(job);

        auditLog.log(adminId, poly.edu.entity.AdminAuditLog.EMAIL_JOB_CANCELED,
            poly.edu.entity.AdminAuditLog.TARGET_EMAIL_JOB, String.valueOf(jobId), null);

        return EmailJobDTO.fromEntity(job);
    }

    // ── Read ─────────────────────────────────────────────────────────────────

    public Page<EmailJobDTO> listJobs(Integer adminId, String status, Pageable pageable) {
        if (status != null && !status.isBlank()) {
            return jobDAO.findByStatus(status).stream()
                .filter(j -> adminId == null || j.getSenderAdminId().equals(adminId))
                .map(this::enrichDto)
                .collect(java.util.stream.Collectors.collectingAndThen(
                    java.util.stream.Collectors.toList(),
                    list -> new org.springframework.data.domain.PageImpl<>(list, pageable, list.size())
                ));
        }
        Page<EmailJob> page = adminId != null
            ? jobDAO.findBySenderAdminIdOrderByCreatedAtDesc(adminId, pageable)
            : jobDAO.findAll(pageable);
        return page.map(this::enrichDto);
    }

    public EmailJobDTO getJobDetail(Long jobId) {
        EmailJob job = jobDAO.findById(jobId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Job not found"));
        EmailJobDTO dto = enrichDto(job);
        // Load recipients
        List<EmailJobRecipient> recs = recipientDAO.findByJobId(jobId);
        dto.setRecipients(recs.stream().map(r -> {
            EmailJobDTO.RecipientDTO rdto = EmailJobDTO.fromRecipient(r);
            if (r.getUserId() != null) {
                accountDAO.findById(r.getUserId()).ifPresent(a -> rdto.setUsername(a.getUsername()));
            }
            return rdto;
        }).toList());
        return dto;
    }

    // ── Internal helpers ─────────────────────────────────────────────────────

    private EmailJobDTO enrichDto(EmailJob job) {
        EmailJobDTO dto = EmailJobDTO.fromEntity(job);
        accountDAO.findById(job.getSenderAdminId())
            .ifPresent(a -> dto.setSenderAdminName(a.getUsername()));
        return dto;
    }

    private void validateRequest(EmailJobRequestDTO req) {
        if (req.getSubject() == null || req.getSubject().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Subject is required");
        }
        if (req.getSubject().length() > 500) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Subject too long (max 500 chars)");
        }
        boolean hasHtml = req.getBodyHtml() != null && !req.getBodyHtml().isBlank();
        boolean hasText = req.getBodyText() != null && !req.getBodyText().isBlank();
        if (!hasHtml && !hasText) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email body (html or text) is required");
        }
    }
}
