package poly.edu.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminEventPostDTO {
    private Integer eventPostID;
    private Integer postID;
    private String postTitle;
    private String postImage;
    private String username;

    // 🔥 Thêm trường này vào để fix lỗi setVoteCount sếp nhé!
    private Integer voteCount;
}