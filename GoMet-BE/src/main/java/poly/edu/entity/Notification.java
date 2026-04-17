package poly.edu.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "Notification")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer notificationID;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "NVARCHAR(MAX)")
    private String content;

    @Column(nullable = false)
    private String type;

    @ManyToOne
    @JoinColumn(name = "AccountID", nullable = true)
    private Account account; // Receiver of notification (nullable for global notifications)

    @ManyToOne
    @JoinColumn(name = "ActorID", nullable = true)
    private Account actor; // Person who triggered the action

    @ManyToOne
    @JoinColumn(name = "PostID")
    private Post post;

    @Column(name = "isRead", nullable = false)
    private Integer isRead;

    @Column(name = "ReadAt")
    private LocalDateTime readAt;

    @Column(name = "CreatedAt", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "Link", length = 500)
    private String link;

    @Column(name = "IsGlobal", nullable = false)
    private Boolean isGlobal;

    @ManyToOne
    @JoinColumn(name = "ParentNotificationID")
    private Notification parentNotification;

    @PrePersist
    public void prePersist() {
        if (this.createdAt == null)
            this.createdAt = LocalDateTime.now();
        if (this.isRead == null)
            this.isRead = 0;
        if (this.isGlobal == null)
            this.isGlobal = false;
    }
}
