package poly.edu.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

/** A single active session entry for the "Manage Sessions" UI. */
@Data
@Builder
public class SessionDTO {
    private Long id;
    private String deviceId;
    private String deviceName;
    private String ip;
    private String userAgent;
    private Instant createdAt;
    private Instant lastUsedAt;
    private Instant expiresAt;
    /** true if this session is the one making the current request. */
    private boolean current;
}
