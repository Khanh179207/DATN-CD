package poly.edu.dto;

import lombok.Data;

@Data
public class AdminCommentDTO {
    private Integer commentID;
    private String content;

    private String username;   // từ Account
    private String postTitle;  // từ Post
}
