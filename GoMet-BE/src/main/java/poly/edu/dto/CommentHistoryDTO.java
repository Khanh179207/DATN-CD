package poly.edu.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List; // 🔥 Import thêm cái này

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentHistoryDTO {
    private Integer commentID;
    private String content;
    private LocalDateTime createdAt;
    private String postTitle;
    private Integer postID;
    private List<String> attachments; // 🔥 THÊM TRƯỜNG NÀY
}