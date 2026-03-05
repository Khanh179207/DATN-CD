package poly.edu.dto;

import lombok.Data;
import poly.edu.entity.PostStatus;

import java.time.LocalDate;

/**
 * DTO returned to a post owner viewing their own posts (all statuses).
 * NOTE: rejectionReason is only returned to the owner — never to public endpoints.
 */
@Data
public class MyPostDTO {
    private Integer    postID;
    private String     title;
    private String     description;
    private String     media;
    private Integer    level;
    private Integer    cookingTime;
    private Integer    views;
    private LocalDate  createdAt;

    private Integer    categoryID;
    private String     categoryName;

    private PostStatus status;
    private String     rejectionReason;
    private String     rejectionCode;
    private Integer    spamScore;

    // Aggregates
    private Double  avgRating;
    private Long    ratingCount;
    private Long    commentCount;
    private Long    favoriteCount;
}
