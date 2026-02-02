package poly.edu.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "EventPosts")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventPosts {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer eventPostID;

    @ManyToOne
    @JoinColumn(name = "EventID", nullable = false)
    private Event event;

    @ManyToOne
    @JoinColumn(name = "PostID", nullable = false)
    private Post post;

    @Column(nullable = false)
    private LocalDate createdAt;
}
