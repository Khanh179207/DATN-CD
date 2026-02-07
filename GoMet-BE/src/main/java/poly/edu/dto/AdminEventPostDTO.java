package poly.edu.dto;

import lombok.Data;

@Data
public class AdminEventPostDTO {
    private Integer eventPostID;
    private Integer postID;
    private String postTitle;
    private String username;
}
