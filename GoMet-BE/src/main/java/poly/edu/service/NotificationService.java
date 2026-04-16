package poly.edu.service;

import poly.edu.entity.Notification;

public interface NotificationService {

    /**
     * Creates a new notification
     * 
     * @param title      The notification title
     * @param content    The notification content
     * @param type       The notification type
     * @param receiverId The account ID of the recipient (receiver)
     * @param actorId    The account ID of the person who triggered the action
     *                   (actor)
     * @param postId     The post ID (optional, can be null)
     * @param link       The link URL (optional, can be null)
     * @return The created notification
     */
    Notification createNotification(String title, String content, String type, Integer receiverId, Integer actorId,
            Integer postId, String link);

    void notifyFollow(String followerUsername, Integer followedAccountId);

    void notifyComment(String commenterUsername, Integer postOwnerId, Integer postId, Integer commentId);

    void notifyRating(String raterUsername, Integer postOwnerId, Integer postId);

    void notifyEventVote(String voterUsername, Integer postOwnerId, Integer postId);

    void notifyAdminTicket(String userUsername, Integer ticketId);

    void notifyAdminFeedback(String userUsername, Integer feedbackId);

    void notifyAdminReport(String userUsername, Integer reportId);

    void notifyAdminPostPendingApproval(String userUsername, Integer postId);

    void notifyPostApproved(Integer postId);

    void notifyPostRejected(Integer postId, String reason);

    void notifyPostDisabled(Integer postId);

    void notifyLike(String likerUsername, Integer postOwnerId, Integer postId);

    void notifyCommentReply(String replierUsername, Integer parentCommentAuthorId, Integer postId, Integer commentId);

    void notifyCommentLike(String likerUsername, Integer commentAuthorId, Integer postId, Integer commentId);

    void notifyMention(String mentionerUsername, Integer mentionedAccountId, Integer postId, Integer commentId);

    void notifyTicketUpdate(Integer ticketId, Integer newStatus, Integer accountId);

    void notifyAppealUpdate(Integer appealId, String status, Integer accountId);

    void notifyCommentStatusChange(Integer commentId, Integer accountId, String action);

    void notifyReward(Integer accountId, Integer points, Integer premiumDays, String source);

    void notifyPaymentStatus(Integer accountId, boolean isSuccess, String orderCode);

    void notifyEventWinner(Integer accountId, Integer eventId, Integer rank);

    void notifyAccountStatus(Integer accountId, String status, String reason);
}