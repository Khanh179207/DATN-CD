package poly.edu.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminNotificationDetailDTO {
    private Integer notificationID;
    private String title;
    private String content;
    private String type;
    private LocalDateTime createdAt;
    private Boolean isGlobal;
    private String link;
    private Integer accountID;
    private String recipientUsername;
    private String recipientEmail;
    private String recipientAvatar;
    private Integer readCount;
    private List<AdminNotificationReaderDTO> readers;
}
