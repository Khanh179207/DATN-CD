package poly.edu.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class AdminCommentDTO {
    private Integer commentID;
    private String content;
    private LocalDateTime createdAt;

    // 🔥 Các trường khai thác triệt để từ SQL
    private Integer rating;
    private Integer likes;
    private Boolean hasAttachments;
    private Integer parentCommentID; // cmtid (Nếu != null tức là Reply)

    // Thông tin người viết
    private Integer authorID;
    private String authorName;
    private String authorAvatar;
    private List<String> imageUrls;

    // Thông tin bài viết
    private Integer postID;
    private String postTitle;
    private Integer isActive;
}