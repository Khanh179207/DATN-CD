package poly.edu.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

/**
 * Stores a device fingerprint that has been verified and trusted by a user.
 * A device is "trusted" when the user completes email verification on first login.
 */
@Entity
@Table(
    name = "trusted_devices",
    uniqueConstraints = @UniqueConstraint(
        name = "UX_trusted_devices_user_device",
        columnNames = {"user_id", "device_id"}
    )
)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrustedDevice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** FK → Account.accountID */
    @Column(name = "user_id", nullable = false)
    private Integer userId;

    /** Client-generated stable fingerprint (e.g. browser fingerprint hash). */
    @Column(name = "device_id", nullable = false)
    private String deviceId;

    /** Human-readable label e.g. "Chrome on Windows". */
    @Column(name = "device_name")
    private String deviceName;

    /** SHA-256 of the raw User-Agent string. */
    @Column(name = "user_agent_hash", nullable = false, length = 64)
    private String userAgentHash;

    @Column(name = "first_seen_at", nullable = false)
    private Instant firstSeenAt;

    @Column(name = "last_seen_at", nullable = false)
    private Instant lastSeenAt;

    @Column(name = "last_ip", length = 45)
    private String lastIp;

    /** false = untrusted/pending verification; true = verified. */
    @Column(name = "is_trusted", nullable = false)
    @Builder.Default
    private boolean isTrusted = false;

    /** Set when user or admin revokes this device. */
    @Column(name = "revoked_at")
    private Instant revokedAt;

    public boolean isActive() {
        return isTrusted && revokedAt == null;
    }
}
