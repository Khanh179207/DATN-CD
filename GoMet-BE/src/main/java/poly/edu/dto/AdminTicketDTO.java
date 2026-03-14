package poly.edu.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AdminTicketDTO {
    private Integer ticketID;
    private Integer accountID;
    private String username;
    private String email;
    private String title;
    private String description;
    private String attachment;
    private Boolean isRead;
    private LocalDateTime createdAt;
}