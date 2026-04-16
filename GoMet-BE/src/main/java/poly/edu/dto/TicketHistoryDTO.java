package poly.edu.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class TicketHistoryDTO {
    private Integer ticketID;
    private String title;
    private String content; // Frontend đang dùng biến này
    private String status;
    private LocalDateTime createdAt;
    private String adminNote; // 🔥 Thêm lời nhắn của Admin

    // 🔥 Đã đổi tham số thứ 3 thành description cho khớp với Database
    public TicketHistoryDTO(Integer ticketID, String title, String description, Integer statusCode, LocalDateTime createdAt, String adminNote) {
        this.ticketID = ticketID;
        this.title = title != null ? title : "Yêu cầu hỗ trợ";
        this.content = description; // Map description từ DB vào content cho FE
        this.createdAt = createdAt;
        this.adminNote = adminNote;

        // Chuyển đổi trạng thái số thành chữ
        if (statusCode == null) {
            this.status = "PENDING";
        } else if (statusCode == 0) {
            this.status = "PENDING";     // Chờ xử lý
        } else if (statusCode == 1) {
            this.status = "PROCESSING";  // Đang xử lý
        } else if (statusCode == 2) {
            this.status = "RESOLVED";    // Đã giải quyết
        } else if (statusCode == 3) {
            this.status = "REJECTED";    // Bị từ chối
        } else {
            this.status = "PENDING";
        }
    }
}