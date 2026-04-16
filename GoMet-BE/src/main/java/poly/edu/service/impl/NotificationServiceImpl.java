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
        // Lấy thông tin người nhận
        Optional<Account> receiverOpt = accountDAO.findById(receiverId);
        if (receiverOpt.isEmpty()) {
            throw new RuntimeException("Không tìm thấy người nhận với ID: " + receiverId);
        }

        // Lấy thông tin người thực hiện hành động (có thể null nếu là hệ thống)
        Account actor = null;
        if (actorId != null) {
            Optional<Account> actorOpt = accountDAO.findById(actorId);
            if (actorOpt.isPresent()) {
                actor = actorOpt.get();
            }
        }

        // Lấy thông tin bài viết nếu có
        Post post = null;
        if (postId != null) {
            Optional<Post> postOpt = postDAO.findById(postId);
            if (postOpt.isEmpty()) {
                throw new RuntimeException("Không tìm thấy bài viết với ID: " + postId);
            }
            post = postOpt.get();
        }

        // Tạo thông báo
        Notification notification = Notification.builder()
                .title(title)
                .content(content)
                .type(type)
                .account(receiverOpt.get())
                .actor(actor)
                .post(post)
                .isRead(0) // Mặc định là chưa đọc
                .createdAt(LocalDateTime.now())
                .link(link)
                .build();

        Notification savedNotification = notificationDAO.save(notification);

        // Gửi thông báo real-time qua WebSocket
        sendRealtimeNotification(receiverId, convertToDTO(savedNotification));

        return savedNotification;
    }

    @Override
    public void notifyFollow(String followerUsername, Integer followedAccountId) {
        Optional<Account> followerOpt = accountDAO.findByUsername(followerUsername);
        if (followerOpt.isEmpty()) {
            throw new RuntimeException("Không tìm thấy người theo dõi với username: " + followerUsername);
        }
        Integer followerId = followerOpt.get().getAccountID();

        String title = "Người theo dõi mới";
        String content = " đã bắt đầu theo dõi bạn.";
        String type = "FOLLOW";
        String link = "/profile/" + followerId;

        createNotification(title, content, type, followedAccountId, followerId, null, link);
    }

    @Override
    public void notifyLike(String likerUsername, Integer postOwnerId, Integer postId) {
        Optional<Account> likerOpt = accountDAO.findByUsername(likerUsername);
        if (likerOpt.isEmpty()) {
            throw new RuntimeException("Không tìm thấy người thích với username: " + likerUsername);
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
        Optional<Account> commenterOpt = accountDAO.findByUsername(commenterUsername);
        if (commenterOpt.isEmpty()) {
            throw new RuntimeException("Không tìm thấy người bình luận với username: " + commenterUsername);
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
        Optional<Account> raterOpt = accountDAO.findByUsername(raterUsername);
        if (raterOpt.isEmpty()) {
            throw new RuntimeException("Không tìm thấy người đánh giá với username: " + raterUsername);
        }
        Integer raterId = raterOpt.get().getAccountID();

        String title = "Đánh giá mới";
        String content = " đã đánh giá bài viết của bạn.";
        String type = "RATING";
        String link = "/post/" + postId;

        createNotification(title, content, type, postOwnerId, raterId, postId, link);
    }

    @Override
    public void notifyEventVote(String voterUsername, Integer postOwnerId, Integer postId) {
        Optional<Account> voterOpt = accountDAO.findByUsername(voterUsername);
        if (voterOpt.isEmpty()) {
            throw new RuntimeException("Không tìm thấy người bình chọn với username: " + voterUsername);
        }
        Integer voterId = voterOpt.get().getAccountID();

        String title = "Lượt bình chọn mới";
        String content = " đã bình chọn cho bài viết của bạn trong sự kiện.";
        String type = "EVENT_VOTE";
        String link = "/post/" + postId;

        createNotification(title, content, type, postOwnerId, voterId, postId, link);
    }

    @Override
    public void notifyAdminTicket(String userUsername, Integer ticketId) {
        Optional<Account> userOpt = accountDAO.findByUsername(userUsername);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("Không tìm thấy người dùng với username: " + userUsername);
        }
        Integer userId = userOpt.get().getAccountID();

        List<Account> admins = accountDAO.findByIsAdmin(1);
        for (Account admin : admins) {
            String title = "Phiếu hỗ trợ mới";
            String content = " đã gửi 1 phiếu hỗ trợ.";
            String type = "TICKET";
            String link = "/admin/tickets";
            Notification notification = createNotification(title, content, type, admin.getAccountID(), userId, null, link);

            // 🔥 FIX LỖI SẾP BỊ QUÊN Ở ĐÂY: Đã thêm còi hú cho Admin
            sendAdminAlert(notification);
        }
    }

    @Override
    public void notifyAdminFeedback(String userUsername, Integer feedbackId) {
        Optional<Account> userOpt = accountDAO.findByUsername(userUsername);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("Không tìm thấy người dùng với username: " + userUsername);
        }
        Integer userId = userOpt.get().getAccountID();

        List<Account> admins = accountDAO.findByIsAdmin(1);
        for (Account admin : admins) {
            String title = "Phản hồi mới";
            String content = " đã gửi phản hồi.";
            String type = "FEEDBACK";
            String link = "/admin/feedback";
            Notification notification = createNotification(title, content, type, admin.getAccountID(), userId, null, link);
            sendAdminAlert(notification);
        }
    }

    @Override
    public void notifyAdminReport(String userUsername, Integer reportId) {
        Optional<Account> userOpt = accountDAO.findByUsername(userUsername);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("Không tìm thấy người dùng với username: " + userUsername);
        }
        Integer userId = userOpt.get().getAccountID();

        List<Account> admins = accountDAO.findByIsAdmin(1);
        for (Account admin : admins) {
            String title = "Báo cáo mới";
            String content = " đã gửi một báo cáo.";
            String type = "REPORT";
            String link = "/admin/reports";
            Notification notification = createNotification(title, content, type, admin.getAccountID(), userId, null, link);
            sendAdminAlert(notification);
        }
    }

    @Override
    public void notifyAdminPostPendingApproval(String userUsername, Integer postId) {
        Optional<Account> userOpt = accountDAO.findByUsername(userUsername);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("Không tìm thấy người dùng với username: " + userUsername);
        }
        Integer userId = userOpt.get().getAccountID();

        List<Account> admins = accountDAO.findByIsAdmin(1);
        for (Account admin : admins) {
            String title = "Bài viết chờ duyệt";
            String content = " đã tạo một bài viết mới cần được duyệt.";
            String type = "POST_PENDING_APPROVAL";
            String link = "/admin/posts/pending";
            Notification notification = createNotification(title, content, type, admin.getAccountID(), userId, postId, link);
            sendAdminAlert(notification);
        }
    }

    @Override
    public void notifyPostApproved(Integer postId) {
        Optional<Post> postOpt = postDAO.findById(postId);
        if (postOpt.isEmpty()) {
            throw new RuntimeException("Không tìm thấy bài viết với ID: " + postId);
        }

        Post post = postOpt.get();
        Account postOwner = post.getAccount();

        String title = "Bài viết được duyệt";
        String content = "Bài viết của bạn đã được Quản trị viên duyệt.";
        String type = "POST_APPROVED";
        String link = "/post/" + postId;

        createNotification(title, content, type, postOwner.getAccountID(), null, postId, link);
    }

    @Override
    public void notifyPostRejected(Integer postId, String reason) {
        Optional<Post> postOpt = postDAO.findById(postId);
        if (postOpt.isEmpty()) {
            throw new RuntimeException("Không tìm thấy bài viết với ID: " + postId);
        }

        Post post = postOpt.get();
        Account postOwner = post.getAccount();

        String title = "Bài viết bị từ chối";
        String content = reason != null && !reason.trim().isEmpty()
                ? "Bài viết của bạn đã bị Quản trị viên từ chối. Lý do: " + reason
                : "Bài viết của bạn đã bị Quản trị viên từ chối.";
        String type = "POST_REJECTED";
        String link = "/post/" + postId;

        createNotification(title, content, type, postOwner.getAccountID(), null, postId, link);
    }

    @Override
    public void notifyPostDisabled(Integer postId) {
        Optional<Post> postOpt = postDAO.findById(postId);
        if (postOpt.isEmpty()) {
            throw new RuntimeException("Không tìm thấy bài viết với ID: " + postId);
        }

        Post post = postOpt.get();
        Account postOwner = post.getAccount();

        String title = "Bài viết bị vô hiệu hóa";
        String content = "Bài viết của bạn đã bị Quản trị viên vô hiệu hóa.";
        String type = "POST_DISABLED";
        String link = "/post/" + postId;

        createNotification(title, content, type, postOwner.getAccountID(), null, postId, link);
    }

    /**
     * Gửi thông báo Real-time cho một người dùng cụ thể
     */
    private void sendRealtimeNotification(Integer accountId, NotificationDTO notificationDTO) {
        try {
            String destination = "/topic/notifications/" + accountId;
            messagingTemplate.convertAndSend(destination, notificationDTO);
        } catch (Exception e) {
            System.err.println("Lỗi gửi thông báo real-time: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Gửi cảnh báo Real-time cho toàn bộ Admin đang online
     */
    private void sendAdminAlert(Notification notification) {
        try {
            NotificationDTO dto = convertToDTO(notification);

            messagingTemplate.convertAndSend("/topic/admin-alerts", dto);

            Integer adminAccountId = notification.getAccount().getAccountID();
            messagingTemplate.convertAndSend("/topic/admin-notifications/" + adminAccountId, dto);

        } catch (Exception e) {
            System.err.println("Lỗi gửi cảnh báo cho Admin: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Chuyển đổi Entity sang DTO
     */
    private NotificationDTO convertToDTO(Notification notification) {
        Account actor = notification.getActor();

        // 🔥 Chuẩn hóa tên: Nếu không có người gửi thì tên là "Hệ thống GoMet"
        String username = actor != null ? actor.getUsername() : "Hệ thống GoMet";

        // 🔥 Sửa lỗi Avatar US: Thay vì để null, ta gán luôn một chiếc logo mặc định (Màu xanh, chữ GM)
        // Sếp có thể thay URL này bằng đường dẫn Logo dự án của Sếp (ví dụ: "/assets/logo.png")
        String avatarUrl = actor != null && actor.getAvatar() != null ?
                actor.getAvatar() :
                "/assets/images/logogoc.jpg";

        return NotificationDTO.builder()
                .notificationID(notification.getNotificationID())
                .title(notification.getTitle())
                .content(notification.getContent())
                .type(notification.getType())
                .postId(notification.getPost() != null ? notification.getPost().getPostID() : null)
                .link(notification.getLink())
                .createdAt(notification.getCreatedAt())
                .isRead(notification.getIsRead())
                .username(username)
                .avatarUrl(avatarUrl)
                .build();
    }
}