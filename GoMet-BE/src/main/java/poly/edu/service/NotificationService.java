package poly.edu.service;

import poly.edu.entity.Notification;

public interface NotificationService {

    /**
     * Creates a new notification
     * 
     * @param title     The notification title
     * @param content   The notification content
     * @param type      The notification type
     * @param accountId The account ID of the recipient
     * @param postId    The post ID (optional, can be null)
     * @param link      The link URL (optional, can be null)
     * @return The created notification
     */
    Notification createNotification(String title, String content, String type, Integer accountId, Integer postId,
            String link);

    /**
     * Creates a follow notification when User A follows User B
     * 
     * @param followerUsername  The username of the follower (User A)
     * @param followedAccountId The account ID of the user being followed (User B)
     */
    void notifyFollow(String followerUsername, Integer followedAccountId);

    /**
     * Creates a comment notification when User A comments on a post
     * 
     * @param commenterUsername The username of the commenter (User A)
     * @param postOwnerId       The account ID of the post owner
     * @param postId            The post ID
     * @param commentId         The comment ID (optional, for linking to specific
     *                          comment)
     */
    void notifyComment(String commenterUsername, Integer postOwnerId, Integer postId, Integer commentId);

    /**
     * Creates a rating notification when User A rates a post
     * 
     * @param raterUsername The username of the rater (User A)
     * @param postOwnerId   The account ID of the post owner
     * @param postId        The post ID
     */
    void notifyRating(String raterUsername, Integer postOwnerId, Integer postId);

    /**
     * Creates an event vote notification when User A votes for a post in an event
     * 
     * @param voterUsername The username of the voter (User A)
     * @param postOwnerId   The account ID of the post owner
     * @param postId        The post ID
     */
    void notifyEventVote(String voterUsername, Integer postOwnerId, Integer postId);

    /**
     * Creates a ticket notification for admins when a user submits a support ticket
     * 
     * @param userUsername The username of the user who submitted the ticket
     * @param ticketId     The ticket ID
     */
    void notifyAdminTicket(String userUsername, Integer ticketId);

    /**
     * Creates a feedback notification for admins when a user submits feedback
     * 
     * @param userUsername The username of the user who submitted feedback
     * @param feedbackId   The feedback ID
     */
    void notifyAdminFeedback(String userUsername, Integer feedbackId);

    /**
     * Creates a report notification for admins when a user submits a report
     * 
     * @param userUsername The username of the user who submitted the report
     * @param reportId     The report ID
     */
    void notifyAdminReport(String userUsername, Integer reportId);

    /**
     * Creates a post pending approval notification for admins when a user creates a
     * post requiring approval
     * 
     * @param userUsername The username of the user who created the post
     * @param postId       The post ID
     */
    void notifyAdminPostPendingApproval(String userUsername, Integer postId);

    /**
     * Creates a notification for the post owner when their post is approved
     * 
     * @param postId The post ID that was approved
     */
    void notifyPostApproved(Integer postId);

    /**
     * Creates a notification for the post owner when their post is rejected
     * 
     * @param postId The post ID that was rejected
     * @param reason The reason for rejection (optional)
     */
    void notifyPostRejected(Integer postId, String reason);

    /**
     * Creates a notification for the post owner when their post is disabled
     * 
     * @param postId The post ID that was disabled
     */
    void notifyPostDisabled(Integer postId);
}