package poly.edu.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

/**
 * Persists a password-reset request.
 * Only the SHA-256 hash of the raw token is stored; the raw token is sent via email only.
 */
@Entity
@Table(name = "PasswordResetToken")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PasswordResetToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** FK to Account.accountID */
    @Column(nullable = false)
    private Integer accountId;

    /** SHA-256 hex of the raw token. Never stored in plaintext. */
    @Column(nullable = false, length = 64)
    private String tokenHash;

    @Column(nullable = false)
    private Instant expiresAt;

    /** Set when successfully used — makes the token single-use. */
    private Instant usedAt;

    @Column(nullable = false)
    private Instant createdAt;

    /** The IP that generated this request (for audit / rate-limiting). */
    @Column(length = 45)
    private String requestIp;
}
