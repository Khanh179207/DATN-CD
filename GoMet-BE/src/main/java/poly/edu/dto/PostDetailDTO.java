package poly.edu.dto;

import lombok.Data;
import java.time.LocalDateTime; // 🔥 Đã đổi từ LocalDate sang LocalDateTime
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
    private LocalDateTime createdAt; // 🔥 Đã cập nhật kiểu dữ liệu

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

    // Trạng thái Like của người dùng hiện tại
    private Boolean isLiked;
}