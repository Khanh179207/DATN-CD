package poly.edu.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import poly.edu.entity.Account;

/**
 * Phase 2 — Risk-Based Login Assessment.
 *
 * Evaluates whether a login attempt is suspicious based on:
 *   - Is the device known/trusted?
 *   - Did the IP change significantly?
 *   - Is it the first login from this device?
 *
 * Note: IP change alone does NOT trigger verification (users use mobile data, VPNs, etc.).
 * Suspicion is raised when the device is UNKNOWN AND the IP is new.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RiskEngine {

    private final TrustedDeviceService trustedDeviceService;

    // ─── Risk assessment ──────────────────────────────────────────────────────

    /**
     * Returns the risk level for a login attempt.
     */
    public RiskLevel assess(Account account, String deviceId, String ip) {
        // First-ever login (no prior device info)
        boolean hasNoDeviceHistory = account.getLastLoginDeviceId() == null;
        boolean deviceTrusted      = deviceId != null && trustedDeviceService.isDeviceTrusted(account.getAccountID(), deviceId);

        if (deviceTrusted) {
            // Known, trusted device → LOW risk regardless of IP
            return RiskLevel.LOW;
        }

        if (hasNoDeviceHistory) {
            // Very first login ever or no device tracking → medium (trust on first use with email)
            return RiskLevel.MEDIUM;
        }

        // Device not trusted, account has history
        boolean sameDeviceAsLast = deviceId != null && deviceId.equals(account.getLastLoginDeviceId());
        if (sameDeviceAsLast) {
            // Same device fingerprint but not yet formally "trusted" → LOW/MEDIUM
            return RiskLevel.LOW;
        }

        // Different device + no trust record → HIGH (trigger email verification)
        boolean ipChanged = !ip.equals(account.getLastLoginIp());
        if (ipChanged) {
            return RiskLevel.HIGH; // different device + different IP = most suspicious
        }

        // Different device but same IP (e.g. same LAN, different browser) → MEDIUM
        return RiskLevel.MEDIUM;
    }

    /** Is the risk level sufficient to trigger email-based device verification? */
    public boolean requiresVerification(RiskLevel level, Account account, String deviceId) {
        // If no deviceId provided, the client has no device fingerprinting — skip verification
        if (deviceId == null || deviceId.isBlank()) {
            return false;
        }
        // If device is already trusted, never require extra verification
        if (trustedDeviceService.isDeviceTrusted(account.getAccountID(), deviceId)) {
            return false;
        }
        // Only HIGH risk (unknown device + new IP) triggers email verification
        return level == RiskLevel.HIGH;
    }

    public enum RiskLevel {
        LOW,    // Known/trusted device → proceed
        MEDIUM, // New-ish device, same IP or first use → proceed with note
        HIGH    // Unknown device, new IP → require email verification
    }
}
