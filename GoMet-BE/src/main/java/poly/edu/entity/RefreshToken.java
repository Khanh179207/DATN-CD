package poly.edu.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

/**
 * Persists a refresh token (JWT rotation model).
 * Only the SHA-256 hash of the raw token is stored.
 * Reuse detection: if a token that was already replaced is presented,
 * revoke the entire chain (replaced_by_token_id chain).
 */
@Entity
@Table(
    name = "refresh_tokens",
    uniqueConstraints = @UniqueConstraint(
        name = "UX_refresh_tokens_hash",
        columnNames = {"token_hash"}
    )
)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    /** SHA-256 hex of the raw refresh token. */
    @Column(name = "token_hash", nullable = false, length = 64)
    private String tokenHash;

    @Column(name = "device_id")
    private String deviceId;

    @Column(name = "ip", length = 45)
    private String ip;

    @Column(name = "user_agent", length = 512)
    private String userAgent;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    /** Set when the token is revoked (logout or rotation). */
    @Column(name = "revoked_at")
    private Instant revokedAt;

    /**
     * Points to the new token that replaced this one during rotation.
     * If this field is set AND the token is presented again → token reuse detected.
     */
    @Column(name = "replaced_by_token_id")
    private Long replacedByTokenId;

    @Column(name = "last_used_at")
    private Instant lastUsedAt;

    public boolean isValid() {
        return revokedAt == null
            && replacedByTokenId == null
            && expiresAt.isAfter(Instant.now());
    }

    public boolean isRevoked() {
        return revokedAt != null;
    }
}
