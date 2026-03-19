package poly.edu.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TicketDTO {
    // ID người gửi (FE lấy từ AuthStore)
    private Integer accountId;

    // Loại: BUG, REPORT, FEEDBACK
    private String ticketType;

    // Tiêu đề ngắn gọn
    private String title;

    // Nội dung chi tiết
    private String description;

    // Link ảnh bằng chứng (Link HTTPS xịn từ Cloudinary FE gửi về)
    private String attachment;

    // ID bài viết bị báo cáo (Nếu ticketType là REPORT)
    private Integer targetPostId;
}