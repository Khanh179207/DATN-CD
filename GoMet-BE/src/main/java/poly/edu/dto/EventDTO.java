package poly.edu.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class EventDTO {
    private Integer eventID;
    private String eventName;
    private LocalDate startAt;
    private LocalDate endAt;
    private Integer winnerPostID;
    private String winnerPostTitle;
    private Long participantCount;
    private String status; // "upcoming" | "active" | "ended"
}
