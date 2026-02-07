package poly.edu.dto;

import lombok.Data;

@Data
public class EventPostDTO {
    private Integer eventPostID;
    private Integer postID;
    private String postTitle;
    private String username;
}
