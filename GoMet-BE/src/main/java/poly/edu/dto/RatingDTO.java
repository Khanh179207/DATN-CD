package poly.edu.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class RatingDTO {
    private Integer ratingID;
    private Integer postID;
    private Integer accountID;
    private String authorName;
    private String authorAvatar;
    private Integer rate;
    private String comment;
    private List<String> imageUrls;
    private LocalDateTime createdAt;
}
