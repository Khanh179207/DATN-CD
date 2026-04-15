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
    public Notification createNotification(String title, String content, String type, Integer receiverId,
            Integer actorId, Integer postId, String link) {
        // Get the receiver (person receiving the notification). receiverId may be null
        // for global notifications
        Optional<Account> receiverOpt = Optional.empty();
        if (receiverId != null) {
            receiverOpt = accountDAO.findById(receiverId);
            if (receiverOpt.isEmpty()) {
                throw new RuntimeException("Receiver not found with ID: " + receiverId);
            }
        }

        // Get the actor (person who triggered the action)
        Account actor = null;
        if (actorId != null) {
            Optional<Account> actorOpt = accountDAO.findById(actorId);
            if (actorOpt.isPresent()) {
                actor = actorOpt.get();
            }
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

        // Create the notification. If receiverOpt is empty, this becomes a global
        // notification (account == null)
        Notification.NotificationBuilder nb = Notification.builder()
                .title(title)
                .content(content)
                .type(type)
                .actor(actor)
                .post(post)
                .isRead(0) // Default to unread
                .createdAt(LocalDateTime.now())
                .link(link);

        if (receiverOpt.isPresent()) {
            nb.account(receiverOpt.get()).isGlobal(false);
        } else {
            nb.account(null).isGlobal(true);
        }

        Notification notification = nb.build();

        Notification savedNotification = notificationDAO.save(notification);

        // Send real-time notification via WebSocket only for user-specific
        // notifications
        if (receiverOpt.isPresent()) {
            sendRealtimeNotification(receiverId, convertToDTO(savedNotification));
        } else {
            // Global notification saved. No per-user realtime send here (admin/manual flows
            // may broadcast separately).
            System.out.println("Saved global notification (no user target)");
        }

        return savedNotification;
    }

    @Override
    public void notifyFollow(String followerUsername, Integer followedAccountId) {
        // Get actor by username
        Optional<Account> followerOpt = accountDAO.findByUsername(followerUsername);
        if (followerOpt.isEmpty()) {
            throw new RuntimeException("Follower not found with username: " + followerUsername);
        }
        Integer followerId = followerOpt.get().getAccountID();

        String title = "Người theo dõi mới";
        String content = " đã bắt đầu theo dõi bạn.";
        String type = "FOLLOW";
        String link = "/profile/" + followerId; // Link to the follower user's profile

        createNotification(title, content, type, followedAccountId, followerId, null, link);
    }

    @Override
    public void notifyLike(String likerUsername, Integer postOwnerId, Integer postId) {
        System.out.println("=== notifyLike CALLED ===");
        System.out.println("likerUsername: " + likerUsername);
        System.out.println("postOwnerId: " + postOwnerId);
        System.out.println("postId: " + postId);
        // Lấy actor (người like)
        Optional<Account> likerOpt = accountDAO.findByUsername(likerUsername);
        if (likerOpt.isEmpty()) {
            throw new RuntimeException("Liker not found with username: " + likerUsername);
        }
        Integer likerId = likerOpt.get().getAccountID();

        String title = "Lượt thích mới";
        String content = " đã thích bài viết của bạn.";
        String type = "LIKE";
        String link = "/post/" + postId;

        createNotification(title, content, type, postOwnerId, likerId, postId, link);
    }

    @Override
    public void notifyComment(String commenterUsername, Integer postOwnerId, Integer postId, Integer commentId) {
        // Get actor by username
        Optional<Account> commenterOpt = accountDAO.findByUsername(commenterUsername);
        if (commenterOpt.isEmpty()) {
            throw new RuntimeException("Commenter not found with username: " + commenterUsername);
        }
        Integer commenterId = commenterOpt.get().getAccountID();

        String title = "Bình luận mới";
        String content = " đã bình luận bài viết của bạn.";
        String type = "COMMENT";
        String link = commentId != null ? "/post/" + postId + "#comment-" + commentId : "/post/" + postId;

        createNotification(title, content, type, postOwnerId, commenterId, postId, link);
    }

    @Override
    public void notifyRating(String raterUsername, Integer postOwnerId, Integer postId) {
        // Get actor by username
        Optional<Account> raterOpt = accountDAO.findByUsername(raterUsername);
        if (raterOpt.isEmpty()) {
            throw new RuntimeException("Rater not found with username: " + raterUsername);
        }
        Integer raterId = raterOpt.get().getAccountID();

        String title = "Đánh giá mới";
        String content = " đã đánh giá bài viết của bạn.";
        String type = "RATING";
        String link = "/post/" + postId; // Link to the post

        createNotification(title, content, type, postOwnerId, raterId, postId, link);
    }

    @Override
    public void notifyEventVote(String voterUsername, Integer postOwnerId, Integer postId) {
        // Get actor by username
        Optional<Account> voterOpt = accountDAO.findByUsername(voterUsername);
        if (voterOpt.isEmpty()) {
            throw new RuntimeException("Voter not found with username: " + voterUsername);
        }
        Integer voterId = voterOpt.get().getAccountID();

        String title = "Lượt bình chọn mới";
        String content = " đã bình chọn cho bài viết của bạn trong sự kiện.";
        String type = "EVENT_VOTE";
        String link = "/post/" + postId; // Link to the post

        createNotification(title, content, type, postOwnerId, voterId, postId, link);
    }

    @Override
    public void notifyAdminTicket(String userUsername, Integer ticketId) {
        // Get actor by username
        Optional<Account> userOpt = accountDAO.findByUsername(userUsername);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found with username: " + userUsername);
        }
        Integer userId = userOpt.get().getAccountID();

        // Notify all admin accounts
        List<Account> admins = accountDAO.findByIsAdmin(1);
        for (Account admin : admins) {
            String title = "Ticket hỗ trợ mới";
            String content = " đã gửi 1 ticket hỗ trợ.";
            String type = "TICKET";
            String link = "/admin/tickets"; // Link to admin tickets page
            Notification notification = createNotification(title, content, type, admin.getAccountID(), userId, null,
                    link);
        }
    }

    @Override
    public void notifyAdminFeedback(String userUsername, Integer feedbackId) {
        // Get actor by username
        Optional<Account> userOpt = accountDAO.findByUsername(userUsername);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found with username: " + userUsername);
        }
        Integer userId = userOpt.get().getAccountID();

        // Notify all admin accounts
        List<Account> admins = accountDAO.findByIsAdmin(1);
        for (Account admin : admins) {
            String title = "Phản hồi mới";
            String content = " đã gửi phản hồi.";
            String type = "FEEDBACK";
            String link = "/admin/feedback"; // Link to admin feedback page
            Notification notification = createNotification(title, content, type, admin.getAccountID(), userId, null,
                    link);
            sendAdminAlert(notification);
        }
    }

    @Override
    public void notifyAdminReport(String userUsername, Integer reportId) {
        // Get actor by username
        Optional<Account> userOpt = accountDAO.findByUsername(userUsername);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found with username: " + userUsername);
        }
        Integer userId = userOpt.get().getAccountID();

        // Notify all admin accounts
        List<Account> admins = accountDAO.findByIsAdmin(1);
        for (Account admin : admins) {
            String title = "Báo cáo mới";
            String content = " đã gửi một báo cáo.";
            String type = "REPORT";
            String link = "/admin/reports"; // Link to admin reports page
            Notification notification = createNotification(title, content, type, admin.getAccountID(), userId, null,
                    link);
            sendAdminAlert(notification);
        }
    }

    @Override
    public void notifyAdminPostPendingApproval(String userUsername, Integer postId) {
        // Get actor by username
        Optional<Account> userOpt = accountDAO.findByUsername(userUsername);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found with username: " + userUsername);
        }
        Integer userId = userOpt.get().getAccountID();

        // Notify all admin accounts
        List<Account> admins = accountDAO.findByIsAdmin(1);
        for (Account admin : admins) {
            String title = "Bài viết chờ duyệt";
            String content = " đã tạo một bài viết mới cần được duyệt.";
            String type = "POST_PENDING_APPROVAL";
            String link = "/admin/posts/pending"; // Link to admin pending posts page
            Notification notification = createNotification(title, content, type, admin.getAccountID(), userId, postId,
                    link);
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

        String title = "Bài viết được duyệt";
        String content = "Bài viết của bạn đã được admin duyệt.";
        String type = "POST_APPROVED";
        String link = "/post/" + postId; // Link to the approved post

        // No actor for admin approval - actor will be null
        createNotification(title, content, type, postOwner.getAccountID(), null, postId, link);
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

        String title = "Bài viết bị từ chối";
        String content = reason != null && !reason.trim().isEmpty()
                ? "Bài viết của bạn đã bị admin từ chối. Lý do: " + reason
                : "Bài viết của bạn đã bị admin từ chối.";
        String type = "POST_REJECTED";
        String link = "/post/" + postId; // Link to the rejected post

        // No actor for admin rejection - actor will be null
        createNotification(title, content, type, postOwner.getAccountID(), null, postId, link);
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

        String title = "Bài viết bị vô hiệu hóa";
        String content = "Bài viết của bạn đã bị admin vô hiệu hóa.";
        String type = "POST_DISABLED";
        String link = "/post/" + postId; // Link to the disabled post

        // No actor for admin disable - actor will be null
        createNotification(title, content, type, postOwner.getAccountID(), null, postId, link);
    }

    /**
     * Send a real-time notification to a specific user via WebSocket
     * Uses topic-based delivery to avoid principal mismatch issues
     * 
     * @param accountId       The account ID of the recipient
     * @param notificationDTO The notification data to send
     */
    private void sendRealtimeNotification(Integer accountId, NotificationDTO notificationDTO) {
        try {
            // Send to user-specific topic based on account ID
            String destination = "/topic/notifications/" + accountId;
            messagingTemplate.convertAndSend(destination, notificationDTO);
            System.out.println("Sent notification to " + destination);
        } catch (Exception e) {
            // Log the error but don't fail the notification creation
            System.err.println("Failed to send real-time notification: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Send a real-time admin alert to all connected admin users via WebSocket
     * 
     * @param notification The notification entity to send
     */
    private void sendAdminAlert(Notification notification) {
        try {
            NotificationDTO dto = convertToDTO(notification);

            // Send to broadcast admin alerts topic (all admins receive)
            messagingTemplate.convertAndSend(
                    "/topic/admin-alerts",
                    dto);

            // Also send user-specific admin notification
            Integer adminAccountId = notification.getAccount().getAccountID();
            messagingTemplate.convertAndSend(
                    "/topic/admin-notifications/" + adminAccountId,
                    dto);

            System.out.println("Sent admin alert to broadcast and user " + adminAccountId);
        } catch (Exception e) {
            // Log the error but don't fail the notification creation
            System.err.println("Failed to send admin alert: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Convert a Notification entity to NotificationDTO
     * 
     * @param notification The notification entity
     * @return The notification DTO
     */
    private NotificationDTO convertToDTO(Notification notification) {
        // Use actor (person who triggered action) instead of account (receiver)
        Account actor = notification.getActor();
        Account receiver = notification.getAccount();

        String username = actor != null ? actor.getUsername() : "Unknown";
        String avatarUrl = actor != null ? actor.getAvatar() : null;

        System.out.println("Actor: " + (actor != null ? actor.getUsername() : "null"));
        System.out.println("Receiver: " + (receiver != null ? receiver.getUsername() : "null"));
        System.out.println("DTO: username=" + username + ", avatarUrl=" + avatarUrl);

        return NotificationDTO.builder()
                .notificationID(notification.getNotificationID())
                .title(notification.getTitle())
                .content(notification.getContent())
                .type(notification.getType())
                .postId(notification.getPost() != null ? notification.getPost().getPostID() : null)
                .link(notification.getLink())
                .createdAt(notification.getCreatedAt())
                .isRead(notification.getIsRead())
                .isGlobal(notification.getIsGlobal() != null ? notification.getIsGlobal() : false)
                .parentNotificationID(notification.getParentNotification() != null
                        ? notification.getParentNotification().getNotificationID()
                        : null)
                .username(username)
                .avatarUrl(avatarUrl)
                .build();
    }
}