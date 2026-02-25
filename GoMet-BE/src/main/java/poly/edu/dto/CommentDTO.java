package poly.edu.dto;

import lombok.Data;

@Data
public class CommentDTO {
    private Integer commentID;
    private Integer postID;
    private Integer accountID;
    private String authorName;
    private String authorAvatar;
    private String content;
    private Integer rating;
}
