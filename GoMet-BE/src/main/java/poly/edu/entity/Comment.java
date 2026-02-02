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
    @JoinColumn(name = "PostID", nullable = false)
    private Post post;

    @Column(nullable = false, columnDefinition = "NVARCHAR(MAX)")
    private String content;
}
