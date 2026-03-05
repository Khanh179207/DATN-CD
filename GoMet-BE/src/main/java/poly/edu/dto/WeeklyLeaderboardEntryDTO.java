package poly.edu.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeeklyLeaderboardEntryDTO {
    private Integer userId;
    private String username;
    private String avatar;
    private Integer score;
    private Integer rank;
}
