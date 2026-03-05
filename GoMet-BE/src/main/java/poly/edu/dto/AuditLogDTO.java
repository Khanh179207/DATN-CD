package poly.edu.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class AuditLogDTO {
    private Long id;
    private String eventType;
    private String eventMetaJson;
    private String ip;
    private Instant createdAt;
}
