package poly.edu.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import poly.edu.dao.AccountDAO;
import poly.edu.dao.NotificationDAO;
import poly.edu.dao.PostDAO;
import poly.edu.dto.AdminNotificationDTO;
import poly.edu.dto.NotificationDTO;
import poly.edu.entity.Account;
import poly.edu.entity.Notification;
import poly.edu.entity.Post;
import poly.edu.service.AdminNotificationService;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional // Nên có Transactional để đảm bảo dữ liệu khi lưu hàng loạt
public class AdminNotificationServiceImpl implements AdminNotificationService {

    private final NotificationDAO notificationDAO;
    private final AccountDAO accountDAO;
    private final PostDAO postDAO;
    private final SimpMessagingTemplate messagingTemplate;

    // Hàm dùng chung để tạo thông báo cá nhân
    private Notification createNotification(Account acc, Post post, AdminNotificationDTO dto) {
        return Notification.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .type("ADMIN_MANUAL") // 🔥 Đồng bộ với Controller để hiện trong lịch sử Admin
                .account(acc)
                .post(post)
                .isRead(0)
                .createdAt(LocalDateTime.now())
                .link(dto.getLink())
                .isGlobal(false)
                .build();
    }

    @Override
    public void sendToAll(AdminNotificationDTO dto) {
        // 🚀 TỐI ƯU HIỆU NĂNG: Sử dụng thông báo Global (HEAD) thay vì loop lưu từng
        // user (DEVELOP)
        Post post = null;
        if (dto.getPostID() != null) {
            post = postDAO.findById(dto.getPostID()).orElse(null);
        }

        Notification noti = Notification.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .type("ADMIN_MANUAL") // 🔥 Đồng bộ với Controller để hiện trong lịch sử Admin
                .account(null)
                .post(post)
                .isRead(0)
                .createdAt(LocalDateTime.now())
                .link(dto.getLink())
                .isGlobal(true)
                .build();

        Notification saved = notificationDAO.save(noti);

        try {
            // Gửi tín hiệu WebSocket để các Client nhận được thông báo ngay lập tức
            NotificationDTO dtoOut = convertToDTO(saved);
            messagingTemplate.convertAndSend("/topic/admin-alerts", dtoOut);
            System.out.println("Đã phát thông báo admin realtime tới /topic/admin-alerts (global)");
        } catch (Exception e) {
            System.err.println("Không thể phát thông báo admin realtime: " + e.getMessage());
        }
    }

    @Override
    public void sendToOne(Integer accountID, AdminNotificationDTO dto) {
        Account acc = accountDAO.findById(accountID)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản ID: " + accountID));

        Post post = null;
        if (dto.getPostID() != null) {
            post = postDAO.findById(dto.getPostID()).orElse(null);
        }

        Notification noti = createNotification(acc, post, dto);
        Notification saved = notificationDAO.save(noti);

        try {
            NotificationDTO dtoOut = convertToDTO(saved);
            messagingTemplate.convertAndSend("/topic/admin-notifications/" + accountID, dtoOut);
            System.out.println("Đã gửi thông báo admin realtime tới /topic/admin-notifications/" + accountID);
        } catch (Exception e) {
            System.err.println("Không thể gửi thông báo admin realtime: " + e.getMessage());
        }
    }

    @Override
    public void delete(Integer id) {
        notificationDAO.findById(id).ifPresent(parent -> {
            // Xóa thông báo con (clone đọc) trước để đảm bảo toàn vẹn dữ liệu.
            notificationDAO.deleteByParentNotificationId(parent.getNotificationID());
            // Sau đó mới xóa thông báo cha do admin tạo.
            notificationDAO.delete(parent);
        });
    }

    private NotificationDTO convertToDTO(Notification saved) {

        return NotificationDTO.builder()
                .notificationID(saved.getNotificationID())
                .title(saved.getTitle())
                .content(saved.getContent())
                .type(saved.getType())
                .postId(saved.getPost() != null ? saved.getPost().getPostID() : null)
                .link(saved.getLink())
                .createdAt(saved.getCreatedAt())
                .isRead(saved.getIsRead())
                .isGlobal(saved.getIsGlobal() != null ? saved.getIsGlobal() : false)
                .parentNotificationID(
                        saved.getParentNotification() != null ? saved.getParentNotification().getNotificationID()
                                : null)
                .username("Hệ thống GoMet")
                .avatarUrl("/logogoc.jpg")
                .build();
    }
}
