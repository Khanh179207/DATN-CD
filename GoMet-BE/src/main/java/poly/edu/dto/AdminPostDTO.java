package poly.edu.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AdminPostDTO {
    private Integer postID;
    private String title;
    private String description;
    private String media;
    private Integer level;
    private Integer cookingTime;
    private Integer views;
    private Integer isActive;
    private Integer isApproved;

    private String username; // from Account
    private Integer accountID;
    private String accountAvatar;
    private String categoryName; // from Category
    private Integer categoryID;
    private LocalDateTime createdAt;
}
