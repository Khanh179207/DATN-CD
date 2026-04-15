package poly.edu.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminNotificationReaderDTO {
    private Integer accountID;
    private String username;
    private String email;
    private String avatar;
    private LocalDateTime readAt;
}
