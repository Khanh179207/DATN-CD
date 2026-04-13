package poly.edu.dto;

import lombok.Data;

@Data
public class LeaderboardUserDTO {
    private Integer accountID;
    private String username;
    private String avatar;
    private Integer point;
    private Long postCount;
    private Long followerCount;
    private Integer rank;
}
