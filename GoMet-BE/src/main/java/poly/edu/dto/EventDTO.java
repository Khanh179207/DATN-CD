package poly.edu.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class EventDTO {
    private Integer eventID;
    private String eventName;
    private Integer winner;
    private LocalDate startAt;
    private LocalDate endAt;
}
