package poly.edu.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

/**
 * One-time TOTP backup code.
 * 8-10 codes are generated when a user enables 2FA.
 * Each can be used once to bypass TOTP (e.g., device lost).
 */
@Entity
@Table(name = "backup_codes")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BackupCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    /** SHA-256 hex of the raw 8-char alphanumeric backup code. */
    @Column(name = "code_hash", nullable = false, length = 64)
    private String codeHash;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    /** Set when the code is used — prevents reuse. */
    @Column(name = "used_at")
    private Instant usedAt;

    public boolean isUsed() {
        return usedAt != null;
    }
}
