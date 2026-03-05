package poly.edu.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/** Sent by the frontend when completing an MFA challenge (TOTP or backup code). */
@Data
public class MfaVerifyRequestDTO {

    /** The mfaSessionToken from the initial login response. */
    @NotBlank(message = "mfaSessionToken is required")
    private String mfaSessionToken;

    /** 6-digit TOTP code from authenticator app. */
    private String totpCode;

    /** 8-char alphanumeric backup code (used when TOTP device is unavailable). */
    private String backupCode;

    /** Device fingerprint (optional). */
    private String deviceId;
    private String deviceName;
}
