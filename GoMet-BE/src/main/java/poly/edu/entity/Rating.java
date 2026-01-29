package poly.edu.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Rating")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Rating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer ratingID;

    @ManyToOne
    @JoinColumn(name = "AccountID", nullable = false)
    private Account account;

    @ManyToOne
    @JoinColumn(name = "PostID", nullable = false)
    private Post post;

    @Column(nullable = false)
    private Integer rate;
}
