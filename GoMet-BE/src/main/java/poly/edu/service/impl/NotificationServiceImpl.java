package poly.edu.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import poly.edu.dao.AccountDAO;
import poly.edu.dao.NotificationDAO;
import poly.edu.dao.PostDAO;
import poly.edu.dto.NotificationDTO;
import poly.edu.entity.Account;
import poly.edu.entity.Notification;
import poly.edu.entity.Post;
import poly.edu.service.NotificationService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationDAO notificationDAO;
    private final AccountDAO accountDAO;
    private final PostDAO postDAO;
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public Notification createNotification(String title, String content, String type, Integer accountId,
            Integer postId, String link) {
        // Get the account entity
        Optional<Account> accountOpt = accountDAO.findById(accountId);
        if (accountOpt.isEmpty()) {
            throw new RuntimeException("Account not found with ID: " + accountId);
        }

        // Get the post entity if postId is provided
        Post post = null;
        if (postId != null) {
            Optional<Post> postOpt = postDAO.findById(postId);
            if (postOpt.isEmpty()) {
                throw new RuntimeException("Post not found with ID: " + postId);
            }
            post = postOpt.get();
        }

        // Create the notification
        Notification notification = Notification.builder()
                .title(title)
                .content(content)
                .type(type)
                .account(accountOpt.get())
                .post(post)
                .isRead(0) // Default to unread
                .createdAt(LocalDateTime.now())
                .link(link)
                .build();

        Notification savedNotification = notificationDAO.save(notification);

        // Send real-time notification via WebSocket
        sendRealtimeNotification(accountId, convertToDTO(savedNotification));

        return savedNotification;
    }

    @Override
    public void notifyFollow(String followerUsername, Integer followedAccountId) {
        String title = "New Follower";
        String content = followerUsername + " started following you.";
        String type = "FOLLOW";
        String link = "/profile/" + followedAccountId; // Link to the followed user's profile

        createNotification(title, content, type, followedAccountId, null, link);
    }

    @Override
    public void notifyComment(String commenterUsername, Integer postOwnerId, Integer postId, Integer commentId) {
        // Don't create notification if commenter is the post owner
        // This check should be done by the caller, but we'll add it here as well
        // For now, assuming the caller handles this

        String title = "New Comment";
        String content = commenterUsername + " commented on your post.";
        String type = "COMMENT";
        String link = commentId != null ? "/post/" + postId + "#comment-" + commentId : "/post/" + postId;

        createNotification(title, content, type, postOwnerId, postId, link);
    }

    @Override
    public void notifyRating(String raterUsername, Integer postOwnerId, Integer postId) {
        // Don't create notification if rater is the post owner
        // This check should be done by the caller, but we'll add it here as well
        // For now, assuming the caller handles this

        String title = "New Rating";
        String content = raterUsername + " rated your post.";
        String type = "RATING";
        String link = "/post/" + postId; // Link to the post

        createNotification(title, content, type, postOwnerId, postId, link);
    }

    @Override
    public void notifyEventVote(String voterUsername, Integer postOwnerId, Integer postId) {
        // Don't create notification if voter is the post owner
        // This check should be done by the caller, but we'll add it here as well
        // For now, assuming the caller handles this

        String title = "New Vote";
        String content = voterUsername + " voted for your post in the event.";
        String type = "EVENT_VOTE";
        String link = "/post/" + postId; // Link to the post

        createNotification(title, content, type, postOwnerId, postId, link);
    }

    @Override
    public void notifyAdminTicket(String userUsername, Integer ticketId) {
        // Notify all admin accounts
        List<Account> admins = accountDAO.findByIsAdmin(1);
        for (Account admin : admins) {
            String title = "New Support Ticket";
            String content = userUsername + " submitted a support ticket.";
            String type = "TICKET";
            String link = "/admin/tickets"; // Link to admin tickets page
            Notification notification = createNotification(title, content, type, admin.getAccountID(), null, link);
            sendAdminAlert(notification);
        }
    }

    @Override
    public void notifyAdminFeedback(String userUsername, Integer feedbackId) {
        // Notify all admin accounts
        List<Account> admins = accountDAO.findByIsAdmin(1);
        for (Account admin : admins) {
            String title = "New Feedback";
            String content = userUsername + " submitted feedback.";
            String type = "FEEDBACK";
            String link = "/admin/feedback"; // Link to admin feedback page
            Notification notification = createNotification(title, content, type, admin.getAccountID(), null, link);
            sendAdminAlert(notification);
        }
    }

    @Override
    public void notifyAdminReport(String userUsername, Integer reportId) {
        // Notify all admin accounts
        List<Account> admins = accountDAO.findByIsAdmin(1);
        for (Account admin : admins) {
            String title = "New Report";
            String content = userUsername + " submitted a report.";
            String type = "REPORT";
            String link = "/admin/reports"; // Link to admin reports page
            Notification notification = createNotification(title, content, type, admin.getAccountID(), null, link);
            sendAdminAlert(notification);
        }
    }

    @Override
    public void notifyAdminPostPendingApproval(String userUsername, Integer postId) {
        // Notify all admin accounts
        List<Account> admins = accountDAO.findByIsAdmin(1);
        for (Account admin : admins) {
            String title = "Post Pending Approval";
            String content = userUsername + " created a new post that requires approval.";
            String type = "POST_PENDING_APPROVAL";
            String link = "/admin/posts/pending"; // Link to admin pending posts page
            Notification notification = createNotification(title, content, type, admin.getAccountID(), postId, link);
            sendAdminAlert(notification);
        }
    }

    @Override
    public void notifyPostApproved(Integer postId) {
        // Get the post and its owner
        Optional<Post> postOpt = postDAO.findById(postId);
        if (postOpt.isEmpty()) {
            throw new RuntimeException("Post not found with ID: " + postId);
        }

        Post post = postOpt.get();
        Account postOwner = post.getAccount();

        String title = "Post approved";
        String content = "Your post has been approved by the admin.";
        String type = "POST_APPROVED";
        String link = "/post/" + postId; // Link to the approved post

        createNotification(title, content, type, postOwner.getAccountID(), postId, link);
    }

    @Override
    public void notifyPostRejected(Integer postId, String reason) {
        // Get the post and its owner
        Optional<Post> postOpt = postDAO.findById(postId);
        if (postOpt.isEmpty()) {
            throw new RuntimeException("Post not found with ID: " + postId);
        }

        Post post = postOpt.get();
        Account postOwner = post.getAccount();

        String title = "Post rejected";
        String content = reason != null && !reason.trim().isEmpty()
                ? "Your post was rejected by the admin. Reason: " + reason
                : "Your post was rejected by the admin.";
        String type = "POST_REJECTED";
        String link = "/post/" + postId; // Link to the rejected post

        createNotification(title, content, type, postOwner.getAccountID(), postId, link);
    }

    @Override
    public void notifyPostDisabled(Integer postId) {
        // Get the post and its owner
        Optional<Post> postOpt = postDAO.findById(postId);
        if (postOpt.isEmpty()) {
            throw new RuntimeException("Post not found with ID: " + postId);
        }

        Post post = postOpt.get();
        Account postOwner = post.getAccount();

        String title = "Post disabled";
        String content = "Your post has been disabled by admin.";
        String type = "POST_DISABLED";
        String link = "/post/" + postId; // Link to the disabled post

        createNotification(title, content, type, postOwner.getAccountID(), postId, link);
    }

    /**
     * Send a real-time notification to a specific user via WebSocket
     * 
     * @param accountId       The account ID of the recipient
     * @param notificationDTO The notification data to send
     */
    private void sendRealtimeNotification(Integer accountId, NotificationDTO notificationDTO) {
        try {
            // Send to the specific user using their account ID as the user identifier
            messagingTemplate.convertAndSendToUser(
                    accountId.toString(),
                    "/queue/notifications",
                    notificationDTO);
        } catch (Exception e) {
            // Log the error but don't fail the notification creation
            System.err.println("Failed to send real-time notification: " + e.getMessage());
        }
    }

    /**
     * Send a real-time admin alert to all connected admin users via WebSocket
     * 
     * @param notification The notification entity to send
     */
    private void sendAdminAlert(Notification notification) {
        try {
            // Send to the admin alerts topic
            messagingTemplate.convertAndSend(
                    "/topic/admin-alerts",
                    convertToDTO(notification));
        } catch (Exception e) {
            // Log the error but don't fail the notification creation
            System.err.println("Failed to send admin alert: " + e.getMessage());
        }
    }

    /**
     * Convert a Notification entity to NotificationDTO
     * 
     * @param notification The notification entity
     * @return The notification DTO
     */
    private NotificationDTO convertToDTO(Notification notification) {
        return NotificationDTO.builder()
                .notificationId(notification.getNotificationID())
                .title(notification.getTitle())
                .content(notification.getContent())
                .type(notification.getType())
                .postId(notification.getPost() != null ? notification.getPost().getPostID() : null)
                .link(notification.getLink())
                .createdAt(notification.getCreatedAt())
                .isRead(notification.getIsRead())
                .build();
    }
}