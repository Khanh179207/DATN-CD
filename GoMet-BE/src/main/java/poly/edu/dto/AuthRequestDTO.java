package poly.edu.dto;

import lombok.Data;

@Data
public class AuthRequestDTO {
    // ── existing fields (backward-compatible) ──
    private String email;
    private String password;

    // ── new device-tracking fields (optional, sent by upgraded FE) ──
    /** Stable browser/device fingerprint generated on the client. */
    private String deviceId;
    /** Human-readable label, e.g. "Chrome on Windows". */
    private String deviceName;
}
