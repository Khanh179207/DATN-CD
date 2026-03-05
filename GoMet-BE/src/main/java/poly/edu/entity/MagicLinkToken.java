package poly.edu.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

/**
 * Single-use, time-limited magic link token for:
 * - VERIFY_LOGIN  : suspicious-login email verification
 * - THIS_WASNT_ME : revoke sessions + force password reset
 */
@Entity
@Table(
    name = "magic_link_tokens",
    uniqueConstraints = @UniqueConstraint(
        name = "UX_magic_link_tokens_hash",
        columnNames = {"token_hash"}
    )
)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MagicLinkToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    /** SHA-256 hex of the raw token. Raw token goes in email only. */
    @Column(name = "token_hash", nullable = false, length = 64)
    private String tokenHash;

    /**
     * Purpose of this token.
     * Values: VERIFY_LOGIN | THIS_WASNT_ME
     */
    @Column(name = "purpose", nullable = false, length = 50)
    private String purpose;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    /** Set on first use — prevents replay. */
    @Column(name = "used_at")
    private Instant usedAt;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "created_ip", length = 45)
    private String createdIp;

    @Column(name = "created_ua_hash", length = 64)
    private String createdUaHash;

    /** Extra context (e.g. the device_id being verified, serialized as JSON). */
    @Column(name = "context_json", length = 1000)
    private String contextJson;

    public boolean isValid() {
        return usedAt == null && expiresAt.isAfter(Instant.now());
    }
}
