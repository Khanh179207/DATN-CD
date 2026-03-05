package poly.edu.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

/**
 * Immutable append-only audit log entry.
 * Written by AuditLogService for every security-relevant event.
 */
@Entity
@Table(name = "audit_logs")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** NULL for unauthenticated events (e.g., failed login with unknown email). */
    @Column(name = "user_id")
    private Integer userId;

    /**
     * Structured event type identifier. Examples:
     *   LOGIN_SUCCESS, LOGIN_FAILURE, LOGIN_SUSPICIOUS,
     *   DEVICE_TRUSTED, DEVICE_REVOKED,
     *   PASSWORD_CHANGE, PASSWORD_RESET_REQUEST, PASSWORD_RESET_SUCCESS,
     *   MFA_ENABLED, MFA_DISABLED, MFA_CHALLENGE_SUCCESS, MFA_CHALLENGE_FAILURE,
     *   SESSION_REVOKE, SESSION_REVOKE_ALL,
     *   ACCOUNT_LOCKED, ACCOUNT_UNLOCKED,
     *   REFRESH_TOKEN_REUSE_DETECTED
     */
    @Column(name = "event_type", nullable = false, length = 100)
    private String eventType;

    /** JSON blob with extra context (device_id, ip_prev/new, reason, etc.). */
    @Column(name = "event_meta_json", columnDefinition = "NVARCHAR(MAX)")
    private String eventMetaJson;

    @Column(name = "ip", length = 45)
    private String ip;

    @Column(name = "user_agent", length = 512)
    private String userAgent;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
}
