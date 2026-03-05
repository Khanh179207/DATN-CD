package poly.edu.dto;

import lombok.Data;

@Data
public class AuthResponseDTO {
    // ── existing fields (backward-compatible) ──
    private Integer accountID;
    private String username;
    private String email;
    private String avatar;
    private Integer isAdmin;
    private Integer isPremium;

    /**
     * Legacy UUID bearer token — kept for backward-compat.
     * New clients should use accessToken + refreshToken.
     */
    private String token;

    // ── new JWT fields ──
    /** Short-lived JWT access token (15 min). */
    private String accessToken;
    /** Long-lived refresh token (7 days), used to rotate access tokens. */
    private String refreshToken;

    // ── MFA challenge fields ──
    /** true = MFA step required; use mfaSessionToken to complete. */
    private boolean requiresMfa;
    /**
     * Short-lived opaque token (UUID) passed back to /auth/mfa/verify.
     * Identifies the pending authentication session without revealing account details.
     * Only set when requiresMfa = true.
     */
    private String mfaSessionToken;

    // ── Device-verification fields ──
    /** true = device not yet trusted; email verification sent. */
    private boolean requiresDeviceVerification;
    /** Token to complete device verification — sent to /auth/verify-login?token=... */
    private String deviceVerificationHint;

    // ── Account status ──
    /** 1 = active, 0 = banned. Included so the FE can detect bans without a separate endpoint. */
    private Integer isActive;
}
