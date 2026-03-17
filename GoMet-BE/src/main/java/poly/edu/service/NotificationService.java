package poly.edu.service;

import poly.edu.entity.Notification;

public interface NotificationService {

    /**
     * Creates a new notification
     * @param title The notification title
     * @param content The notification content
     * @param type The notification type
     * @param accountId The account ID of the recipient
     * @param postId The post ID (optional, can be null)
     * @return The created notification
     */
    Notification createNotification(String title, String content, String type, Integer accountId, Integer postId);

    /**
     * Creates a follow notification when User A follows User B
     * @param followerUsername The username of the follower (User A)
     * @param followedAccountId The account ID of the user being followed (User B)
     */
    void notifyFollow(String followerUsername, Integer followedAccountId);

    /**
     * Creates a comment notification when User A comments on a post
     * @param commenterUsername The username of the commenter (User A)
     * @param postOwnerId The account ID of the post owner
     * @param postId The post ID
     */
    void notifyComment(String commenterUsername, Integer postOwnerId, Integer postId);

    /**
     * Creates a rating notification when User A rates a post
     * @param raterUsername The username of the rater (User A)
     * @param postOwnerId The account ID of the post owner
     * @param postId The post ID
     */
    void notifyRating(String raterUsername, Integer postOwnerId, Integer postId);

    /**
     * Creates an event vote notification when User A votes for a post in an event
     * @param voterUsername The username of the voter (User A)
     * @param postOwnerId The account ID of the post owner
     * @param postId The post ID
     */
    void notifyEventVote(String voterUsername, Integer postOwnerId, Integer postId);
}