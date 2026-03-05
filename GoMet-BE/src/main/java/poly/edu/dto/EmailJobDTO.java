package poly.edu.dto;

import lombok.*;
import poly.edu.entity.EmailJob;
import poly.edu.entity.EmailJobRecipient;

import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailJobDTO {
    private Long    id;
    private String  senderAdminName;
    private Integer senderAdminId;
    private String  subject;
    private String  bodyHtml;
    private String  bodyText;
    private String  status;
    private String  failReason;
    private int     recipientCount;
    private int     sentCount;
    private int     failedCount;
    private Instant createdAt;
    private Instant queuedAt;
    private Instant sentAt;

    /** Only populated for detail view */
    private List<RecipientDTO> recipients;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RecipientDTO {
        private Long    id;
        private Integer userId;
        private String  email;
        private String  username;
        private String  status;
        private String  failReason;
        private Instant sentAt;
    }

    public static EmailJobDTO fromEntity(EmailJob job) {
        return EmailJobDTO.builder()
            .id(job.getId())
            .senderAdminId(job.getSenderAdminId())
            .subject(job.getSubject())
            .bodyHtml(job.getBodyHtml())
            .bodyText(job.getBodyText())
            .status(job.getStatus())
            .failReason(job.getFailReason())
            .recipientCount(job.getRecipientCount())
            .sentCount(job.getSentCount())
            .failedCount(job.getFailedCount())
            .createdAt(job.getCreatedAt())
            .queuedAt(job.getQueuedAt())
            .sentAt(job.getSentAt())
            .build();
    }

    public static RecipientDTO fromRecipient(EmailJobRecipient r) {
        return RecipientDTO.builder()
            .id(r.getId())
            .userId(r.getUserId())
            .email(r.getEmail())
            .status(r.getStatus())
            .failReason(r.getFailReason())
            .sentAt(r.getSentAt())
            .build();
    }
}
