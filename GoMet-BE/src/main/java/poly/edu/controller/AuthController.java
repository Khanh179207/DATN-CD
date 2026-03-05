package poly.edu.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import poly.edu.dao.AccountDAO;
import poly.edu.dao.RefreshTokenDAO;
import poly.edu.dto.*;
import poly.edu.entity.Account;
import poly.edu.entity.MagicLinkToken;
import poly.edu.service.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.*;

/**
 * Authentication controller — enterprise security suite (Phases 1–5).
 *
 * New endpoints added over the legacy controller:
 *   POST /api/auth/refresh              — JWT refresh-token rotation
 *   POST /api/auth/mfa/verify           — TOTP / backup-code challenge
 *   GET  /api/auth/verify-login         — Magic-link device verification
 *   POST /api/auth/logout               — Revoke current or all sessions
 *   POST /api/auth/change-password      — Authenticated password change
 */
@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AccountDAO            accountDAO;
    private final EmailService          emailService;
    private final OtpStore              otpStore;
    private final PasswordResetService  passwordResetService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final AuthService           authService;
    private final TokenService          tokenService;
    private final TotpService           totpService;
    private final MfaSessionStore       mfaSessionStore;
    private final AuditLogService       auditLogService;
    private final TrustedDeviceService  trustedDeviceService;
    private final MagicLinkService      magicLinkService;

    // Field-injected because @RequiredArgsConstructor only covers final fields added before build
    @Autowired private RefreshTokenDAO refreshTokenDAO;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${google.client.id:}")
    private String googleClientId;

    @Value("${app.frontend.url:http://localhost:5173}")
    private String frontendUrl;

    @PostConstruct
    void checkGoogleConfig() {
        if (googleClientId == null || googleClientId.isBlank()) {
            log.warn("[SECURITY] google.client.id is not set — Google token audience is NOT verified. "
                + "Set GOOGLE_CLIENT_ID env var or google.client.id in application.properties.");
        } else {
            log.info("Google Sign-In configured with client_id: {}...{}",
                googleClientId.substring(0, Math.min(8, googleClientId.length())),
                googleClientId.length() > 12 ? googleClientId.substring(googleClientId.length() - 4) : "");
        }
    }

    // ─── LOGIN ────────────────────────────────────────────────────────────────
    /**
     * Main login entry point. Delegates the entire authentication pipeline to
     * {@link AuthService#login(AuthRequestDTO, HttpServletRequest)}.
     * Possible outcomes:
     *  • 200 Success               — full JWT pair in {@link AuthResponseDTO}
     *  • 200 RequiresMfa           — {@code requiresMfa=true} + {@code mfaSessionToken}
     *  • 200 RequiresDeviceVerification — magic link emailed, device not trusted
     *  • 401 Failure               — bad credentials / inactive account
     *  • 429 Blocked               — rate-limit or lockout
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequestDTO req,
                                   HttpServletRequest httpReq) {
        if (req.getEmail() == null || req.getEmail().isBlank() ||
            req.getPassword() == null || req.getPassword().isBlank()) {
            return ResponseEntity.badRequest().body(err("Email and password are required."));
        }

        AuthService.LoginResult result = authService.login(req, httpReq);

        if (result instanceof AuthService.LoginResult.Success) {
            return ResponseEntity.ok(((AuthService.LoginResult.Success) result).response());
        } else if (result instanceof AuthService.LoginResult.RequiresMfa) {
            AuthService.LoginResult.RequiresMfa mfa = (AuthService.LoginResult.RequiresMfa) result;
            AuthResponseDTO r = new AuthResponseDTO();
            r.setRequiresMfa(true);
            r.setMfaSessionToken(mfa.mfaSessionToken());
            return ResponseEntity.ok(r);
        } else if (result instanceof AuthService.LoginResult.RequiresDeviceVerification) {
            AuthResponseDTO r = new AuthResponseDTO();
            r.setRequiresDeviceVerification(true);
            return ResponseEntity.ok(r);
        } else if (result instanceof AuthService.LoginResult.Failure) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(err(((AuthService.LoginResult.Failure) result).message()));
        } else {
            AuthService.LoginResult.Blocked b = (AuthService.LoginResult.Blocked) result;
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(err(b.message()));
        }
    }

    // ─── MFA VERIFY ──────────────────────────────────────────────────────────
    /**
     * Completes a login that required TOTP / backup-code verification.
     * Requires the {@code mfaSessionToken} returned by /login.
     */
    @PostMapping("/mfa/verify")
    public ResponseEntity<?> mfaVerify(@RequestBody @Valid MfaVerifyRequestDTO req,
                                        HttpServletRequest httpReq) {
        Optional<Integer> maybeId = mfaSessionStore.getAccountIdIfValid(req.getMfaSessionToken());
        if (maybeId.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(err("MFA session expired or invalid. Please log in again.",
                    Map.of("code", "MFA_SESSION_INVALID")));
        }
        int accountId = maybeId.get();
        Account account = accountDAO.findById(accountId).orElse(null);
        if (account == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(err("Account not found.", Map.of("code", "ACCOUNT_NOT_FOUND")));
        }

        boolean mfaOk = false;
        if (req.getTotpCode() != null && !req.getTotpCode().isBlank()) {
            mfaOk = totpService.verifyForAccount(account, req.getTotpCode());
            auditLogService.log(accountId,
                    mfaOk ? AuditLogService.MFA_CHALLENGE_SUCCESS : AuditLogService.MFA_CHALLENGE_FAILURE,
                    httpReq, Map.of("method", "TOTP"));
        } else if (req.getBackupCode() != null && !req.getBackupCode().isBlank()) {
            mfaOk = totpService.verifyBackupCode(account, req.getBackupCode());
            if (mfaOk) {
                auditLogService.log(accountId, AuditLogService.MFA_BACKUP_CODE_USED, httpReq,
                        Map.of("remaining", String.valueOf(totpService.countRemainingBackupCodes(account))));
            } else {
                auditLogService.log(accountId, AuditLogService.MFA_CHALLENGE_FAILURE, httpReq,
                        Map.of("method", "BACKUP_CODE"));
            }
        }

        if (!mfaOk) {
            int remaining = mfaSessionStore.registerFailure(req.getMfaSessionToken());
            if (remaining <= 0) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(err("Too many invalid codes. Please log in again.",
                    Map.of("remainingAttempts", 0, "code", "MFA_LOCKED")));
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(err("Invalid authentication code. Please try again.",
                    Map.of("remainingAttempts", remaining, "code", "MFA_CODE_INVALID")));
        }

        // consume session token only after successful MFA verification
        mfaSessionStore.consumeOnSuccess(req.getMfaSessionToken());

        String ip        = RateLimitService.resolveIp(httpReq);
        String userAgent = httpReq.getHeader("User-Agent");
        AuthService.LoginResult result = authService.completeLogin(
                account, req.getDeviceId(), req.getDeviceName(), ip, userAgent, httpReq);

        if (result instanceof AuthService.LoginResult.Success) {
            if (req.getDeviceId() != null && !req.getDeviceId().isBlank()) {
                trustedDeviceService.trustDevice(account.getAccountID(), req.getDeviceId());
            }
            return ResponseEntity.ok(((AuthService.LoginResult.Success) result).response());
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(err("Login completion failed after MFA."));
    }

    // ─── MAGIC LINK DEVICE VERIFICATION ──────────────────────────────────────
    /**
     * Handles magic-link clicks from email.
     * ?action=verify    → trust device, issue tokens, redirect to frontend
     * ?action=wasnt-me  → revoke all sessions + devices, redirect to password reset
     */
    @GetMapping("/verify-login")
    public ResponseEntity<?> verifyLogin(@RequestParam String token,
                                          @RequestParam(defaultValue = "verify") String action,
                                          HttpServletRequest httpReq) {
        Optional<MagicLinkToken> tokenOpt = magicLinkService.consume(token);
        if (tokenOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(err("This verification link is invalid, expired, or already used."));
        }
        MagicLinkToken magicToken = tokenOpt.get();
        Account account = accountDAO.findById(magicToken.getUserId()).orElse(null);
        if (account == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err("Account not found."));
        }

        String ip        = RateLimitService.resolveIp(httpReq);
        String userAgent = httpReq.getHeader("User-Agent");

        boolean isWasntMe = MagicLinkService.PURPOSE_THIS_WASNT_ME.equals(magicToken.getPurpose())
                || "wasnt-me".equals(action);

        if (isWasntMe) {
            tokenService.revokeAllSessions(account.getAccountID());
            trustedDeviceService.revokeAllDevices(account.getAccountID());
            auditLogService.log(account.getAccountID(), AuditLogService.THIS_WASNT_ME, httpReq,
                    Map.of("ip", ip));
            return ResponseEntity.ok(Map.of(
                "message", "All sessions have been revoked. Reset your password to secure your account.",
                "action", "REVOKED_ALL",
                "redirectUrl", frontendUrl + "/forgot-password"
            ));
        }
        String deviceId   = null;
        String deviceName = null;
        if (magicToken.getContextJson() != null) {
            try {
                @SuppressWarnings("unchecked")
                Map<String, Object> ctx = objectMapper.readValue(magicToken.getContextJson(), Map.class);
                deviceId   = (String) ctx.get("deviceId");
                deviceName = (String) ctx.get("deviceName");
            } catch (Exception e) {
                log.debug("Could not parse magic link context JSON: {}", e.getMessage());
            }
        }
        if (deviceId != null && !deviceId.isBlank()) {
            trustedDeviceService.trustDevice(account.getAccountID(), deviceId);
        }
        auditLogService.log(account.getAccountID(), AuditLogService.DEVICE_TRUSTED, httpReq,
                Map.of("deviceId", deviceId != null ? deviceId : ""));

        AuthService.LoginResult result = authService.completeLogin(
                account, deviceId, deviceName, ip, userAgent, httpReq);

        if (result instanceof AuthService.LoginResult.Success) {
            AuthService.LoginResult.Success success = (AuthService.LoginResult.Success) result;
            return ResponseEntity.ok(Map.of(
                    "message", "Device verified successfully.",
                    "redirectUrl", frontendUrl + "/",
                    "auth", authPayload(success.response())
            ));
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(err("Verification link processed but login failed. Please log in again."));
    }

    // ─── REFRESH TOKEN ────────────────────────────────────────────────────────
    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody @Valid RefreshTokenRequestDTO req,
                                      HttpServletRequest httpReq) {
        // Look up the account via the refresh token hash first
        String hash    = TokenService.sha256(req.getRefreshToken());
        Account account = refreshTokenDAO.findByHash(hash)
                .flatMap(rt -> accountDAO.findById(rt.getUserId()))
                .orElse(null);

        if (account == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(err("Invalid or expired refresh token."));
        }

        String ip        = RateLimitService.resolveIp(httpReq);
        String userAgent = httpReq.getHeader("User-Agent");

        Optional<TokenService.RotationResult> rotated =
                tokenService.rotate(req.getRefreshToken(), ip, userAgent, req.getDeviceId(), account);

        if (rotated.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(err("Refresh token invalid, expired, or token reuse detected. Please log in again."));
        }
        TokenService.TokenPair pair = rotated.get().pair();
        return ResponseEntity.ok(authService.buildResponse(account, pair.accessToken(), pair.refreshToken()));
    }

    // ─── LOGOUT ───────────────────────────────────────────────────────────────
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody(required = false) Map<String, String> body,
                                     HttpServletRequest httpReq) {
        Integer accountId = resolveAccountId();
        if (accountId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(err("Not authenticated."));
        }
        // Revoke only the presented refresh token if provided, otherwise revoke all
        if (body != null && body.containsKey("refreshToken")) {
            String rawToken = body.get("refreshToken");
            String hash = TokenService.sha256(rawToken);
            refreshTokenDAO.findByHash(hash).ifPresent(rt -> {
                if (Objects.equals(rt.getUserId(), accountId)) {
                    tokenService.revokeSession(rt.getId(), accountId);
                }
            });
        } else {
            tokenService.revokeAllSessions(accountId);
        }
        auditLogService.log(accountId, AuditLogService.SESSION_REVOKE_ALL, httpReq, Map.of());
        return ResponseEntity.ok(Map.of("message", "Logged out successfully."));
    }

    // ─── FORGOT PASSWORD ──────────────────────────────────────────────────────
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequestDTO req,
                                             HttpServletRequest httpReq) {
        if (req.getIdentifier() == null || req.getIdentifier().isBlank()) {
            return ResponseEntity.badRequest().body(err("Identifier is required"));
        }
        String ip        = RateLimitService.resolveIp(httpReq);
        String userAgent = httpReq.getHeader("User-Agent");
        try {
            boolean found = passwordResetService.processForgotPassword(req.getIdentifier(), ip, userAgent);
            if (!found) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(err("No account found with that email or username."));
            }
        } catch (RuntimeException e) {
            if ("RATE_LIMITED".equals(e.getMessage())) {
                return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                        .body(err("Too many requests. Please wait before trying again."));
            }
            throw e;
        }
        return ResponseEntity.ok(Map.of(
            "message", "A password reset link has been sent to your email."
        ));
    }

    // ─── RESET PASSWORD ───────────────────────────────────────────────────────
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequestDTO req,
                                           HttpServletRequest httpReq) {
        if (req.getToken() == null || req.getToken().isBlank())
            return ResponseEntity.badRequest().body(err("Token is required"));
        if (req.getNewPassword() == null || req.getNewPassword().isBlank())
            return ResponseEntity.badRequest().body(err("New password is required"));
        try {
            Account account = passwordResetService.resetPasswordAndReturnAccount(req.getToken(), req.getNewPassword());
            if (account == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(err("Reset link is invalid or expired. Please request a new one."));
            }

            if (Boolean.TRUE.equals(req.getAutoLogin())) {
                AuthRequestDTO loginReq = new AuthRequestDTO();
                loginReq.setEmail(account.getEmail());
                loginReq.setPassword(req.getNewPassword());
                loginReq.setDeviceId(req.getDeviceId());
                loginReq.setDeviceName(req.getDeviceName());

                AuthService.LoginResult loginResult = authService.login(loginReq, httpReq);
                if (loginResult instanceof AuthService.LoginResult.Success) {
                    AuthService.LoginResult.Success s = (AuthService.LoginResult.Success) loginResult;
                    return ResponseEntity.ok(Map.of(
                            "message", "Password updated and logged in.",
                            "autoLogin", true,
                            "auth", authPayload(s.response())
                    ));
                }
                if (loginResult instanceof AuthService.LoginResult.RequiresMfa) {
                    AuthService.LoginResult.RequiresMfa mfa = (AuthService.LoginResult.RequiresMfa) loginResult;
                    return ResponseEntity.ok(Map.of(
                            "message", "Password updated. Please complete 2FA to continue.",
                            "autoLogin", false,
                            "requiresMfa", true,
                            "mfaSessionToken", mfa.mfaSessionToken()
                    ));
                }
                if (loginResult instanceof AuthService.LoginResult.RequiresDeviceVerification) {
                    return ResponseEntity.ok(Map.of(
                            "message", "Password updated. Please verify login from your email.",
                            "autoLogin", false,
                            "requiresDeviceVerification", true
                    ));
                }
            }

            return ResponseEntity.ok(Map.of("message", "Password updated. You can now log in."));
        } catch (PasswordPolicyService.PasswordPolicyViolationException e) {
            return ResponseEntity.badRequest().body(err(e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(err(e.getMessage()));
        }
    }

    // ─── CHANGE PASSWORD (authenticated) ─────────────────────────────────────
    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody @Valid ChangePasswordRequestDTO req,
                                             HttpServletRequest httpReq) {
        Integer accountId = resolveAccountId();
        if (accountId == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(err("Not authenticated."));

        Account account = accountDAO.findById(accountId).orElse(null);
        if (account == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(err("Account not found."));

        try {
            authService.changePassword(account, req.getCurrentPassword(), req.getNewPassword(), httpReq);
            return ResponseEntity.ok(Map.of("message", "Password changed. All sessions have been revoked."));
        } catch (PasswordPolicyService.PasswordPolicyViolationException e) {
            return ResponseEntity.badRequest().body(err(e.getMessage()));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(err(e.getMessage()));
        }
    }

    // ─── REGISTRATION (OTP flow) ──────────────────────────────────────────────
    @PostMapping("/send-otp")
    public ResponseEntity<?> sendOtp(@RequestBody RegisterRequestDTO req) {
        if (req.getEmail() == null || req.getUsername() == null || req.getPassword() == null)
            return ResponseEntity.badRequest().body(err("All fields are required"));
        if (req.getPassword().length() < 8)
            return ResponseEntity.badRequest().body(err("Password must be at least 8 characters"));
        if (accountDAO.findByEmail(req.getEmail()).isPresent())
            return ResponseEntity.status(HttpStatus.CONFLICT).body(err("This email is already registered"));
        if (accountDAO.findByUsername(req.getUsername()).isPresent())
            return ResponseEntity.status(HttpStatus.CONFLICT).body(err("This username is already taken"));

        String otp = String.format("%06d", new Random().nextInt(1_000_000));
        otpStore.save(req.getEmail(), otp, req.getUsername(), req.getPassword());
        try {
            emailService.sendOtpEmail(req.getEmail(), req.getUsername(), otp);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(err("Failed to send verification email. Please try again."));
        }
        return ResponseEntity.ok(Map.of("message", "Verification code sent to " + req.getEmail()));
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody OtpVerifyRequestDTO req) {
        if (!otpStore.verify(req.getEmail(), req.getOtp()))
            return ResponseEntity.badRequest().body(err("Invalid or expired verification code"));

        var pending = otpStore.getPending(req.getEmail());
        if (accountDAO.findByEmail(req.getEmail()).isPresent()) {
            otpStore.remove(req.getEmail());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(err("Email already registered"));
        }

        String legacyToken = UUID.randomUUID().toString();
        Account acc = Account.builder()
                .username(pending.username())
                .email(req.getEmail())
                .password(passwordEncoder.encode(pending.password()))
                .avatar("")
                .token(legacyToken)
                .point(0)
                .isAdmin(0)
                .isPremium(0)
                .isActive(1)
                .createdAt(LocalDate.now())
                .build();
        accountDAO.save(acc);
        otpStore.remove(req.getEmail());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(authService.buildResponse(acc, null, null));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequestDTO req) {
        return sendOtp(req);
    }

    // ─── ME ───────────────────────────────────────────────────────────────────
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer "))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(err("Not authenticated"));

        String token = authHeader.substring(7);
        // Prefer JWT context (set by JwtAuthFilter)
        Integer accountId = resolveAccountId();
        Optional<Account> opt;
        if (accountId != null) {
            opt = accountDAO.findById(accountId);
        } else {
            // Fall back to legacy UUID token lookup
            opt = accountDAO.findAll().stream()
                    .filter(a -> token.equals(a.getToken()))
                    .findFirst();
        }
        if (opt.isEmpty())
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(err("Invalid token"));

        return ResponseEntity.ok(authService.buildResponse(opt.get(), null, null));
    }

    // ─── GOOGLE SIGN-IN ───────────────────────────────────────────────────────
    @PostMapping("/google")
    public ResponseEntity<?> googleLogin(@RequestBody Map<String, String> body,
                                          HttpServletRequest httpReq) {
        String idTokenString = body.get("idToken");
        if (idTokenString == null || idTokenString.isBlank())
            return ResponseEntity.badRequest().body(err("idToken is required"));
        try {
            GoogleIdToken.Payload payload = verifyGoogleToken(idTokenString);
            if (payload == null)
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(err("Invalid Google token"));

            String email  = payload.getEmail();
            String name   = (String) payload.get("name");
            String avatar = (String) payload.get("picture");
            if (name == null || name.isBlank()) name = email.split("@")[0];

            Optional<Account> existing = accountDAO.findByEmail(email);
            Account acc;
            if (existing.isPresent()) {
                acc = existing.get();
                if (!acc.isAccountActive())
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(err("ACCOUNT_BANNED"));
                if ((acc.getAvatar() == null || acc.getAvatar().isBlank()) && avatar != null)
                    acc.setAvatar(avatar);
            } else {
                acc = Account.builder()
                        .username(generateUniqueUsername(name))
                        .email(email)
                        .password(passwordEncoder.encode(UUID.randomUUID().toString()))
                        .avatar(avatar != null ? avatar : "")
                        .token("")
                        .point(0).isAdmin(0).isPremium(0).isActive(1)
                        .createdAt(LocalDate.now())
                        .build();
                accountDAO.save(acc);
            }

            String ip        = RateLimitService.resolveIp(httpReq);
            String userAgent = httpReq.getHeader("User-Agent");
            TokenService.TokenPair pair = tokenService.issueTokenPair(
                    acc, body.get("deviceId"), ip, userAgent);

            acc.setToken(UUID.randomUUID().toString()); // refresh legacy token
            acc.setLastLoginAt(Instant.now());
            acc.setLastLoginIp(ip);
            accountDAO.save(acc);

            auditLogService.log(acc.getAccountID(), AuditLogService.LOGIN_SUCCESS, httpReq,
                    Map.of("method", "GOOGLE"));

            return ResponseEntity.ok(authService.buildResponse(acc, pair.accessToken(), pair.refreshToken()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(err("Google authentication failed: " + e.getMessage()));
        }
    }

    // ─── HELPERS ─────────────────────────────────────────────────────────────
    /** Extract the authenticated accountId from the Spring Security context. */
    private Integer resolveAccountId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) return null;
        Object principal = auth.getPrincipal();
        if (principal instanceof Integer) return (Integer) principal;
        return null;
    }

    private GoogleIdToken.Payload verifyGoogleToken(String idTokenString) throws Exception {
        GoogleIdTokenVerifier.Builder builder = new GoogleIdTokenVerifier.Builder(
                new NetHttpTransport(), GsonFactory.getDefaultInstance());
        if (googleClientId != null && !googleClientId.isBlank())
            builder.setAudience(Collections.singletonList(googleClientId));
        GoogleIdToken idToken = builder.build().verify(idTokenString);
        return idToken != null ? idToken.getPayload() : null;
    }

    private String generateUniqueUsername(String base) {
        String clean = base.replaceAll("[^a-zA-Z0-9_]", "_").toLowerCase();
        if (clean.length() > 20) clean = clean.substring(0, 20);
        String candidate = clean;
        int counter = 1;
        while (accountDAO.findByUsername(candidate).isPresent()) {
            candidate = clean + counter++;
        }
        return candidate;
    }

    private Map<String, Object> authPayload(AuthResponseDTO auth) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("accountID", auth.getAccountID());
        payload.put("username", auth.getUsername());
        payload.put("email", auth.getEmail());
        payload.put("avatar", auth.getAvatar());
        payload.put("isAdmin", auth.getIsAdmin());
        payload.put("isPremium", auth.getIsPremium());
        payload.put("token", auth.getToken());
        payload.put("accessToken", auth.getAccessToken());
        payload.put("refreshToken", auth.getRefreshToken());
        payload.put("isActive", auth.getIsActive());
        return payload;
    }

    private Map<String, String> err(String message) {
        return Map.of("message", message);
    }

    private Map<String, Object> err(String message, Map<String, Object> extras) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("message", message);
        if (extras != null && !extras.isEmpty()) {
            payload.putAll(extras);
        }
        return payload;
    }
}
