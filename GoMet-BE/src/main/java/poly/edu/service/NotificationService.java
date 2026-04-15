package poly.edu.service;

import poly.edu.entity.Notification;

public interface NotificationService {

    
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
}