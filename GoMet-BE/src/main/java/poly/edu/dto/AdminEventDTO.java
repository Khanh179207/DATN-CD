package poly.edu.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminEventDTO {
    private Integer eventID;
    private String eventName;
    private Integer winner;
    private String bannerImage;
    private Integer postCount;
    private Integer totalVotes;
    private String description;
    private String rules;
    private String reward;
    private Integer maxVotes;

    // 🔥 Đổi hết sang String để nhận dữ liệu an toàn 100%
    private String startAt;
    private String endAt;
    private String voteStartAt;
    private String voteEndAt;
}