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
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationDAO notificationDAO;
    private final AccountDAO accountDAO;
    private final PostDAO postDAO;
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public Notification createNotification(String title, String content, String type, Integer accountId, Integer postId) {
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

        createNotification(title, content, type, followedAccountId, null);
    }

    @Override
    public void notifyComment(String commenterUsername, Integer postOwnerId, Integer postId) {
        // Don't create notification if commenter is the post owner
        // This check should be done by the caller, but we'll add it here as well
        // For now, assuming the caller handles this

        String title = "New Comment";
        String content = commenterUsername + " commented on your post.";
        String type = "COMMENT";

        createNotification(title, content, type, postOwnerId, postId);
    }

    @Override
    public void notifyRating(String raterUsername, Integer postOwnerId, Integer postId) {
        // Don't create notification if rater is the post owner
        // This check should be done by the caller, but we'll add it here as well
        // For now, assuming the caller handles this

        String title = "New Rating";
        String content = raterUsername + " rated your post.";
        String type = "RATING";

        createNotification(title, content, type, postOwnerId, postId);
    }

    @Override
    public void notifyEventVote(String voterUsername, Integer postOwnerId, Integer postId) {
        // Don't create notification if voter is the post owner
        // This check should be done by the caller, but we'll add it here as well
        // For now, assuming the caller handles this

        String title = "New Vote";
        String content = voterUsername + " voted for your post in the event.";
        String type = "EVENT_VOTE";

        createNotification(title, content, type, postOwnerId, postId);
    }

    /**
     * Send a real-time notification to a specific user via WebSocket
     * @param accountId The account ID of the recipient
     * @param notificationDTO The notification data to send
     */
    private void sendRealtimeNotification(Integer accountId, NotificationDTO notificationDTO) {
        try {
            // Send to the specific user using their account ID as the user identifier
            messagingTemplate.convertAndSendToUser(
                accountId.toString(),
                "/queue/notifications",
                notificationDTO
            );
        } catch (Exception e) {
            // Log the error but don't fail the notification creation
            System.err.println("Failed to send real-time notification: " + e.getMessage());
        }
    }

    /**
     * Convert a Notification entity to NotificationDTO
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
                .createdAt(notification.getCreatedAt())
                .build();
    }
}