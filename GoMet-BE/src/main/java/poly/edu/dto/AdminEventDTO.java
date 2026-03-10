package poly.edu.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminEventDTO {
    private Integer eventID;
    private String eventName;
    private Integer winner;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private LocalDateTime voteStartAt;
    private LocalDateTime voteEndAt;
    private String bannerImage;

    // 🔥 Các trường thống kê thực tế từ Database
    private Integer postCount;  // Tổng số bài đã nộp
    private Integer totalVotes; // Tổng số phiếu bầu của toàn bộ bài dự thi
    // Thêm 3 dòng này vào AdminEventDTO.java (Và cả EventDetailDTO nếu sếp có)
    private String description;
    private String rules;
    private String reward;
    private Integer maxVotes;
// Sếp nhớ kiểm tra xem file DTO đã có @Data (Lombok) chưa nhé,
// nếu chưa thì phải generate Getter/Setter cho 3 thằng này.
}