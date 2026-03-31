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
    private String bannerImage;
    private Integer winnerPostID;
    private Long postCount;
    private String status;
    private LocalDateTime voteStartAt;
    private LocalDateTime voteEndAt;
    private String description;
    private String reward;
    private Integer isActive;
    private Integer isForceEnded;
}