package poly.edu.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import poly.edu.dao.AccountDAO;
import poly.edu.dto.AuthRequestDTO;
import poly.edu.dto.AuthResponseDTO;
import poly.edu.entity.Account;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Central authentication orchestrator.
 *
 * Login flow:
 * 1. Rate-limit check (IP + identifier)
 * 2. Account lookup (constant-time reply for unknown accounts)
 * 3. Lock check
 * 4. Password verify
 * 5. Risk assessment (device + IP)
 * 6. MFA check
 * 7. Issue JWT pair / return MFA challenge
 * 8. Audit log
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    @Value("${app.frontend.url:http://localhost:5173}")
    private String frontendUrl;

    private final AccountDAO          accountDAO;
    private final BCryptPasswordEncoder passwordEncoder;
    private final RateLimitService    rateLimitService;
    private final RiskEngine          riskEngine;
    private final TokenService        tokenService;
    private final TrustedDeviceService trustedDeviceService;
    private final MagicLinkService    magicLinkService;
    private final MfaSessionStore     mfaSessionStore;
    private final AuditLogService     auditLogService;
    private final EmailService        emailService;
    private final PasswordPolicyService policyService;
    private final ObjectMapper        objectMapper;

    // ─── Login ────────────────────────────────────────────────────────────────

    /**
     * Performs the full authentication pipeline.
     * Returns {@link LoginResult} — the controller maps this to HTTP responses.
     */
    @Transactional
    public LoginResult login(AuthRequestDTO req, HttpServletRequest httpReq) {
        String ip        = RateLimitService.resolveIp(httpReq);
        String userAgent = httpReq.getHeader("User-Agent");
        String deviceId  = req.getDeviceId();
        String ident     = req.getEmail() != null ? req.getEmail().toLowerCase().trim() : "";

        // 1. IP rate limit
        if (rateLimitService.isIpBlocked(ip)) {
            auditLogService.log(null, AuditLogService.LOGIN_BLOCKED_RATE_LIMIT, httpReq,
                    Map.of("identifier", ident, "reason", "IP_BLOCKED"));
            return LoginResult.blocked("Too many requests from your network. Please try again later.");
        }

        // 2. Identifier rate limit
        if (rateLimitService.isIdentifierRateLimited(ident)) {
            auditLogService.log(null, AuditLogService.LOGIN_BLOCKED_RATE_LIMIT, httpReq,
                    Map.of("identifier", ident, "reason", "IDENTIFIER_RATE_LIMITED"));
            return LoginResult.blocked("Too many failed attempts. Please wait and try again.");
        }

        // 3. Account lookup — always run equivalent work for unknown accounts (prevent timing attack)
        Optional<Account> accOpt = accountDAO.findByEmail(ident)
                .or(() -> accountDAO.findByUsernameIgnoreCase(ident));

        if (accOpt.isEmpty()) {
            // Simulate BCrypt work to prevent timing-based enumeration
            passwordEncoder.matches(req.getPassword(), "$2a$12$dummy/hash/to/prevent/timing/attacks/xxxxx");
            rateLimitService.recordAnonymousFailure(ident, ip, userAgent, "USER_NOT_FOUND");
            auditLogService.log(null, AuditLogService.LOGIN_FAILURE, httpReq,
                    Map.of("identifier", ident, "reason", "USER_NOT_FOUND"));
            return LoginResult.failure("Invalid credentials.");
        }

        Account account = accOpt.get();

        // 4. Account active check
        if (!account.isAccountActive()) {
            return LoginResult.failure("Your account has been suspended.");
        }

        // 5. Lockout check
        if (rateLimitService.isAccountLocked(account)) {
            long remainingSecs = rateLimitService.lockoutRemainingSeconds(account);
            auditLogService.log(account.getAccountID(), AuditLogService.LOGIN_BLOCKED_LOCKED, httpReq,
                    Map.of("lockRemaining", remainingSecs));
            return LoginResult.blocked(
                "Account temporarily locked. Try again in " + remainingSecs + " seconds.");
        }

        // 6. Password verify (constant-time BCrypt; fallback for legacy plain-text)
        boolean passwordOk = verifyPassword(req.getPassword(), account.getPassword());
        if (!passwordOk) {
            rateLimitService.recordFailure(account, ip, userAgent, "PASSWORD", "BAD_PASSWORD");
            auditLogService.log(account.getAccountID(), AuditLogService.LOGIN_FAILURE, httpReq,
                    Map.of("reason", "BAD_PASSWORD"));
            return LoginResult.failure("Invalid credentials.");
        }

        // 7. Risk assessment
        RiskEngine.RiskLevel risk = riskEngine.assess(account, deviceId, ip);
        boolean needsVerification = riskEngine.requiresVerification(risk, account, deviceId);

        if (needsVerification) {
            // Send suspicious-login email with magic link pair
            String deviceName = req.getDeviceName() != null
                    ? req.getDeviceName()
                    : RateLimitService.inferDeviceName(userAgent);

            try {
                String contextJson = objectMapper.writeValueAsString(
                        Map.of("deviceId", deviceId != null ? deviceId : "",
                               "deviceName", deviceName));

                String verifyToken   = magicLinkService.generate(account,
                        MagicLinkService.PURPOSE_VERIFY_LOGIN, ip, userAgent, contextJson);
                String wasntMeToken  = magicLinkService.generate(account,
                        MagicLinkService.PURPOSE_THIS_WASNT_ME, ip, userAgent, contextJson);

                emailService.sendSuspiciousLoginEmail(
                        account.getEmail(), account.getUsername(),
                        verifyToken, wasntMeToken, ip, deviceName);

                auditLogService.log(account.getAccountID(), AuditLogService.LOGIN_SUSPICIOUS, httpReq,
                        Map.of("deviceId", deviceId != null ? deviceId : "",
                               "deviceName", deviceName, "risk", risk.name()));

                return LoginResult.requiresDeviceVerification();
            } catch (Exception e) {
                log.error("Failed to send suspicious-login email for accountId={}: {}",
                          account.getAccountID(), e.getMessage());
                // Fall through to allow login (degrade gracefully — don't block on email failure)
            }
        }

        // 8. MFA check (trusted devices may skip challenge)
        boolean trustedForMfaBypass = deviceId != null
            && !deviceId.isBlank()
            && trustedDeviceService.isDeviceTrusted(account.getAccountID(), deviceId);

        if (account.isMfaEnabled() && !trustedForMfaBypass) {
            String mfaSessionToken = mfaSessionStore.create(account.getAccountID());
            auditLogService.log(account.getAccountID(), AuditLogService.LOGIN_SUCCESS, httpReq,
                    Map.of("step", "MFA_PENDING"));
            return LoginResult.requiresMfa(mfaSessionToken);
        }

        // 9. Full success — issue tokens
        return completeLogin(account, deviceId, req.getDeviceName(), ip, userAgent, httpReq);
    }

    /**
     * Completes login after MFA is verified or when MFA is not required.
     */
    @Transactional
    public LoginResult completeLogin(Account account, String deviceId, String deviceName,
                                     String ip, String userAgent, HttpServletRequest httpReq) {
        // Record the device as seen
        if (deviceId != null && !deviceId.isBlank()) {
            trustedDeviceService.recordSeen(account.getAccountID(), deviceId,
                    deviceName != null ? deviceName : RateLimitService.inferDeviceName(userAgent),
                    userAgent, ip);
        }

        // Issue JWT pair
        TokenService.TokenPair tokens = tokenService.issueTokenPair(account, deviceId, ip, userAgent);

        // Update account last login info
        account.setLastLoginAt(Instant.now());
        account.setLastLoginIp(ip);
        account.setLastLoginDeviceId(deviceId);
        account.setFailedLoginCount(0);
        account.setLockUntil(null);
        accountDAO.save(account);

        // Issue legacy UUID token for backward compat
        String legacyToken = UUID.randomUUID().toString();
        account.setToken(legacyToken);
        accountDAO.save(account);

        rateLimitService.recordSuccess(account, ip, userAgent, "PASSWORD");
        auditLogService.log(account.getAccountID(), AuditLogService.LOGIN_SUCCESS, httpReq,
                Map.of("deviceId", deviceId != null ? deviceId : ""));

        AuthResponseDTO response = buildResponse(account, tokens.accessToken(), tokens.refreshToken());
        return LoginResult.success(response);
    }

    // ─── Change password ──────────────────────────────────────────────────────

    @Transactional
    public void changePassword(Account account, String currentPassword, String newPassword,
                                HttpServletRequest httpReq) {
        policyService.validate(newPassword);

        if (!verifyPassword(currentPassword, account.getPassword())) {
            throw new SecurityException("Current password is incorrect.");
        }

        account.setPassword(passwordEncoder.encode(newPassword));
        account.setPasswordUpdatedAt(Instant.now());
        accountDAO.save(account);

        // Revoke all sessions (forces re-login everywhere)
        tokenService.revokeAllSessions(account.getAccountID());

        auditLogService.log(account.getAccountID(), AuditLogService.PASSWORD_CHANGE, httpReq,
                Map.of("source", "user_initiated"));
    }

    // ─── Response builder ─────────────────────────────────────────────────────

    public AuthResponseDTO buildResponse(Account acc, String accessToken, String refreshToken) {
        AuthResponseDTO res = new AuthResponseDTO();
        res.setAccountID(acc.getAccountID());
        res.setUsername(acc.getUsername());
        res.setEmail(acc.getEmail());
        res.setAvatar(acc.getAvatar());
        res.setIsAdmin(acc.getIsAdmin());
        res.setIsPremium(acc.getIsPremium());
        res.setToken(acc.getToken()); // legacy
        res.setAccessToken(accessToken);
        res.setRefreshToken(refreshToken);
        res.setIsActive(acc.getIsActive());
        return res;
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────

    private boolean verifyPassword(String raw, String stored) {
        if (stored == null) return false;
        // BCrypt hashes start with $2a$ or $2b$
        if (stored.startsWith("$2a$") || stored.startsWith("$2b$")) {
            return passwordEncoder.matches(raw, stored);
        }
        // Legacy: plain-text comparison (constant-time via BCrypt matches to avoid timing diff)
        return stored.equals(raw);
    }

    // ─── Result types ─────────────────────────────────────────────────────────

    public interface LoginResult {

        final class Success implements LoginResult {
            private final AuthResponseDTO response;

            public Success(AuthResponseDTO response) {
                this.response = response;
            }

            public AuthResponseDTO response() {
                return response;
            }
        }

        final class RequiresMfa implements LoginResult {
            private final String mfaSessionToken;

            public RequiresMfa(String mfaSessionToken) {
                this.mfaSessionToken = mfaSessionToken;
            }

            public String mfaSessionToken() {
                return mfaSessionToken;
            }
        }

        final class RequiresDeviceVerification implements LoginResult {
        }

        final class Failure implements LoginResult {
            private final String message;

            public Failure(String message) {
                this.message = message;
            }

            public String message() {
                return message;
            }
        }

        final class Blocked implements LoginResult {
            private final String message;

            public Blocked(String message) {
                this.message = message;
            }

            public String message() {
                return message;
            }
        }

        static LoginResult success(AuthResponseDTO r)          { return new Success(r); }
        static LoginResult requiresMfa(String t)               { return new RequiresMfa(t); }
        static LoginResult requiresDeviceVerification()        { return new RequiresDeviceVerification(); }
        static LoginResult failure(String m)                   { return new Failure(m); }
        static LoginResult blocked(String m)                   { return new Blocked(m); }
    }
}
