package poly.edu.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime; // 🔥 Phải là LocalDateTime sếp ơi

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventPostDTO {
    private Integer eventPostID;
    private Integer postID;
    private String postTitle;
    private String postImage;
    private String authorName;
    private String authorAvatar;
    private Integer voteCount;
    private LocalDateTime createdAt; // 🔥 Khớp với Entity
}