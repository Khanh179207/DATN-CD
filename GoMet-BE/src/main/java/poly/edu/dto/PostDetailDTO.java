package poly.edu.dto;

import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class PostDetailDTO {
    private Integer postID;
    private String title;
    private String description;
    private String ingredients;
    private String media;
    private String video;
    private Integer level;
    private Integer cookingTime;
    private Integer views;
    private LocalDate createdAt;

    // Author
    private Integer authorID;
    private String authorName;
    private String authorAvatar;
    private Long authorPostCount;
    private Long authorFollowerCount;
    private String authorBio;

    // Category
    private Integer categoryID;
    private String categoryName;

    // Event
    private Integer eventID;
    private String eventName;

    // Aggregates
    private Double avgRating;
    private Long ratingCount;
    private Long commentCount;
    private Long favoriteCount;

    // Steps
    private List<CookingStepDTO> steps;

    // Comments
    private List<CommentDTO> comments;
    // Thêm vào trong file PostDetailDTO.java
    private Boolean isLiked;
}
