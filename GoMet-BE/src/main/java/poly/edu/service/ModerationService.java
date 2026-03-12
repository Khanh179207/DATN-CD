package poly.edu.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import poly.edu.dao.*;
import poly.edu.dto.ModerationActionDTO;
import poly.edu.dto.ModerationQueuePostDTO;
import poly.edu.dto.MyPostDTO;
import poly.edu.entity.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Handles the full post moderation workflow:
 * approve / reject / hide / unhide / flag-spam, audit logging, and
 * in-app notifications to post owners.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ModerationService {

    private final PostDAO             postDAO;
    private final AccountDAO          accountDAO;
    private final ModerationActionDAO moderationActionDAO;
    private final NotificationDAO     notificationDAO;
    private final AuditLogService     auditLogService;
    private final ObjectMapper        objectMapper = new ObjectMapper();

    // ─── Approve ─────────────────────────────────────────────────────────────

    @Transactional
    public void approvePost(Integer postId, Integer adminId, String reason,
                            HttpServletRequest request) {
        Post post = requirePost(postId);
        Account admin = requireAdmin(adminId);

        post.setStatus(PostStatus.APPROVED);
        post.setIsApproved(1);
        post.setIsActive(1);
        post.setModeratedBy(admin);
        post.setModeratedAt(Instant.now());
        post.setUpdatedAt(Instant.now());
        postDAO.save(post);

        recordAction(post, admin, ModerationActionType.APPROVE, reason, null, request);
        sendNotification(post, "Post Approved",
                "Your post \"" + post.getTitle() + "\" has been approved and is now public.",
                "MODERATION_APPROVED");
        auditLogService.log(adminId, AuditLogService.POST_APPROVED, request,
                Map.of("postId", postId, "postTitle", post.getTitle()));
    }

    // ─── Reject ──────────────────────────────────────────────────────────────

    @Transactional
    public void rejectPost(Integer postId, Integer adminId, String code, String reason,
                           HttpServletRequest request) {
        Post post = requirePost(postId);
        Account admin = requireAdmin(adminId);

        post.setStatus(PostStatus.REJECTED);
        post.setIsApproved(0);
        post.setRejectionCode(code);
        post.setRejectionReason(reason);
        post.setModeratedBy(admin);
        post.setModeratedAt(Instant.now());
        post.setUpdatedAt(Instant.now());
        postDAO.save(post);

        String metaJson = toJson(Map.of("code", code != null ? code : "", "reason", reason != null ? reason : ""));
        recordAction(post, admin, ModerationActionType.REJECT, reason, metaJson, request);
        sendNotification(post, "Post Rejected",
                "Your post \"" + post.getTitle() + "\" was not approved. Reason: " +
                (reason != null ? reason : "See rejection details."),
                "MODERATION_REJECTED");
        auditLogService.log(adminId, AuditLogService.POST_REJECTED, request,
                Map.of("postId", postId, "code", code != null ? code : "", "reason", reason != null ? reason : ""));
    }

    // ─── Hide ────────────────────────────────────────────────────────────────

    @Transactional
    public void hidePost(Integer postId, Integer adminId, String reason,
                         HttpServletRequest request) {
        Post post = requirePost(postId);
        Account admin = requireAdmin(adminId);

        post.setStatus(PostStatus.HIDDEN);
        post.setIsActive(0);
        post.setHiddenAt(Instant.now());
        post.setModeratedBy(admin);
        post.setModeratedAt(Instant.now());
        post.setUpdatedAt(Instant.now());
        postDAO.save(post);

        recordAction(post, admin, ModerationActionType.HIDE, reason, null, request);
        sendNotification(post, "Post Hidden",
                "Your post \"" + post.getTitle() + "\" has been temporarily hidden.",
                "MODERATION_HIDDEN");
        auditLogService.log(adminId, AuditLogService.POST_HIDDEN, request,
                Map.of("postId", postId, "reason", reason != null ? reason : ""));
    }

    // ─── Unhide ──────────────────────────────────────────────────────────────

    @Transactional
    public void unhidePost(Integer postId, Integer adminId, String reason,
                           HttpServletRequest request) {
        Post post = requirePost(postId);
        Account admin = requireAdmin(adminId);

        post.setStatus(PostStatus.APPROVED);
        post.setIsActive(1);
        post.setIsApproved(1);
        post.setHiddenAt(null);
        post.setModeratedBy(admin);
        post.setModeratedAt(Instant.now());
        post.setUpdatedAt(Instant.now());
        postDAO.save(post);

        recordAction(post, admin, ModerationActionType.UNHIDE, reason, null, request);
        sendNotification(post, "Post Restored",
                "Your post \"" + post.getTitle() + "\" is visible again.",
                "MODERATION_UNHIDDEN");
        auditLogService.log(adminId, AuditLogService.POST_UNHIDDEN, request,
                Map.of("postId", postId));
    }

    // ─── Flag Spam ───────────────────────────────────────────────────────────

    @Transactional
    public void flagSpam(Integer postId, Integer adminId, String reason,
                         HttpServletRequest request) {
        Post post = requirePost(postId);
        Account admin = requireAdmin(adminId);

        post.setStatus(PostStatus.HIDDEN);
        post.setIsActive(0);
        post.setIsApproved(0);
        post.setSpamScore(100);
        post.setHiddenAt(Instant.now());
        post.setModeratedBy(admin);
        post.setModeratedAt(Instant.now());
        post.setRejectionCode("SPAM");
        post.setRejectionReason(reason != null ? reason : "Flagged as spam by admin");
        post.setUpdatedAt(Instant.now());
        postDAO.save(post);

        String metaJson = toJson(Map.of("reason", reason != null ? reason : ""));
        recordAction(post, admin, ModerationActionType.FLAG_SPAM, reason, metaJson, request);
        sendNotification(post, "Post Flagged as Spam",
                "Your post \"" + post.getTitle() + "\" has been flagged and hidden.",
                "MODERATION_SPAM");
        auditLogService.log(adminId, AuditLogService.POST_FLAGGED_SPAM, request,
                Map.of("postId", postId));
    }

    // ─── Admin queue ──────────────────────────────────────────────────────────

    public Page<ModerationQueuePostDTO> getPendingQueue(String keyword, int page, int size) {
        Page<Post> posts = postDAO.findByStatusAndKeyword(
                PostStatus.PENDING_REVIEW,
                keyword,
                PageRequest.of(page, size, Sort.by("createdAt").descending()));
        return posts.map(this::toQueueDTO);
    }

    public Page<ModerationQueuePostDTO> getByStatus(PostStatus status, String keyword, int page, int size) {
        Page<Post> posts = postDAO.findByStatusAndKeyword(
                status, keyword,
                PageRequest.of(page, size, Sort.by("createdAt").descending()));
        return posts.map(this::toQueueDTO);
    }

    public List<ModerationActionDTO> getPostActions(Integer postId) {
        return moderationActionDAO.findTimelineForPost(postId).stream()
                .map(this::toActionDTO)
                .collect(Collectors.toList());
    }

    // ─── User: my posts ───────────────────────────────────────────────────────

    public List<MyPostDTO> getMyPosts(Integer accountId, PostStatus status) {
        List<Post> posts = (status == null)
                ? postDAO.findByAccount_AccountIDOrderByCreatedAtDesc(accountId)
                : postDAO.findByAccount_AccountIDAndStatusOrderByCreatedAtDesc(accountId, status);
        return posts.stream().map(this::toMyPostDTO).collect(Collectors.toList());
    }

    // ─── Helpers ─────────────────────────────────────────────────────────────

    private Post requirePost(Integer postId) {
        return postDAO.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found: " + postId));
    }

    private Account requireAdmin(Integer adminId) {
        return accountDAO.findById(adminId)
                .orElseThrow(() -> new IllegalArgumentException("Admin not found: " + adminId));
    }

    private void recordAction(Post post, Account admin, ModerationActionType type,
                              String reason, String metaJson, HttpServletRequest request) {
        ModerationAction action = ModerationAction.builder()
                .post(post)
                .admin(admin)
                .action(type)
                .reason(reason)
                .metaJson(metaJson)
                .ip(extractIp(request))
                .userAgent(request != null ? request.getHeader("User-Agent") : null)
                .createdAt(Instant.now())
                .build();
        moderationActionDAO.save(action);
    }

    private void sendNotification(Post post, String title, String body, String type) {
        try {
            Account owner = post.getAccount();
            if (owner == null) return;
            Notification n = new Notification();
            n.setAccount(owner);
            n.setPost(post);
            n.setTitle(title);
            n.setContent(body);
            n.setType(type);
            n.setIsRead(0);
            n.setCreatedAt(java.time.LocalDate.now());
            notificationDAO.save(n);
        } catch (Exception e) {
            log.warn("Failed to create moderation notification for post {}: {}", post.getPostID(), e.getMessage());
        }
    }

    private String extractIp(HttpServletRequest request) {
        if (request == null) return null;
        String xff = request.getHeader("X-Forwarded-For");
        return (xff != null && !xff.isBlank()) ? xff.split(",")[0].trim() : request.getRemoteAddr();
    }

    private String toJson(Object obj) {
        try { return objectMapper.writeValueAsString(obj); }
        catch (JsonProcessingException e) { return null; }
    }

    // ─── Mapping ─────────────────────────────────────────────────────────────

    public ModerationQueuePostDTO toQueueDTO(Post p) {
        ModerationQueuePostDTO dto = new ModerationQueuePostDTO();
        dto.setPostID(p.getPostID());
        dto.setTitle(p.getTitle());
        dto.setDescription(p.getDescription());
        dto.setMedia(p.getMedia());
        dto.setLevel(p.getLevel());
        dto.setCookingTime(p.getCookingTime());
        dto.setStatus(p.getStatus());
        dto.setSpamScore(p.getSpamScore());
        dto.setSpamReasons(p.getSpamReasons());
        dto.setCreatedAt(p.getCreatedAt());
        dto.setModeratedAt(p.getModeratedAt());
        dto.setRejectionCode(p.getRejectionCode());
        dto.setRejectionReason(p.getRejectionReason());
        if (p.getAccount() != null) {
            dto.setAuthorID(p.getAccount().getAccountID());
            dto.setAuthorName(p.getAccount().getUsername());
            dto.setAuthorAvatar(p.getAccount().getAvatar());
            dto.setAuthorApprovedPostCount(postDAO.countApprovedByAccount(p.getAccount().getAccountID()));
        }
        if (p.getModeratedBy() != null) {
            dto.setModeratedByName(p.getModeratedBy().getUsername());
        }
        if (p.getCategory() != null) {
            dto.setCategoryID(p.getCategory().getCategoryID());
            dto.setCategoryName(p.getCategory().getCategoryName());
        }
        return dto;
    }

    public ModerationActionDTO toActionDTO(ModerationAction a) {
        ModerationActionDTO dto = new ModerationActionDTO();
        dto.setId(a.getId());
        dto.setAction(a.getAction());
        dto.setReason(a.getReason());
        dto.setMetaJson(a.getMetaJson());
        dto.setIp(a.getIp());
        dto.setCreatedAt(a.getCreatedAt());
        if (a.getPost() != null) {
            dto.setPostID(a.getPost().getPostID());
            dto.setPostTitle(a.getPost().getTitle());
        }
        if (a.getAdmin() != null) {
            dto.setAdminID(a.getAdmin().getAccountID());
            dto.setAdminName(a.getAdmin().getUsername());
            dto.setAdminAvatar(a.getAdmin().getAvatar());
        }
        return dto;
    }

    public MyPostDTO toMyPostDTO(Post p) {
        MyPostDTO dto = new MyPostDTO();
        dto.setPostID(p.getPostID());
        dto.setTitle(p.getTitle());
        dto.setDescription(p.getDescription());
        dto.setMedia(p.getMedia());
        dto.setLevel(p.getLevel());
        dto.setCookingTime(p.getCookingTime());
        dto.setViews(p.getViews());
        dto.setCreatedAt(p.getCreatedAt());
        dto.setStatus(p.getStatus());
        dto.setRejectionReason(p.getRejectionReason());
        dto.setRejectionCode(p.getRejectionCode());
        dto.setSpamScore(p.getSpamScore());
        if (p.getCategory() != null) {
            dto.setCategoryID(p.getCategory().getCategoryID());
            dto.setCategoryName(p.getCategory().getCategoryName());
        }
        if (p.getRatings() != null && !p.getRatings().isEmpty()) {
            dto.setAvgRating(p.getRatings().stream().mapToInt(r -> r.getRate()).average().orElse(0));
            dto.setRatingCount((long) p.getRatings().size());
        } else {
            dto.setAvgRating(0.0);
            dto.setRatingCount(0L);
        }
        dto.setCommentCount(p.getComments() != null ? (long) p.getComments().size() : 0L);
        dto.setFavoriteCount(p.getFavorites() != null ? (long) p.getFavorites().size() : 0L);
        return dto;
    }
}
