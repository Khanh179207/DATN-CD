package poly.edu.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Lightweight projection for the admin report list.
 * Uses a JPQL constructor query to avoid loading the full Post
 * and Account object graphs (prevents N+1 queries on Post's collections).
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReportSummaryDTO {

    private Integer reportID;

    /** Display name of the user who filed the report. */
    private String reporterName;

    /** ID of the reported post. */
    private Integer postID;

    /** Title of the reported post. */
    private String postTitle;

    private String reason;

    private LocalDate createdAt;
}
