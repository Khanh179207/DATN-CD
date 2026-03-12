package poly.edu.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import poly.edu.dao.PostDAO;
import poly.edu.dao.RateLimitEventDAO;
import poly.edu.entity.Account;
import poly.edu.entity.RateLimitEvent;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Facade for post anti-spam checks and rate-limiting.
 *
 * <p>Combines:
 * <ul>
 *   <li>DB-backed rate limiting ({@link RateLimitEventDAO})</li>
 *   <li>Content heuristics: link count, repeated chars, suspicious keywords, duplicates</li>
 *   <li>Trusted-user relaxation for authors with many approved posts</li>
 * </ul>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PostAntiSpamService {

    private final RateLimitEventDAO rateLimitEventDAO;
    private final PostDAO           postDAO;
    private final AuditLogService   auditLogService;
    private final ObjectMapper      objectMapper = new ObjectMapper();

    // ─── Configurable thresholds ─────────────────────────────────────────────

    /** Max post creations per user within the window. */
    @Value("${spam.post.max-per-user:3}")
    private int maxPostsPerUser;

    /** Max post creations per IP within the window. */
    @Value("${spam.post.max-per-ip:5}")
    private int maxPostsPerIp;

    /** Window in minutes for rate limiting. */
    @Value("${spam.post.window-minutes:10}")
    private int windowMinutes;

    /** Minimum approved posts for a user to be considered "trusted". */
    @Value("${spam.post.trusted-approved-threshold:10}")
    private int trustedThreshold;

    // ─── Spam heuristics constants ────────────────────────────────────────────

    private static final int MAX_URLS          = 2;
    private static final int MAX_REPEAT_CHARS  = 5;   // 5+ same consecutive chars = suspicious
    private static final Pattern URL_PATTERN =
            Pattern.compile("https?://\\S+|www\\.\\S+", Pattern.CASE_INSENSITIVE);

    private static final Pattern REPEAT_CHARS_PATTERN =
            Pattern.compile("(.)\\1{" + MAX_REPEAT_CHARS + ",}");

    /** Suspicious keywords that heavily inflate spam score (all lower-case). */
    private static final Set<String> SPAM_KEYWORDS = Set.of(
            "free money", "click here", "buy now", "limited offer",
            "100% free", "make money fast", "earn cash", "casino",
            "crypto airdrop", "investment opportunity", "work from home"
    );

    // ─── Result record ────────────────────────────────────────────────────────

    /**
     * Result of a combined rate-limit + spam check.
     *
     * @param blocked     true if the request should be rejected outright (429)
     * @param blockReason human-readable reason if blocked
     * @param spamScore   0–100 score; >= 80 triggers auto-HIDDEN status
     * @param reasonsJson JSON array string of heuristic reasons
     */
    public record SpamCheckResult(boolean blocked, String blockReason,
                                   int spamScore, String reasonsJson) {}

    // ─── Main entry point ─────────────────────────────────────────────────────

    @Transactional
    public SpamCheckResult checkAndRecord(Account account, String ip,
                                          String title, String content,
                                          String ingredients,
                                          List<String> stepDescriptions) {
        List<String> reasons = new ArrayList<>();
        int score = 0;

        // 1. Rate limiting
        Instant windowStart = Instant.now().minusSeconds(windowMinutes * 60L);

        // Per-IP limit (always applies)
        long ipCount = rateLimitEventDAO.countByIpAndTypeAfter(ip, "CREATE_POST", windowStart);
        boolean isTrusted = account != null &&
                postDAO.countApprovedByAccount(account.getAccountID()) >= trustedThreshold;

        int effectiveIpLimit   = isTrusted ? maxPostsPerIp * 2   : maxPostsPerIp;
        int effectiveUserLimit = isTrusted ? maxPostsPerUser * 2 : maxPostsPerUser;

        if (ipCount >= effectiveIpLimit) {
            auditEvent(account, ip, AuditLogService.POST_RATE_LIMITED,
                    Map.of("type", "IP", "count", ipCount, "limit", effectiveIpLimit));
            return new SpamCheckResult(true,
                    "Too many posts from this IP. Please wait before posting again.", 100, "[]");
        }

        if (account != null) {
            long userCount = rateLimitEventDAO.countByUserAndTypeAfter(
                    account.getAccountID(), "CREATE_POST", windowStart);
            if (userCount >= effectiveUserLimit) {
                auditEvent(account, ip, AuditLogService.POST_RATE_LIMITED,
                        Map.of("type", "USER", "count", userCount, "limit", effectiveUserLimit));
                return new SpamCheckResult(true,
                        "You've posted too frequently. Please wait " + windowMinutes + " minutes.", 100, "[]");
            }
        }

        // 2. Duplicate detection
        if (account != null && title != null && !title.isBlank()) {
            LocalDate duplicateSince = LocalDate.now();  // same day is sufficient
            List<?> dupes = postDAO.findDuplicateCandidates(
                    account.getAccountID(), title, duplicateSince);
            if (!dupes.isEmpty()) {
                reasons.add("DUPLICATE_TITLE");
                score += 60;
            }
        }

        // 3. Content heuristics
        String fullText = joinText(title, content, ingredients, stepDescriptions);

        // URL count
        long urlCount = URL_PATTERN.matcher(fullText).results().count();
        if (urlCount >= MAX_URLS) {
            reasons.add("TOO_MANY_URLS:" + urlCount);
            score += Math.min(50, (int) (urlCount * 15));
        }

        // Repeated characters
        if (REPEAT_CHARS_PATTERN.matcher(fullText).find()) {
            reasons.add("REPEATED_CHARS");
            score += 20;
        }

        // Suspicious keywords
        String lower = fullText.toLowerCase();
        for (String kw : SPAM_KEYWORDS) {
            if (lower.contains(kw)) {
                reasons.add("KEYWORD:" + kw.replace(" ", "_").toUpperCase());
                score += 25;
            }
        }

        // Cap score at 100
        score = Math.min(100, score);

        // 4. Record the event (for rate-limit counting in future checks)
        recordEvent(account, ip);

        if (score >= 80) {
            auditEvent(account, ip, AuditLogService.POST_SPAM_BLOCKED,
                    Map.of("score", score, "reasons", reasons));
        }

        return new SpamCheckResult(false, null, score, toJson(reasons));
    }

    // ─── Internals ────────────────────────────────────────────────────────────

    private void recordEvent(Account account, String ip) {
        RateLimitEvent event = RateLimitEvent.builder()
                .user(account)
                .ip(ip != null ? ip : "0.0.0.0")
                .actionType("CREATE_POST")
                .createdAt(Instant.now())
                .build();
        rateLimitEventDAO.save(event);
    }

    private void auditEvent(Account account, String ip, String eventType, Map<String, Object> meta) {
        try {
            auditLogService.log(
                    account != null ? account.getAccountID() : null,
                    eventType, ip, null, meta);
        } catch (Exception e) {
            log.warn("Failed to write audit log for {}: {}", eventType, e.getMessage());
        }
    }

    private String joinText(String title, String content, String ingredients,
                             List<String> stepDescriptions) {
        StringBuilder sb = new StringBuilder();
        if (title != null)       sb.append(title).append(' ');
        if (content != null)     sb.append(content).append(' ');
        if (ingredients != null) sb.append(ingredients).append(' ');
        if (stepDescriptions != null) {
            for (String desc : stepDescriptions) {
                if (desc != null) sb.append(desc).append(' ');
            }
        }
        return sb.toString();
    }

    private String toJson(List<String> list) {
        try { return objectMapper.writeValueAsString(list); }
        catch (JsonProcessingException e) { return "[]"; }
    }
}
