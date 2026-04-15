package poly.edu.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
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
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminNotificationServiceImpl implements AdminNotificationService {

    private final NotificationDAO notificationDAO;
    private final AccountDAO accountDAO;
    private final PostDAO postDAO;
    private final SimpMessagingTemplate messagingTemplate;

    private Notification createNotification(Account acc, Post post, AdminNotificationDTO dto) {
        return Notification.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .type("ADMIN_MANUAL")
                .account(acc)
                .post(post)
                .isRead(0)
                .createdAt(LocalDateTime.now())
                .link(dto.getLink())
                .build();
    }

    @Override
    public void sendToAll(AdminNotificationDTO dto) {
        // Create a single global (broadcast) notification instead of inserting per-user
        // rows
        Post post = null;
        if (dto.getPostID() != null) {
            post = postDAO.findById(dto.getPostID()).orElse(null);
        }

        Notification noti = Notification.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .type("ADMIN_MANUAL")
                .account(null)
                .post(post)
                .isRead(0)
                .createdAt(LocalDateTime.now())
                .link(dto.getLink())
                .isGlobal(true)
                .build();

        Notification saved = notificationDAO.save(noti);

        try {
            NotificationDTO dtoOut = NotificationDTO.builder()
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
                    .username(saved.getActor() != null ? saved.getActor().getUsername() : null)
                    .avatarUrl(saved.getActor() != null ? saved.getActor().getAvatar() : null)
                    .build();

            messagingTemplate.convertAndSend("/topic/admin-alerts", dtoOut);
            System.out.println("Broadcasted admin alert to /topic/admin-alerts");
        } catch (Exception e) {
            System.err.println("Failed to broadcast admin alert: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void sendToOne(Integer accountID, AdminNotificationDTO dto) {

        Account acc = accountDAO.findById(accountID).orElseThrow();

        Post post = null;
        if (dto.getPostID() != null) {
            post = postDAO.findById(dto.getPostID()).orElse(null);
        }

        Notification noti = createNotification(acc, post, dto);
        Notification saved = notificationDAO.save(noti);

        try {
            NotificationDTO dtoOut = NotificationDTO.builder()
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
                    .username(saved.getActor() != null ? saved.getActor().getUsername() : null)
                    .avatarUrl(saved.getActor() != null ? saved.getActor().getAvatar() : null)
                    .build();

            messagingTemplate.convertAndSend("/topic/admin-notifications/" + accountID, dtoOut);
            System.out.println("Sent admin notification to /topic/admin-notifications/" + accountID);
        } catch (Exception e) {
            System.err.println("Failed to send admin notification: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void delete(Integer id) {
        notificationDAO.deleteById(id);
    }
}
