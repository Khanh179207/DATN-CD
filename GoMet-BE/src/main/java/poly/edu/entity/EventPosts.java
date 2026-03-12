package poly.edu.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "EventPosts")
public class EventPosts {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer eventPostID;

    @ManyToOne(optional = false)
    @JoinColumn(name = "EventID", nullable = false)
    private Event event;

    @ManyToOne(optional = false)
    @JoinColumn(name = "PostID", nullable = false)
    private Post post;

    @Builder.Default // 🔥 Thêm cái này để lúc nộp bài nó mặc định là 0 vote
    private Integer voteCount = 0;

    @Builder.Default // 🔥 Thêm cái này để lấy thời điểm nộp bài chuẩn
    private LocalDateTime createdAt = LocalDateTime.now();
}