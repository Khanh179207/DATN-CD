package poly.edu.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SecurityStreamEventDTO {
    private AuditLogDTO event;
    private SecurityRiskDTO risk;
}