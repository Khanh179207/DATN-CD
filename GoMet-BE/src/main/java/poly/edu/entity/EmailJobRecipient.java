package poly.edu.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "email_job_recipients")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class EmailJobRecipient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id", nullable = false)
    @ToString.Exclude
    private EmailJob job;

    /** Nullable — may be null when sending to external email not in system */
    @Column(name = "user_id")
    private Integer userId;

    @Column(nullable = false, length = 255)
    private String email;

    /** QUEUED | SENT | FAILED | SKIPPED */
    @Column(nullable = false, length = 20)
    @Builder.Default
    private String status = "QUEUED";

    @Column(name = "fail_reason", columnDefinition = "NVARCHAR(MAX)")
    private String failReason;

    @Column(name = "sent_at")
    private Instant sentAt;

    // Status constants
    public static final String STATUS_QUEUED  = "QUEUED";
    public static final String STATUS_SENT    = "SENT";
    public static final String STATUS_FAILED  = "FAILED";
    public static final String STATUS_SKIPPED = "SKIPPED";
}
