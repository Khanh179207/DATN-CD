package poly.edu.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor // 🔥 Bây giờ nó sẽ tự tạo hộp 8 ngăn chuẩn 100%
public class EventPostDTO {
    private Integer eventPostID;
    private Integer postID;
    private String postTitle;
    private String postImage;
    private String authorName;
    private String authorAvatar;
    private Integer voteCount;
    private LocalDateTime createdAt;
}