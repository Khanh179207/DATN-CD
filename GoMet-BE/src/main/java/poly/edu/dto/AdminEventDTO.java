package poly.edu.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminEventDTO {
    private Integer eventID;
    private String eventName;
    private Integer winner;
    private String bannerImage;
    private Integer postCount;
    private Integer totalVotes;
    private String description;
    private String reward;
    private Integer maxVotes;
    private Integer isActive;
    private Integer isForceEnded;
    private String startAt;
    private String endAt;
    private String voteStartAt;
    private String voteEndAt;
}