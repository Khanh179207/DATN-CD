package poly.edu.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import poly.edu.dao.AccountDAO;
import poly.edu.dao.NotificationDAO;
import poly.edu.dao.PostDAO;
import poly.edu.dto.AdminNotificationDTO;
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

        Post post = null;
        if (dto.getPostID() != null) {
            post = postDAO.findById(dto.getPostID()).orElse(null);
        }

        List<Account> accounts = accountDAO.findAll();

        for (Account acc : accounts) {
            Notification noti = createNotification(acc, post, dto);
            notificationDAO.save(noti);
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
        notificationDAO.save(noti);
    }

    @Override
    public void delete(Integer id) {
        notificationDAO.deleteById(id);
    }
}
