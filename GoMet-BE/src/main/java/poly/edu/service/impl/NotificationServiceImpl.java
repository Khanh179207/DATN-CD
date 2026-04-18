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
import java.time.format.DateTimeFormatter;
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
        // Lấy thông tin người nhận. receiverId có thể null đối với thông báo toàn cầu
        Optional<Account> receiverOpt = Optional.empty();
        if (receiverId != null) {
            receiverOpt = accountDAO.findById(receiverId);
            if (receiverOpt.isEmpty()) {
                throw new RuntimeException("Không tìm thấy người nhận với ID: " + receiverId);
            }
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
        Notification.NotificationBuilder nb = Notification.builder()
                .title(title)
                .content(content)
                .type(type)
                .actor(actor)
                .post(post)
                .isRead(0) // Mặc định là chưa đọc
                .createdAt(LocalDateTime.now())
                .link(link);

        if (receiverOpt.isPresent()) {
            nb.account(receiverOpt.get()).isGlobal(false);
        } else {
            nb.account(null).isGlobal(true);
        }

        Notification notification = nb.build();
        Notification savedNotification = notificationDAO.save(notification);

        // Gửi thông báo real-time qua WebSocket chỉ cho thông báo gửi tới user cụ thể
        if (receiverOpt.isPresent()) {
            sendRealtimeNotification(receiverId, convertToDTO(savedNotification));
        }

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
            Notification notification = createNotification(title, content, type, admin.getAccountID(), userId, null,
                    link);
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
            Notification notification = createNotification(title, content, type, admin.getAccountID(), userId, null,
                    link);
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
            Notification notification = createNotification(title, content, type, admin.getAccountID(), userId, null,
                    link);
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
            Notification notification = createNotification(title, content, type, admin.getAccountID(), userId, postId,
                    link);
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
        if (postOwner == null)
            return;

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
        if (postOwner == null)
            return;

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
        if (postOwner == null)
            return;

        String title = "Bài viết bị vô hiệu hóa";
        String content = "Bài viết của bạn đã bị Quản trị viên vô hiệu hóa.";
        String type = "POST_DISABLED";
        String link = "/post/" + postId;

        createNotification(title, content, type, postOwner.getAccountID(), null, postId, link);
    }

    @Override
    public void notifyCommentReply(String replierUsername, Integer parentCommentAuthorId, Integer postId,
            Integer commentId) {
        Optional<Account> replierOpt = accountDAO.findByUsername(replierUsername);
        if (replierOpt.isEmpty())
            return;
        Integer replierId = replierOpt.get().getAccountID();

        String title = "Trả lời bình luận";
        String content = " đã trả lời bình luận của bạn.";
        String type = "COMMENT_REPLY";
        String link = commentId != null ? "/post/" + postId + "#comment-" + commentId : "/post/" + postId;
        createNotification(title, content, type, parentCommentAuthorId, replierId, postId, link);
    }

    @Override
    public void notifyCommentLike(String likerUsername, Integer commentAuthorId, Integer postId, Integer commentId) {
        Optional<Account> likerOpt = accountDAO.findByUsername(likerUsername);
        if (likerOpt.isEmpty())
            return;
        Integer likerId = likerOpt.get().getAccountID();

        String title = "Lượt thích bình luận";
        String content = " đã thích bình luận của bạn.";
        String type = "COMMENT_LIKE";
        String link = commentId != null ? "/post/" + postId + "#comment-" + commentId : "/post/" + postId;
        createNotification(title, content, type, commentAuthorId, likerId, postId, link);
    }

    @Override
    public void notifyMention(String mentionerUsername, Integer mentionedAccountId, Integer postId, Integer commentId) {
        Optional<Account> mentionerOpt = accountDAO.findByUsername(mentionerUsername);
        if (mentionerOpt.isEmpty())
            return;
        Integer mentionerId = mentionerOpt.get().getAccountID();

        String title = "Nhắc đến bạn";
        String content = " đã nhắc đến bạn trong một " + (commentId != null ? "bình luận." : "bài viết.");
        String type = "MENTION";
        String link = commentId != null ? "/post/" + postId + "#comment-" + commentId : "/post/" + postId;
        createNotification(title, content, type, mentionedAccountId, mentionerId, postId, link);
    }

    @Override
    public void notifyTicketUpdate(Integer ticketId, Integer newStatus, Integer accountId) {
        String title = "Cập nhật phiếu hỗ trợ";
        String content = newStatus == 1 ? "Phiếu hỗ trợ #" + ticketId + " đang được xử lý."
                : newStatus == 2 ? "Phiếu hỗ trợ #" + ticketId + " đã được giải quyết."
                        : "Phiếu hỗ trợ #" + ticketId + " đã bị từ chối.";
        String type = "TICKET_UPDATE";
        createNotification(title, content, type, accountId, null, null, null);
    }

    @Override
    public void notifyAppealUpdate(Integer appealId, String status, Integer accountId) {
        String title = "Cập nhật khiếu nại";
        String content;
        if ("Approved".equalsIgnoreCase(status)) {
            content = "Khiếu nại của bạn đã được duyệt và tài khoản đã được mở khóa.";
        } else if ("Rejected".equalsIgnoreCase(status)) {
            content = "Khiếu nại của bạn đã bị từ chối.";
        } else {
            content = "Đơn khiếu nại của bạn đã được cập nhật trạng thái: " + status;
        }
        String type = "APPEAL_UPDATE";
        createNotification(title, content, type, accountId, null, null, "/appeal");
    }

    @Override
    public void notifyCommentStatusChange(Integer commentId, Integer accountId, String action) {
        String title = action.equals("DELETE") ? "Bình luận bị xóa" : "Bình luận được khôi phục";
        String content = action.equals("DELETE")
                ? "Một bình luận của bạn đã bị quản trị viên ẩn/xóa do vi phạm nội quy."
                : "Bình luận của bạn đã được quản trị viên khôi phục.";
        String type = "COMMENT_STATUS";
        createNotification(title, content, type, accountId, null, null, null);
    }

    @Override
    public void notifyReward(Integer accountId, Integer points, Integer premiumDays, String source) {
        String title = "Nhận thưởng thành công";
        StringBuilder content = new StringBuilder("Bạn vừa nhận được ");
        if (points != null && points > 0)
            content.append(points).append(" GoMet Point ");
        if (points != null && points > 0 && premiumDays != null && premiumDays > 0)
            content.append("và ");
        if (premiumDays != null && premiumDays > 0)
            content.append(premiumDays).append(" ngày Premium ");
        content.append("từ ").append(source).append(".");

        String type = "REWARD";
        createNotification(title, content.toString(), type, accountId, null, null, "/profile/points");
    }

    @Override
    public void notifyPaymentStatus(Integer accountId, boolean isSuccess, String orderCode) {
        String title = isSuccess ? "Thanh toán thành công" : "Thanh toán thất bại";
        String content = isSuccess ? "Giao dịch " + orderCode + " đã hoàn tất. Cảm ơn bạn!"
                : "Giao dịch " + orderCode + " đã thất bại hoặc bị hủy.";
        String type = "PAYMENT_STATUS";
        createNotification(title, content, type, accountId, null, null, isSuccess ? "/premium" : "/upgrade");
    }

    @Override
    public void notifyPremiumStatus(Integer accountId, boolean isActivated, LocalDateTime endAt) {
        String title = isActivated ? "Premium đã được kích hoạt" : "Premium đã hết hạn";
        String content;
        String type;
        String link;

        if (isActivated) {
            String endAtText = endAt != null
                    ? endAt.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
                    : "không xác định";
            content = "Gói Premium của bạn đã được kích hoạt thành công. Thời hạn đến: " + endAtText + ".";
            type = "PREMIUM_PURCHASED";
            link = "/profile";
        } else {
            content = "Gói Premium của bạn đã hết hạn. Hãy gia hạn để tiếp tục sử dụng các đặc quyền.";
            type = "PREMIUM_EXPIRED";
            link = "/upgrade";
        }

        createNotification(title, content, type, accountId, null, null, link);
    }

    @Override
    public void notifyEventWinner(Integer accountId, Integer eventId, Integer rank) {
        String title = "Kết quả sự kiện";
        String content = "Chúc mừng! Bạn đã đạt " + (rank == 1 ? "Giải Nhất" : rank == 2 ? "Giải Nhì" : "Giải Ba")
                + " trong sự kiện!";
        String type = "EVENT_WINNER";
        createNotification(title, content, type, accountId, null, null, "/events");
    }

    @Override
    public void notifyAccountStatus(Integer accountId, String status, String reason) {
        String title = status.equals("BANNED") ? "Kỷ luật tài khoản" : "Tài khoản được mở khóa";
        String content = status.equals("BANNED")
                ? "Tài khoản của bạn đã bị cấm/đình chỉ. Lý do: " + (reason != null ? reason : "Vi phạm nội quy")
                : "Tài khoản của bạn đã được ân xá/mở khóa.";
        String type = "ACCOUNT_STATUS";
        createNotification(title, content, type, accountId, null, null, "/login");
    }

    private void sendRealtimeNotification(Integer accountId, NotificationDTO notificationDTO) {
        try {
            String destination = "/topic/notifications/" + accountId;
            messagingTemplate.convertAndSend(destination, notificationDTO);
        } catch (Exception e) {
            System.err.println("Lỗi gửi thông báo real-time: " + e.getMessage());
        }
    }

    private void sendAdminAlert(Notification notification) {
        try {
            NotificationDTO dto = convertToDTO(notification);
            messagingTemplate.convertAndSend("/topic/admin-alerts", dto);
            if (notification.getAccount() != null) {
                Integer adminAccountId = notification.getAccount().getAccountID();
                messagingTemplate.convertAndSend("/topic/admin-notifications/" + adminAccountId, dto);
            }
        } catch (Exception e) {
            System.err.println("Lỗi gửi cảnh báo cho Admin: " + e.getMessage());
        }
    }

    private NotificationDTO convertToDTO(Notification notification) {
        Account actor = notification.getActor();
        String username = actor != null ? actor.getUsername() : "Hệ thống GoMet";
        String avatarUrl = (actor != null && actor.getAvatar() != null) ? actor.getAvatar() : "/logogoc.jpg";

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
