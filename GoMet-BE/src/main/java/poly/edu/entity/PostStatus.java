package poly.edu.entity;

/**
 * Lifecycle states for a Post in the moderation workflow.
 */
public enum PostStatus {
    /** Created by user but not yet reviewed. */
    PENDING_REVIEW,

    /** Reviewed and approved by admin — publicly visible. */
    APPROVED,

    /** Reviewed and rejected by admin — visible only to owner. */
    REJECTED,

    /** Previously approved, then hidden by admin — not publicly visible. */
    HIDDEN
}
