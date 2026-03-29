package poly.edu.dto;

import lombok.*;

/**
 * DTO for representing a user rewarded from an event
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RewardedUserDTO {
    private Integer accountID;
    private String username;
    private String avatar;
    private Integer rank; // 1, 2, or 3
    private Integer pointsRewarded; // 3, 2, or 1
    private Integer voteCount; // Vote count of their best post
    private Integer postID; // Best post ID
}
