package poly.edu.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AuthRequestDTO {
    // ── existing fields (backward-compatible) ──
    @NotBlank(message = "Email or username is required")
    @Size(max = 255, message = "Email or username must be at most 255 characters")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 128, message = "Password must be between 8 and 128 characters")
    private String password;

    // ── new device-tracking fields (optional, sent by upgraded FE) ──
    /** Stable browser/device fingerprint generated on the client. */
    @Size(max = 255, message = "Device ID must be at most 255 characters")
    private String deviceId;
    /** Human-readable label, e.g. "Chrome on Windows". */
    @Size(max = 255, message = "Device name must be at most 255 characters")
    private String deviceName;
}
