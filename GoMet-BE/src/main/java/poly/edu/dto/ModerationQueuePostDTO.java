package poly.edu.dto;

import lombok.Data;
import poly.edu.entity.PostStatus;

import java.time.Instant;
import java.time.LocalDate;

/**
 * Summary DTO for the admin moderation inbox queue.
 * Contains everything needed to make a moderation decision at a glance.
 */
@Data
public class ModerationQueuePostDTO {
    private Integer postID;
    private String  title;
    private String  description;
    private String  media;
    private Integer level;
    private Integer cookingTime;

    private Integer authorID;
    private String  authorName;
    private String  authorAvatar;
    private long    authorApprovedPostCount;

    private Integer categoryID;
    private String  categoryName;

    private PostStatus status;
    private Integer    spamScore;
    private String     spamReasons;

    private LocalDate createdAt;
    private Instant   moderatedAt;
    private String    moderatedByName;
    private String    rejectionCode;
    private String    rejectionReason;
}
