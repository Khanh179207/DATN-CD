package poly.edu.dto;

import lombok.Data;

@Data
public class AdminNotificationDTO {
    private String title;
    private String content;
    private String type;
    private Integer postID; // có thể null
}