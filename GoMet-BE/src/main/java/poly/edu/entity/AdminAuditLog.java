package poly.edu.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

/**
 * Admin action audit log — separate from security AuditLog.
 * Records what admin did what to whom (email sends, bans, role changes, etc.)
 */
@Entity
@Table(name = "admin_audit_logs")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminAuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** The admin who performed the action. */
    @Column(name = "actor_id", nullable = false)
    private Integer actorId;

    /** Structured action type, e.g. EMAIL_JOB_QUEUED, USER_BANNED */
    @Column(name = "action_type", nullable = false, length = 80)
    private String actionType;

    /** Type of resource affected: USER | POST | EMAIL_JOB | SYSTEM */
    @Column(name = "target_type", length = 50)
    private String targetType;

    /** Stringified id of the affected resource (FK-agnostic). */
    @Column(name = "target_id", length = 100)
    private String targetId;

    /** JSON blob with extra context (before/after values, reason, etc.) */
    @Column(name = "meta_json", columnDefinition = "NVARCHAR(MAX)")
    private String metaJson;

    @Column(name = "ip", length = 45)
    private String ip;

    @Column(name = "user_agent", length = 512)
    private String userAgent;

    @Column(name = "created_at", nullable = false)
    @Builder.Default
    private Instant createdAt = Instant.now();

    // ─── Action type constants ────────────────────────────────────────────────
    public static final String EMAIL_JOB_CREATED  = "EMAIL_JOB_CREATED";
    public static final String EMAIL_JOB_QUEUED   = "EMAIL_JOB_QUEUED";
    public static final String EMAIL_JOB_CANCELED = "EMAIL_JOB_CANCELED";
    public static final String EMAIL_JOB_SENT     = "EMAIL_JOB_SENT";
    public static final String EMAIL_JOB_FAILED   = "EMAIL_JOB_FAILED";
    public static final String USER_BANNED        = "USER_BANNED";
    public static final String USER_UNBANNED      = "USER_UNBANNED";
    public static final String USER_FORCE_LOGOUT  = "USER_FORCE_LOGOUT";
    public static final String ROLE_ASSIGNED      = "ROLE_ASSIGNED";
    public static final String ROLE_REVOKED       = "ROLE_REVOKED";
    public static final String POST_APPROVED      = "POST_APPROVED";
    public static final String POST_REJECTED      = "POST_REJECTED";
    public static final String POST_HIDDEN        = "POST_HIDDEN";
    public static final String REPORT_RESOLVED    = "REPORT_RESOLVED";
    public static final String SETTINGS_CHANGED   = "SETTINGS_CHANGED";

    // ─── Target type constants ────────────────────────────────────────────────
    public static final String TARGET_USER      = "USER";
    public static final String TARGET_POST      = "POST";
    public static final String TARGET_EMAIL_JOB = "EMAIL_JOB";
    public static final String TARGET_SYSTEM    = "SYSTEM";
}
