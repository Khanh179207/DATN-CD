package poly.edu.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AdminTicketDTO {
    private Integer ticketID;
    private Integer accountID;
    private String username;
    private String email;

    private String ticketType; // Lỗi, Báo cáo, Góp ý
    private Integer targetPostId; // Bài viết bị báo cáo (nếu có)

    private String title;
    private String description;
    private String attachment;

    private Integer status; // 0: Pending, 1: Processing, 2: Resolved, 3: Rejected

    private LocalDateTime createdAt;
    private LocalDateTime resolvedAt;
}