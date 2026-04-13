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
    private Integer voteCount;

    // Thêm trường accountID để show ra ai là người chiến thắng
    private Integer accountID;
}