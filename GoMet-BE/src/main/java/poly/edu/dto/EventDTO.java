package poly.edu.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EventDTO {
    private Integer eventID;
    private String eventName;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private LocalDateTime voteStartAt;
    private LocalDateTime voteEndAt;
    private String bannerImage;
    private Integer winnerPostID;
    private String winnerPostTitle;
    private Long participantCount;
    private Long postCount;
    private String description;
    private String rules;
    private String reward;
    private String status;
}
