package poly.edu.dto;

import lombok.*;

import java.time.Instant;
import java.util.List;

/** Request DTO for creating an email job. */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailJobRequestDTO {
    private String       subject;
    private String       bodyHtml;
    private String       bodyText;
    /** User IDs to send to (looked up by account ID). */
    private List<Integer> recipientUserIds;
    /** External email addresses (not necessarily users). */
    private List<String>  recipientEmails;
}
