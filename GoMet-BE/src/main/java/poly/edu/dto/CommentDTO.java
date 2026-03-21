package poly.edu.dto;

import lombok.Data;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;

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

    // ĐỔI TÊN Ở ĐÂY: likeCount -> likes
    private Integer likes = 0;

    private Boolean isLiked = false;
    private LocalDateTime createdAt;

    /** Nested replies (threaded comments) */
    private List<CommentDTO> children = new ArrayList<>();
}