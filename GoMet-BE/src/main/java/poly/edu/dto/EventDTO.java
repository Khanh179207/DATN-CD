package poly.edu.dto;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class EventDTO {
    private Integer eventID;
    private String eventName;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private String bannerImage; // Cho Slider Vue nó húp
    private Integer winnerPostID;
    private Long postCount; // Đổi tên cho khớp logic nộp bài
    private String status;
    private LocalDateTime voteStartAt;
    private LocalDateTime voteEndAt;
    private String description;
    private String rules;
    private String reward;
}