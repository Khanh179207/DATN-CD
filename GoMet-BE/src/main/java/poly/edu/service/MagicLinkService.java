package poly.edu.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import poly.edu.dao.MagicLinkTokenDAO;
import poly.edu.entity.Account;
import poly.edu.entity.MagicLinkToken;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.HexFormat;
import java.util.Optional;

/**
 * Phase 2 — Suspicious Login Email Verification (Magic Link).
 *
 * Purposes:
 *   VERIFY_LOGIN   → User clicks link to trust new device and complete login
 *   THIS_WASNT_ME  → User reports fraud → revoke all sessions + force password reset
 *
 * Token lifecycle:
 *   - Raw token sent in email (URL query param)
 *   - SHA-256 hash stored in DB
 *   - Single-use (usedAt set on consumption)
 *   - Expires in 10 minutes
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MagicLinkService {

    public static final String PURPOSE_VERIFY_LOGIN  = "VERIFY_LOGIN";
    public static final String PURPOSE_THIS_WASNT_ME = "THIS_WASNT_ME";

    private static final long TOKEN_TTL_MIN = 10; // 10 minutes
    private static final int  MAX_RECENT    = 3;  // max 3 active tokens per purpose

    private final MagicLinkTokenDAO magicLinkTokenDAO;

    // ─── Generate ─────────────────────────────────────────────────────────────

    /**
     * Generates and persists a magic link token.
     * Returns the raw token (for inclusion in the email URL).
     */
    @Transactional
    public String generate(Account account, String purpose, String ip, String userAgent,
                           String contextJson) {
        // Invalidate any pending tokens for this user+purpose (avoid confusion)
        Instant since = Instant.now().minusSeconds(TOKEN_TTL_MIN * 60);
        long recent = magicLinkTokenDAO.countRecentByUserAndPurpose(
                account.getAccountID(), purpose, since);
        if (recent >= MAX_RECENT) {
            log.warn("Rate-limited magic link for userId={} purpose={}", account.getAccountID(), purpose);
            // Silently invalidate oldest and issue a new one
            magicLinkTokenDAO.invalidateAllForUser(account.getAccountID(), Instant.now());
        }

        String rawToken = generateSecureToken();
        String hash     = sha256(rawToken);

        MagicLinkToken token = MagicLinkToken.builder()
                .userId(account.getAccountID())
                .tokenHash(hash)
                .purpose(purpose)
                .expiresAt(Instant.now().plusSeconds(TOKEN_TTL_MIN * 60))
                .createdAt(Instant.now())
                .createdIp(ip)
                .createdUaHash(RateLimitService.sha256(userAgent))
                .contextJson(contextJson)
                .build();

        magicLinkTokenDAO.save(token);
        log.debug("Magic link generated for userId={} purpose={}", account.getAccountID(), purpose);
        return rawToken;
    }

    // ─── Consume ──────────────────────────────────────────────────────────────

    /**
     * Validates and marks the token as used.
     * Returns the token entity (including contextJson) so the caller can act on it.
     */
    @Transactional
    public Optional<MagicLinkToken> consume(String rawToken) {
        String hash = sha256(rawToken);
        Optional<MagicLinkToken> tokenOpt = magicLinkTokenDAO.findValid(hash, Instant.now());

        if (tokenOpt.isEmpty()) {
            log.debug("Magic link invalid or expired for hash={}", hash.substring(0, 8) + "...");
            return Optional.empty();
        }

        MagicLinkToken token = tokenOpt.get();
        token.setUsedAt(Instant.now());
        magicLinkTokenDAO.save(token);

        return Optional.of(token);
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────

    private String generateSecureToken() {
        byte[] bytes = new byte[32]; // 256 bits
        new SecureRandom().nextBytes(bytes);
        return HexFormat.of().formatHex(bytes);
    }

    public static String sha256(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(md.digest(input.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }
}
