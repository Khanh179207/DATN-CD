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
public class AdminNotificationSummaryDTO {
    private Integer notificationID;
    private String title;
    private String content;
    private String type;
    private LocalDateTime createdAt;
    private Boolean isGlobal;
    private String link;
    private Integer accountID;
    private String recipientUsername;
}
