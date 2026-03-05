package poly.edu.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

/**
 * DB-backed event log used for post / comment rate limiting when Redis is unavailable.
 * Maps to the {@code rate_limit_events} table.
 */
@Entity
@Table(name = "rate_limit_events")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RateLimitEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** NULL for anonymous or unauthenticated requests. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Account user;

    @Column(nullable = false, length = 45)
    private String ip;

    /**
     * Discriminator for what resource was created.
     * Expected values: {@code CREATE_POST}, {@code CREATE_COMMENT}.
     */
    @Column(nullable = false, length = 50)
    private String actionType;

    @Column(nullable = false)
    private Instant createdAt;
}
