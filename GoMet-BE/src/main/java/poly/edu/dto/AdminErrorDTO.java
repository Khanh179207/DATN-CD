package poly.edu.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;

@Data
@Builder
public class AdminErrorDTO {
    private Integer errorID;
    private String errorName;
    private String description;
    private LocalDate createdAt;
    private String username;
}
