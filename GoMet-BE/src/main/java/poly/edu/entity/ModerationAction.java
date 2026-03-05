package poly.edu.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

/**
 * Audit record for every admin moderation action applied to a Post.
 * Maps to the {@code moderation_actions} table.
 */
@Entity
@Table(name = "moderation_actions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModerationAction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id", nullable = false)
    private Account admin;

    @Column(nullable = false, length = 30)
    @Enumerated(EnumType.STRING)
    private ModerationActionType action;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String reason;

    /** Optional extra context (rejection code, spam reasons, etc.) serialised as JSON. */
    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String metaJson;

    @Column(length = 45)
    private String ip;

    @Column(length = 500)
    private String userAgent;

    @Column(nullable = false)
    private Instant createdAt;
}
