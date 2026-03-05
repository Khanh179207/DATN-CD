package poly.edu.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import poly.edu.dto.EmailJobDTO;
import poly.edu.dto.EmailJobRequestDTO;
import poly.edu.service.EmailJobService;
import poly.edu.service.RbacService;

/**
 * Admin email job endpoints.
 * Requires EMAIL_SEND permission.
 */
@RestController
@RequestMapping("/api/admin/email")
@RequiredArgsConstructor
@CrossOrigin
public class AdminEmailController {

    private final EmailJobService jobService;
    private final RbacService     rbac;

    /**
     * POST /api/admin/email/jobs — create a draft job.
     */
    @PostMapping("/jobs")
    public ResponseEntity<EmailJobDTO> createDraft(
            @RequestBody EmailJobRequestDTO req,
            @AuthenticationPrincipal Integer adminId,
            HttpServletRequest request) {

        rbac.requirePermission("EMAIL_SEND");
        EmailJobDTO dto = jobService.createDraft(req, adminId);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    /**
     * POST /api/admin/email/jobs/{jobId}/queue — queue a draft for sending.
     */
    @PostMapping("/jobs/{jobId}/queue")
    public ResponseEntity<EmailJobDTO> queueJob(
            @PathVariable Long jobId,
            @AuthenticationPrincipal Integer adminId) {

        rbac.requirePermission("EMAIL_SEND");
        return ResponseEntity.ok(jobService.queueJob(jobId, adminId));
    }

    /**
     * POST /api/admin/email/jobs — shortcut: create + queue in one call.
     * Body: same EmailJobRequestDTO but backend auto-queues it.
     */
    @PostMapping("/jobs/send")
    public ResponseEntity<EmailJobDTO> createAndSend(
            @RequestBody EmailJobRequestDTO req,
            @AuthenticationPrincipal Integer adminId) {

        rbac.requirePermission("EMAIL_SEND");
        EmailJobDTO draft = jobService.createDraft(req, adminId);
        EmailJobDTO queued = jobService.queueJob(draft.getId(), adminId);
        return ResponseEntity.status(HttpStatus.CREATED).body(queued);
    }

    /**
     * POST /api/admin/email/jobs/{jobId}/cancel
     */
    @PostMapping("/jobs/{jobId}/cancel")
    public ResponseEntity<EmailJobDTO> cancelJob(
            @PathVariable Long jobId,
            @AuthenticationPrincipal Integer adminId) {

        rbac.requirePermission("EMAIL_SEND");
        return ResponseEntity.ok(jobService.cancelJob(jobId, adminId));
    }

    /**
     * GET /api/admin/email/jobs?status=&page=&size=
     */
    @GetMapping("/jobs")
    public ResponseEntity<Page<EmailJobDTO>> listJobs(
            @RequestParam(required = false) String  status,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal Integer adminId) {

        rbac.requirePermission("EMAIL_SEND");
        PageRequest pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(jobService.listJobs(adminId, status, pageable));
    }

    /**
     * GET /api/admin/email/jobs/all — SUPER_ADMIN: see all jobs across admins.
     */
    @GetMapping("/jobs/all")
    public ResponseEntity<Page<EmailJobDTO>> listAllJobs(
            @RequestParam(required = false) String  status,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {

        rbac.requirePermission("EMAIL_SEND", "AUDIT_READ");
        PageRequest pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(jobService.listJobs(null, status, pageable));
    }

    /**
     * GET /api/admin/email/jobs/{jobId}
     */
    @GetMapping("/jobs/{jobId}")
    public ResponseEntity<EmailJobDTO> getJobDetail(@PathVariable Long jobId) {
        rbac.requirePermission("EMAIL_SEND");
        return ResponseEntity.ok(jobService.getJobDetail(jobId));
    }
}
