package poly.edu.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import poly.edu.config.JwtTokenProvider;
import poly.edu.dao.RefreshTokenDAO;
import poly.edu.dto.SessionDTO;
import poly.edu.entity.Account;
import poly.edu.entity.RefreshToken;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.HexFormat;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Phase 3 — JWT Access Token + Refresh Token Rotation.
 *
 * - Access token: short-lived JWT (15 min)
 * - Refresh token: random UUID stored as SHA-256 hash (7 days)
 * - Rotation: each use issues a new refresh token, old one is replaced
 * - Reuse detection: if a replaced token is re-presented, ALL user sessions are revoked
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TokenService {

    @Value("${jwt.refresh-token-ttl-seconds:604800}")
    private long refreshTokenTtlSeconds;

    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenDAO  refreshTokenDAO;
    private final AuditLogService  auditLogService;

    // ─── Issue pair ───────────────────────────────────────────────────────────

    /**
     * Issues a new JWT access token + refresh token pair.
     * Persists the refresh token hash to the database.
     */
    @Transactional
    public TokenPair issueTokenPair(Account account, String deviceId, String ip, String userAgent) {
        String roles = account.isAdminAccount() ? "ADMIN,USER" : "USER";
        String accessToken = jwtTokenProvider.issueAccessToken(
                account.getAccountID(), account.getEmail(), roles);

        String rawRefresh = generateSecureToken();
        String refreshHash = sha256(rawRefresh);

        RefreshToken rt = RefreshToken.builder()
                .userId(account.getAccountID())
                .tokenHash(refreshHash)
                .deviceId(deviceId)
                .ip(ip)
                .userAgent(userAgent)
                .createdAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(refreshTokenTtlSeconds))
                .lastUsedAt(Instant.now())
                .build();

        refreshTokenDAO.save(rt);

        return new TokenPair(accessToken, rawRefresh);
    }

    // ─── Rotate ───────────────────────────────────────────────────────────────

    /**
     * Validates the incoming raw refresh token, rotates it (issues a new one),
     * returns the new pair.
     *
     * Returns empty if the token is invalid, expired, or already revoked.
     * If the token was REPLACED (reuse detected), revokes ALL sessions for the user.
     */
    @Transactional
    public Optional<RotationResult> rotate(String rawRefreshToken, String ip, String userAgent,
                                           String deviceId, Account account) {
        String hash = sha256(rawRefreshToken);
        Optional<RefreshToken> tokenOpt = refreshTokenDAO.findByHash(hash);

        if (tokenOpt.isEmpty()) {
            log.warn("Refresh token not found for userId={}", account.getAccountID());
            return Optional.empty();
        }

        RefreshToken existing = tokenOpt.get();

        // ── Reuse detection: token was already replaced ────────────────────
        if (existing.getReplacedByTokenId() != null) {
            log.warn("⚠ REFRESH TOKEN REUSE DETECTED for userId={}, revoking ALL sessions",
                     account.getAccountID());
            refreshTokenDAO.revokeAllForUser(account.getAccountID(), Instant.now());
            auditLogService.log(account.getAccountID(), AuditLogService.REFRESH_TOKEN_REUSE,
                                ip, userAgent, java.util.Map.of("deviceId", deviceId != null ? deviceId : ""));
            return Optional.empty();
        }

        // ── Revoked or expired ─────────────────────────────────────────────
        if (!existing.isValid()) {
            log.debug("Refresh token invalid/expired for userId={}", account.getAccountID());
            return Optional.empty();
        }

        // ── Rotate: issue new pair ─────────────────────────────────────────
        TokenPair newPair = issueTokenPair(account, deviceId, ip, userAgent);

        // Find the new RT row to get its ID
        String newHash = sha256(newPair.refreshToken());
        Optional<RefreshToken> newRt = refreshTokenDAO.findByHash(newHash);

        // Mark old token as replaced
        existing.setReplacedByTokenId(newRt.map(RefreshToken::getId).orElse(null));
        existing.setRevokedAt(Instant.now());
        refreshTokenDAO.save(existing);

        return Optional.of(new RotationResult(newPair, existing));
    }

    // ─── Revoke ───────────────────────────────────────────────────────────────

    @Transactional
    public boolean revokeSession(Long tokenId, Integer requestingUserId) {
        Optional<RefreshToken> rtOpt = refreshTokenDAO.findById(tokenId);
        if (rtOpt.isEmpty()) return false;
        RefreshToken rt = rtOpt.get();
        // Security: users can only revoke their own sessions
        if (!rt.getUserId().equals(requestingUserId)) return false;
        refreshTokenDAO.revokeById(tokenId, Instant.now());
        return true;
    }

    @Transactional
    public void revokeAllSessions(Integer userId) {
        refreshTokenDAO.revokeAllForUser(userId, Instant.now());
    }

    @Transactional
    public void revokeOtherSessions(Integer userId, String currentRefreshToken) {
        String currentHash = sha256(currentRefreshToken);
        Instant now = Instant.now();

        refreshTokenDAO.findActiveSessions(userId, now).stream()
                .filter(token -> !token.getTokenHash().equals(currentHash))
                .forEach(token -> refreshTokenDAO.revokeById(token.getId(), now));
    }

    @Transactional
    public void revokeSessionsCreatedBefore(Integer userId, Instant before) {
        refreshTokenDAO.revokeAllCreatedBefore(userId, before, Instant.now());
    }

    // ─── Sessions list ────────────────────────────────────────────────────────

    public List<SessionDTO> listActiveSessions(Integer userId, String currentTokenHash) {
        return refreshTokenDAO.findActiveSessions(userId, Instant.now()).stream()
                .map(rt -> SessionDTO.builder()
                        .id(rt.getId())
                        .deviceId(rt.getDeviceId())
                        .deviceName(null) // enriched by TrustedDeviceService if needed
                        .ip(rt.getIp())
                        .userAgent(rt.getUserAgent())
                        .createdAt(rt.getCreatedAt())
                        .lastUsedAt(rt.getLastUsedAt())
                        .expiresAt(rt.getExpiresAt())
                        .current(sha256(currentTokenHash).equals(rt.getTokenHash()))
                        .build())
                .collect(Collectors.toList());
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────

    private String generateSecureToken() {
        byte[] bytes = new byte[64];
        new SecureRandom().nextBytes(bytes);
        return HexFormat.of().formatHex(bytes);
    }

    public static String sha256(String input) {
        if (input == null) return "";
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(md.digest(input.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }

    // ─── Value types ──────────────────────────────────────────────────────────

    public record TokenPair(String accessToken, String refreshToken) {}
    public record RotationResult(TokenPair pair, RefreshToken oldToken) {}
}
