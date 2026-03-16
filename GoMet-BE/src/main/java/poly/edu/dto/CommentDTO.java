package poly.edu.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CommentDTO {
    private Integer commentID;
    private Integer postID;
    private Integer cmtid;
    private Integer accountID;
    private String authorName;
    private String authorAvatar;
    private String content;
    private List<String> imageUrls = new ArrayList<>();
    private Integer rating;
    private Integer likeCount = 0;
    private Boolean isLiked = false;
    private java.time.LocalDateTime createdAt;

    /** Nested replies (threaded comments) */
    private List<CommentDTO> children = new ArrayList<>();
}
