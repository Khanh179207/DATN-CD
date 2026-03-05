package poly.edu.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import poly.edu.dao.AccountDAO;
import poly.edu.dto.TotpEnableRequestDTO;
import poly.edu.dto.TotpSetupResponseDTO;
import poly.edu.entity.Account;
import poly.edu.service.AuditLogService;
import poly.edu.service.TotpService;

import java.util.Map;

/**
 * TOTP / 2FA management endpoints (all require authentication).
 *
 *   GET    /api/auth/mfa/setup      — generate a TOTP secret + QR + backup codes (not saved yet)
 *   POST   /api/auth/mfa/enable     — verify first TOTP code, persist secret + backup codes
 *   DELETE /api/auth/mfa/disable    — disable TOTP (requires confirmation code)
 *   GET    /api/auth/mfa/status     — whether MFA is currently enabled + remaining backup codes
 */
@Slf4j
@RestController
@RequestMapping("/api/auth/mfa")
@RequiredArgsConstructor
public class TotpController {

    private final AccountDAO      accountDAO;
    private final TotpService     totpService;
    private final AuditLogService auditLogService;

    // ─── SETUP ───────────────────────────────────────────────────────────────
    /**
     * Returns a new TOTP secret, OTPAuth URI for QR scanning, and proposed backup codes.
     * Nothing is saved at this point — the user must confirm via /mfa/enable.
     */
    @GetMapping("/setup")
    public ResponseEntity<?> setup() {
        Account account = resolveAccount();
        if (account == null)
            return unauthorized();
        if (account.isMfaEnabled()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(err("2FA is already enabled. Disable it first to re-enroll."));
        }
        try {
            TotpSetupResponseDTO dto = totpService.setup(account);
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            log.error("TOTP setup failed for account {}: {}", account.getAccountID(), e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(err("Failed to generate QR code. Please try again."));
        }
    }

    // ─── ENABLE ──────────────────────────────────────────────────────────────
    /**
     * Validates the first TOTP code against the provided secret, then persists
     * the encrypted secret and hashed backup codes.
     * <p>
     * Request body:
     * <pre>{
     *   "secret":      "BASE32SECRET",
     *   "totpCode":    "123456",
     *   "backupCodes": ["abc123", ...]   // the codes returned by /setup
     * }</pre>
     */
    @PostMapping("/enable")
    public ResponseEntity<?> enable(@RequestBody @Valid TotpEnableRequestDTO req,
                                     HttpServletRequest httpReq) {
        Account account = resolveAccount();
        if (account == null)
            return unauthorized();
        if (account.isMfaEnabled()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(err("2FA is already enabled."));
        }

        boolean ok = totpService.enableMfa(account, req.getSecret(), req.getTotpCode(),
                req.getBackupCodes());
        if (!ok) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(err("Invalid TOTP code. Check your authenticator app and try again."));
        }

        auditLogService.log(account.getAccountID(), AuditLogService.MFA_ENABLED, httpReq, Map.of());
        return ResponseEntity.ok(Map.of(
            "message", "Two-factor authentication enabled. Store your backup codes securely.",
            "remainingBackupCodes", totpService.countRemainingBackupCodes(
                    accountDAO.findById(account.getAccountID()).orElse(account))
        ));
    }

    // ─── DISABLE ─────────────────────────────────────────────────────────────
    /**
     * Disables TOTP.  Requires the current TOTP code (or a backup code) for confirmation,
     * preventing an attacker who hijacked the session from easily disabling 2FA.
     * <p>
     * Request body (JSON):
     * <pre>{ "totpCode": "123456" }          OR         { "backupCode": "xyz..." }</pre>
     */
    @DeleteMapping("/disable")
    public ResponseEntity<?> disable(@RequestBody Map<String, String> body,
                                      HttpServletRequest httpReq) {
        Account account = resolveAccount();
        if (account == null)
            return unauthorized();
        if (!account.isMfaEnabled()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(err("2FA is not enabled on this account."));
        }

        String totpCode   = body.get("totpCode");
        String backupCode = body.get("backupCode");

        boolean confirmed = false;
        if (totpCode != null && !totpCode.isBlank()) {
            confirmed = totpService.verifyForAccount(account, totpCode);
        } else if (backupCode != null && !backupCode.isBlank()) {
            confirmed = totpService.verifyBackupCode(account, backupCode);
        }

        if (!confirmed) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(err("Confirmation code invalid. Cannot disable 2FA."));
        }

        totpService.disableMfa(account);
        auditLogService.log(account.getAccountID(), AuditLogService.MFA_DISABLED, httpReq, Map.of());
        return ResponseEntity.ok(Map.of("message", "Two-factor authentication has been disabled."));
    }

    // ─── STATUS ──────────────────────────────────────────────────────────────
    /**
     * Returns the current 2FA status for the authenticated user.
     */
    @GetMapping("/status")
    public ResponseEntity<?> status() {
        Account account = resolveAccount();
        if (account == null)
            return unauthorized();

        int remaining = account.isMfaEnabled()
                ? totpService.countRemainingBackupCodes(account)
                : 0;

        return ResponseEntity.ok(Map.of(
            "mfaEnabled",          account.isMfaEnabled(),
            "remainingBackupCodes", remaining
        ));
    }

    // ─── HELPERS ─────────────────────────────────────────────────────────────

    private Account resolveAccount() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) return null;
        Object principal = auth.getPrincipal();
        if (!(principal instanceof Integer)) return null;
        Integer accountId = (Integer) principal;
        return accountDAO.findById(accountId).orElse(null);
    }

    private ResponseEntity<Map<String, String>> unauthorized() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(err("Not authenticated."));
    }

    private Map<String, String> err(String message) {
        return Map.of("message", message);
    }
}
