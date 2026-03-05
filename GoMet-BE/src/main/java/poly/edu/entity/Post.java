package poly.edu.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "Post")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer postID;

    @ManyToOne
    @JoinColumn(name = "AccountID", nullable = false)
    private Account account;

    @ManyToOne
    @JoinColumn(name = "CategoryID", nullable = false)
    private Category category;

    @ManyToOne
    @JoinColumn(name = "EventID")
    private Event event;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "NVARCHAR(MAX)")
    private String description;

    @Column(nullable = false, columnDefinition = "NVARCHAR(MAX)")
    private String ingredients;

    private String media;

    @Column(nullable = false)
    private Integer level;

    @Column(nullable = false)
    private Integer cookingTime;

    @Column(nullable = false)
    private Integer views;

    @Column(nullable = false)
    private Integer isActive;

    @Column(nullable = false)
    private Integer isApproved;

    @Column(nullable = false)
    private LocalDate createdAt;

    // ─── Moderation workflow fields ───────────────────────────────────────────

    /** Moderation lifecycle status. Default = PENDING_REVIEW for all new posts. */
    @Column(length = 30)
    @Enumerated(EnumType.STRING)
    private PostStatus status = PostStatus.PENDING_REVIEW;

    /** Human-readable rejection reason — only exposed to post owner and admins. */
    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String rejectionReason;

    /** Short code categorising the rejection (e.g. SPAM, INAPPROPRIATE, OFF_TOPIC). */
    @Column(length = 50)
    private String rejectionCode;

    /** Admin account that last moderated this post. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "moderated_by_id")
    private Account moderatedBy;

    /** UTC timestamp of the last moderation action. */
    private Instant moderatedAt;

    /** UTC timestamp when the post was hidden (HIDE action). */
    private Instant hiddenAt;

    /** Anti-spam score (0 = clean, higher = more suspicious). */
    private Integer spamScore = 0;

    /** JSON array of spam heuristic reasons (stored as string). */
    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String spamReasons;

    /** Last-updated timestamp (set on every save). */
    private Instant updatedAt;

    // Relationships
    @OneToMany(mappedBy = "post")
    private List<Comment> comments;

    @OneToMany(mappedBy = "post")
    private List<Rating> ratings;

    @OneToMany(mappedBy = "post")
    private List<Favorite> favorites;

    @OneToMany(mappedBy = "post")
    private List<History> histories;

    @OneToMany(mappedBy = "post")
    private List<CookingSteps> cookingSteps;

    @OneToMany(mappedBy = "post")
    private List<EventPosts> eventPosts;

    @OneToMany(mappedBy = "post")
    private List<Notification> notifications;

    @OneToMany(mappedBy = "post")
    private List<Report> reports;

}
