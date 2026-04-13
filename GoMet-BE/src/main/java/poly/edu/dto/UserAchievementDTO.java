package poly.edu.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class UserAchievementDTO {
    private Integer     uaid;
    private Integer     achievementID;
    private String      achievementName;
    private String      description;
    private String      icon;
    private LocalDate   receivedAt;
}
