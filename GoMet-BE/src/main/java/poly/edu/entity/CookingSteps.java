package poly.edu.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "CookingSteps")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CookingSteps {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer stepID;

    @ManyToOne
    @JoinColumn(name = "PostID", nullable = false)
    private Post post;

    @Column(nullable = false)
    private Integer stepNumber;

    @Column(nullable = false, columnDefinition = "NVARCHAR(MAX)")
    private String content;

    private String image;
    private String video;
}
