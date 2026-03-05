package poly.edu.dto;

import lombok.*;
import poly.edu.entity.AdminAuditLog;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminAuditLogDTO {
    private Long    id;
    private Integer actorId;
    private String  actorName;
    private String  actionType;
    private String  targetType;
    private String  targetId;
    private String  metaJson;
    private String  ip;
    private String  userAgent;
    private Instant createdAt;

    public static AdminAuditLogDTO fromEntity(AdminAuditLog log) {
        return AdminAuditLogDTO.builder()
            .id(log.getId())
            .actorId(log.getActorId())
            .actionType(log.getActionType())
            .targetType(log.getTargetType())
            .targetId(log.getTargetId())
            .metaJson(log.getMetaJson())
            .ip(log.getIp())
            .userAgent(log.getUserAgent())
            .createdAt(log.getCreatedAt())
            .build();
    }
}
