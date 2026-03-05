package poly.edu.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "email_jobs")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class EmailJob {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sender_admin_id", nullable = false)
    private Integer senderAdminId;

    @Column(nullable = false, length = 500)
    private String subject;

    @Column(name = "body_html", columnDefinition = "NVARCHAR(MAX)")
    private String bodyHtml;

    @Column(name = "body_text", columnDefinition = "NVARCHAR(MAX)")
    private String bodyText;

    /**
     * Job lifecycle state machine:
     * DRAFT → QUEUED → SENDING → SENT
     *                          → FAILED
     * Any state → CANCELED (if still DRAFT/QUEUED)
     */
    @Column(nullable = false, length = 20)
    @Builder.Default
    private String status = "DRAFT";

    @Column(name = "fail_reason", columnDefinition = "NVARCHAR(MAX)")
    private String failReason;

    @Column(name = "recipient_count", nullable = false)
    @Builder.Default
    private Integer recipientCount = 0;

    @Column(name = "sent_count", nullable = false)
    @Builder.Default
    private Integer sentCount = 0;

    @Column(name = "failed_count", nullable = false)
    @Builder.Default
    private Integer failedCount = 0;

    @Column(name = "created_at", nullable = false)
    @Builder.Default
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at", nullable = false)
    @Builder.Default
    private Instant updatedAt = Instant.now();

    @Column(name = "queued_at")
    private Instant queuedAt;

    @Column(name = "sent_at")
    private Instant sentAt;

    @OneToMany(mappedBy = "job", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    @ToString.Exclude
    private List<EmailJobRecipient> recipients = new ArrayList<>();

    // ─── Status constants ─────────────────────────────────────────────────────
    public static final String STATUS_DRAFT    = "DRAFT";
    public static final String STATUS_QUEUED   = "QUEUED";
    public static final String STATUS_SENDING  = "SENDING";
    public static final String STATUS_SENT     = "SENT";
    public static final String STATUS_FAILED   = "FAILED";
    public static final String STATUS_CANCELED = "CANCELED";
}
