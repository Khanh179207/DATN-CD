package poly.edu.dto;

import lombok.Data;

/**
 * Request body for admin moderation actions (approve / reject / hide / unhide / flag-spam).
 */
@Data
public class ModerationRequestDTO {
    /** Optional human-readable reason for all action types. */
    private String reason;

    /** Short code for REJECT action (e.g. SPAM, INAPPROPRIATE, OFF_TOPIC, DUPLICATE). */
    private String code;
}
