package poly.edu.dto;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class PublicPostDTO {
    private Integer postID;
    private String title;
    private String description;
    private String ingredients;
    private String media;
    private String video;
    private Integer level;
    private Integer cookingTime;
    private Integer views;
    private LocalDateTime createdAt;

    // Author
    private Integer authorID;
    private String authorName;
    private String authorAvatar;
    private Boolean isPremium;

    // Category
    private Integer categoryID;
    private String categoryName;

    // Aggregates
    private Double avgRating;
    private Long ratingCount;
    private Long commentCount;
    private Long favoriteCount;
    private Boolean isLiked; 
    private Integer isActive;   // 🔥 Trạng thái ẩn/hiện
    private Integer isApproved; // 🔥 Trạng thái duyệt
}
