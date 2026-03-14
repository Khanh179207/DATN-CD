package poly.edu.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Comment")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer commentID;

    @ManyToOne
    @JoinColumn(name = "AccountID", nullable = false)
    private Account account;

    @ManyToOne
    @JoinColumn(name = "PostID", nullable = true)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cmtid")
    private Comment parentComment;

    @Column(nullable = false, columnDefinition = "NVARCHAR(MAX)")
    private String content;

    @Column(name = "CreatedAt", insertable = false, updatable = false)
    private java.time.LocalDateTime createdAt;
}
