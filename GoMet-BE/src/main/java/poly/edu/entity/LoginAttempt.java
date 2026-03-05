package poly.edu.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

/**
 * Records every login attempt (success or failure) for audit and rate-limit purposes.
 */
@Entity
@Table(name = "login_attempts")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** NULL when the account doesn't exist (prevents enumeration via join). */
    @Column(name = "user_id")
    private Integer userId;

    /** The email/username submitted in the login form. */
    @Column(name = "identifier", nullable = false)
    private String identifier;

    @Column(name = "ip", nullable = false, length = 45)
    private String ip;

    /** SHA-256 of the raw User-Agent string. */
    @Column(name = "user_agent_hash", length = 64)
    private String userAgentHash;

    /**
     * Type of attempt: PASSWORD, OTP, MFA_TOTP, MFA_BACKUP, GOOGLE.
     */
    @Column(name = "attempt_type", nullable = false, length = 50)
    private String attemptType;

    @Column(name = "success", nullable = false)
    @Builder.Default
    private boolean success = false;

    /** Short reason string on failure (BAD_PASSWORD, LOCKED, MFA_FAIL, etc.). */
    @Column(name = "failure_reason", length = 100)
    private String failureReason;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
}
