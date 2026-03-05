package poly.edu.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

/** Details for a trusted device entry (shown in device management page). */
@Data
@Builder
public class TrustedDeviceDTO {
    private Long id;
    private String deviceId;
    private String deviceName;
    private String lastIp;
    private Instant firstSeenAt;
    private Instant lastSeenAt;
    private boolean trusted;
}
