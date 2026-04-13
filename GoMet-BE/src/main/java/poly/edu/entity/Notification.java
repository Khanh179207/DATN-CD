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
    @JoinColumn(name = "AccountID", nullable = false)
    private Account account; // Receiver of notification

    @ManyToOne
    @JoinColumn(name = "ActorID", nullable = true)
    private Account actor; // Person who triggered the action

    @ManyToOne
    @JoinColumn(name = "PostID")
    private Post post;

    @Column(nullable = false)
    private Integer isRead;

    private LocalDateTime readAt;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(length = 500)
    private String link;
}
