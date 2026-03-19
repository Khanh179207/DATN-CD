package poly.edu.dto;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class UserProfileDTO {
    private Integer accountID;
    private String username;
    private String email;
    private String avatar;
    private String bio;
    private Integer point;
    private Integer isPremium;
    private Integer isAdmin;
    private LocalDateTime createdAt;
    private Long postCount;
    private Long followerCount;
    private Long followingCount;
    private Long totalViews;
    private Double avgRating;
}
