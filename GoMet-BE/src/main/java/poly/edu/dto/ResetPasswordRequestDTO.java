package poly.edu.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ResetPasswordRequestDTO {
    /** The raw token from the email link query-string. */
    @NotBlank(message = "Token is required")
    @Size(max = 512, message = "Token must be at most 512 characters")
    private String token;

    /** New plain password from user — will be BCrypt-hashed before storing. */
    @NotBlank(message = "New password is required")
    @Size(min = 8, max = 128, message = "New password must be between 8 and 128 characters")
    private String newPassword;

    /** If true, backend attempts secure login immediately after successful reset. */
    private Boolean autoLogin;

    /** Stable browser/device fingerprint generated on the client. */
    @Size(max = 255, message = "Device ID must be at most 255 characters")
    private String deviceId;

    /** Human-readable device label, e.g. "Chrome on Windows". */
    @Size(max = 255, message = "Device name must be at most 255 characters")
    private String deviceName;
}
