package poly.edu.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "Favorite",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"AccountID","PostID"})
        }
)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Favorite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer favoriteID;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "AccountID", nullable = false)
    private Account account;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PostID", nullable = false)
    private Post post;
}
