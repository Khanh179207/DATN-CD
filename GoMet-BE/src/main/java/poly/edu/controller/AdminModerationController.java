package poly.edu.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import poly.edu.dto.ModerationActionDTO;
import poly.edu.dto.ModerationQueuePostDTO;
import poly.edu.dto.ModerationRequestDTO;
import poly.edu.entity.PostStatus;
import poly.edu.service.ModerationService;

import java.util.List;
import java.util.Map;

/**
 * Admin-only REST endpoints for the Post Moderation Workflow.
 * All routes under /api/admin/moderation are secured to ADMIN role via SecurityConfig.
 */
@RestController
@RequestMapping("/api/admin/moderation")
@RequiredArgsConstructor
public class AdminModerationController {

    private final ModerationService moderationService;

    // ─── Moderation inbox ─────────────────────────────────────────────────────

    /**
     * GET /api/admin/moderation/posts
     * Returns paginated posts by status (default PENDING_REVIEW).
     * Query params: status, keyword, page (0-based), size
     */
    @GetMapping("/posts")
    public ResponseEntity<Page<ModerationQueuePostDTO>> getPosts(
            @RequestParam(defaultValue = "PENDING_REVIEW") PostStatus status,
            @RequestParam(defaultValue = "")   String keyword,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(moderationService.getByStatus(status, keyword, page, size));
    }

    // ─── Moderation actions ───────────────────────────────────────────────────

    /** POST /api/admin/moderation/posts/{id}/approve */
    @PostMapping("/posts/{id}/approve")
    public ResponseEntity<?> approve(@PathVariable Integer id,
                                     @RequestBody(required = false) ModerationRequestDTO req,
                                     HttpServletRequest request) {
        Integer adminId = resolveAdminId(request);
        String reason = req != null ? req.getReason() : null;
        moderationService.approvePost(id, adminId, reason, request);
        return ResponseEntity.ok(Map.of("message", "Post approved"));
    }

    /** POST /api/admin/moderation/posts/{id}/reject */
    @PostMapping("/posts/{id}/reject")
    public ResponseEntity<?> reject(@PathVariable Integer id,
                                    @RequestBody ModerationRequestDTO req,
                                    HttpServletRequest request) {
        Integer adminId = resolveAdminId(request);
        moderationService.rejectPost(id, adminId, req.getCode(), req.getReason(), request);
        return ResponseEntity.ok(Map.of("message", "Post rejected"));
    }

    /** POST /api/admin/moderation/posts/{id}/hide */
    @PostMapping("/posts/{id}/hide")
    public ResponseEntity<?> hide(@PathVariable Integer id,
                                  @RequestBody(required = false) ModerationRequestDTO req,
                                  HttpServletRequest request) {
        Integer adminId = resolveAdminId(request);
        moderationService.hidePost(id, adminId, req != null ? req.getReason() : null, request);
        return ResponseEntity.ok(Map.of("message", "Post hidden"));
    }

    /** POST /api/admin/moderation/posts/{id}/unhide */
    @PostMapping("/posts/{id}/unhide")
    public ResponseEntity<?> unhide(@PathVariable Integer id,
                                    @RequestBody(required = false) ModerationRequestDTO req,
                                    HttpServletRequest request) {
        Integer adminId = resolveAdminId(request);
        moderationService.unhidePost(id, adminId, req != null ? req.getReason() : null, request);
        return ResponseEntity.ok(Map.of("message", "Post unhidden"));
    }

    /** POST /api/admin/moderation/posts/{id}/flag-spam */
    @PostMapping("/posts/{id}/flag-spam")
    public ResponseEntity<?> flagSpam(@PathVariable Integer id,
                                      @RequestBody(required = false) ModerationRequestDTO req,
                                      HttpServletRequest request) {
        Integer adminId = resolveAdminId(request);
        moderationService.flagSpam(id, adminId, req != null ? req.getReason() : null, request);
        return ResponseEntity.ok(Map.of("message", "Post flagged as spam and hidden"));
    }

    // ─── Action history ───────────────────────────────────────────────────────

    /** GET /api/admin/moderation/actions?postId=... */
    @GetMapping("/actions")
    public ResponseEntity<List<ModerationActionDTO>> getActions(
            @RequestParam Integer postId) {
        return ResponseEntity.ok(moderationService.getPostActions(postId));
    }

    // ─── Helper ───────────────────────────────────────────────────────────────

    /**
     * Resolves the admin's accountID from the JWT principal in the Security context.
     * AdminID is carried as the Integer principal set by JwtAuthFilter.
     */
    private Integer resolveAdminId(HttpServletRequest request) {
        org.springframework.security.core.Authentication auth =
                org.springframework.security.core.context.SecurityContextHolder
                        .getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof Integer) return (Integer) auth.getPrincipal();
        // Fallback: legacy header (used during JWT migration)
        String header = request.getHeader("X-Admin-Id");
        if (header != null) {
            try { return Integer.parseInt(header); } catch (NumberFormatException ignored) {}
        }
        throw new IllegalStateException("Cannot resolve admin identity from security context");
    }
}
