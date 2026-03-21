package poly.edu.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDTO {
    private Integer notificationId;
    private String title;
    private String content;
    private String type;
    private Integer postId;
    private String link;
    private LocalDateTime createdAt;
    private Integer isRead;
}