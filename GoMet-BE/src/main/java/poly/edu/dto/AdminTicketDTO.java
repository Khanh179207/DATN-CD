package poly.edu.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AdminTicketDTO {
    private Integer ticketID;
    private Integer accountID;
    private String username;
    private String email;

    private String ticketType;
    private Integer targetPostId;

    private String title;
    private String description;
    private String attachment;

    private Integer status;

    private LocalDateTime createdAt;
    private LocalDateTime resolvedAt;
    private LocalDateTime processedAt;

    // 🔥 MỚI THÊM: Để hiển thị "chính chủ" Admin xử lý (ID 7)
    private Integer adminId;
    private String adminName;
    private String adminNote;
}