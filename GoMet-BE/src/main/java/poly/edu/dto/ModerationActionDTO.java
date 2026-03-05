package poly.edu.dto;

import lombok.Data;
import poly.edu.entity.ModerationActionType;

import java.time.Instant;

/**
 * Represents a single admin moderation action on a post — used for the
 * timeline / action-history view.
 */
@Data
public class ModerationActionDTO {
    private Long   id;
    private Integer postID;
    private String  postTitle;

    private Integer adminID;
    private String  adminName;
    private String  adminAvatar;

    private ModerationActionType action;
    private String               reason;
    private String               metaJson;
    private String               ip;
    private Instant              createdAt;
}
