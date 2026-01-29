package poly.edu.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "Achievement")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Achievement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer achievementID;

    @Column(nullable = false)
    private String achievementName;

    @Column(nullable = false, columnDefinition = "NVARCHAR(MAX)")
    private String description;

    private String icon;

    //Relationships

    @OneToMany(mappedBy = "achievement")
    private List<UserAchievement> userAchievements;

}
