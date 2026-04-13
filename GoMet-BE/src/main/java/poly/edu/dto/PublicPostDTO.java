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

    // Category
    private Integer categoryID;
    private String categoryName;

    // Aggregates
    private Double avgRating;
    private Long ratingCount;
    private Long commentCount;
    private Long favoriteCount;
    // Thêm vào trong file PublicPostDTO.java
    private Boolean isLiked; // Nhớ dùng Boolean (chữ B hoa) sếp nhé
}
