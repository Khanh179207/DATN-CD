package poly.edu.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "Likes")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Likes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer likeID;

    @ManyToOne
    @JoinColumn(name = "AccountID", nullable = false)
    private Account account;

    @ManyToOne
    @JoinColumn(name = "PostID", nullable = false)
    private Post post;

    @Column(name = "CreatedAt")
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }
}