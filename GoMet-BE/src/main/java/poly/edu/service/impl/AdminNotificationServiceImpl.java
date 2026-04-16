package poly.edu.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import poly.edu.dao.AccountDAO;
import poly.edu.dao.NotificationDAO;
import poly.edu.dao.PostDAO;
import poly.edu.dto.AdminNotificationDTO;
import poly.edu.entity.Account;
import poly.edu.entity.Notification;
import poly.edu.entity.Post;
import poly.edu.service.AdminNotificationService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional // Nên có Transactional để đảm bảo dữ liệu khi lưu hàng loạt
public class AdminNotificationServiceImpl implements AdminNotificationService {

    private final NotificationDAO notificationDAO;
    private final AccountDAO accountDAO;
    private final PostDAO postDAO;

    // Hàm dùng chung để tạo thông báo
    private Notification createNotification(Account acc, Post post, AdminNotificationDTO dto) {
        return Notification.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .type("SYSTEM") // 🔥 Đã đổi từ ADMIN_MANUAL sang SYSTEM theo thống nhất
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

        // 🔥 Chỉ lấy những tài khoản còn hoạt động, không gửi cho người bị Ban
        List<Account> accounts = accountDAO.findAll().stream()
                .filter(acc -> acc.getIsActive() != -1)
                .toList();

        List<Notification> notificationList = new ArrayList<>();

        for (Account acc : accounts) {
            notificationList.add(createNotification(acc, post, dto));
        }

        // 🔥 Tối ưu hiệu năng: Lưu hàng loạt trong 1 lần thực thi SQL
        if (!notificationList.isEmpty()) {
            notificationDAO.saveAll(notificationList);
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
        notificationDAO.save(noti);
    }

    @Override
    public void delete(Integer id) {
        // Kiểm tra tồn tại trước khi xóa
        if (notificationDAO.existsById(id)) {
            notificationDAO.deleteById(id);
        }
    }
}