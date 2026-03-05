package poly.edu.dto;

import lombok.Data;

@Data
public class ResetPasswordRequestDTO {
    /** The raw token from the email link query-string. */
    private String token;
    /** New plain password from user — will be BCrypt-hashed before storing. */
    private String newPassword;

    /** If true, backend attempts secure login immediately after successful reset. */
    private Boolean autoLogin;

    /** Stable browser/device fingerprint generated on the client. */
    private String deviceId;

    /** Human-readable device label, e.g. "Chrome on Windows". */
    private String deviceName;
}
