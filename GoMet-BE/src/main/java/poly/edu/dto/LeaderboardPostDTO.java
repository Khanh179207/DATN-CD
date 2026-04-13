package poly.edu.dto;

import lombok.Data;

@Data
public class LeaderboardPostDTO {
    private Integer postID;
    private String title;
    private String media;
    private Integer views;
    private Double avgRating;
    private Long ratingCount;
    private Long favoriteCount;
    private Double score; // combined metric

    // Author
    private Integer authorID;
    private String authorName;
    private String authorAvatar;

    // Category
    private String categoryName;
    private Integer rank;
}
