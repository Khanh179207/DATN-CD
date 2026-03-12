package poly.edu.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
@Builder
public class SecurityRiskDTO {
    private String riskLevel;
    private boolean requiresVerification;
    private boolean deviceTrusted;
    private boolean hasNoDeviceHistory;
    private boolean sameDeviceAsLast;
    private boolean ipChanged;
    private String latestDeviceId;
    private String latestIp;
    private Instant latestActivityAt;
    private int activeSessionCount;
    private int trustedDeviceCount;
    private List<String> reasons;
    private List<String> recommendedActions;
}