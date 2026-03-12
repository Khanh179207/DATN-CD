package poly.edu.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class AdminNotificationDTO {
    private Integer notificationID;
    private String title;
    private String content;
    private String type;
    private Integer accountID;
    private String accountName;
    private Integer postID; // có thể null
    private Integer isRead;
    private LocalDate readAt;
    private LocalDate createdAt;
}