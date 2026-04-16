package poly.edu.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import poly.edu.dao.AccountDAO;
import poly.edu.dao.NotificationDAO;
import poly.edu.entity.Account;
import poly.edu.entity.Notification;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()") // 🔥 Bắt buộc đăng nhập
public class NotificationController {

    private final NotificationDAO notificationDAO;
    private final AccountDAO accountDAO; // 🔥 Cấy thêm DAO để kiểm tra danh tính

    // 🔥 HÀM BẢO MẬT: Kiểm tra xem User có đang "nhìn trộm" dữ liệu của người khác không
    private boolean isNotOwner(Integer requestAccountId) {
        // Lấy định danh (Username hoặc Email) từ Token hiện tại
        String usernameOrEmail = SecurityContextHolder.getContext().getAuthentication().getName();

        // Truy vấn ra Account hiện đang đăng nhập
        Account currentUser = accountDAO.findByUsername(usernameOrEmail)
                .orElseGet(() -> accountDAO.findByEmail(usernameOrEmail).orElse(null));

        // Nếu không tìm thấy hoặc ID đăng nhập khác với ID trên URL -> Đang nhìn trộm!
        return currentUser == null || !currentUser.getAccountID().equals(requestAccountId);
    }

    // 🟡 USER: Xem danh sách thông báo của mình ──────────────────────────────
    @GetMapping("/{accountID}")
    @Transactional
    public ResponseEntity<?> getUserNotifications(@PathVariable Integer accountID) {

        // 🚨 CHỐT CHẶN CHỐNG NHÌN TRỘM (IDOR)
        if (isNotOwner(accountID)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "Từ chối truy cập: Không được phép xem thông báo của người khác!"));
        }

        List<Map<String, Object>> result = notificationDAO
                .findByAccount_AccountID(accountID)
                .stream()
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .map(n -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("notificationID", n.getNotificationID());
                    map.put("title", n.getTitle());
                    map.put("content", n.getContent());
                    map.put("type", n.getType());
                    map.put("isRead", n.getIsRead());
                    map.put("createdAt", n.getCreatedAt().toString());
                    map.put("postID", n.getPost() != null ? n.getPost().getPostID() : 0);
                    map.put("link", n.getLink()); // Có thể null
                    map.put("username", n.getActor() != null ? n.getActor().getUsername() : "Hệ thống");
                    map.put("avatarUrl", n.getActor() != null ? n.getActor().getAvatar() : null);
                    return map;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }

    // 🟡 USER: Đánh dấu 1 thông báo là đã đọc ────────────────────────────────
    @PutMapping("/{id}/read")
    @Transactional
    public ResponseEntity<?> markRead(@PathVariable Integer id) {
        return notificationDAO.findById(id).map(n -> {

            // 🚨 CHỐT CHẶN CHỐNG NHÌN TRỘM (Tránh việc hacker tự ý đánh dấu đọc của người khác)
            if (isNotOwner(n.getAccount().getAccountID())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("message", "Từ chối truy cập: Bạn không có quyền thao tác trên thông báo này!"));
            }

            n.setIsRead(1);
            n.setReadAt(LocalDateTime.now());
            notificationDAO.save(n);
            return ResponseEntity.ok(Map.of("message", "Đã đánh dấu là đã đọc"));

        }).orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", "Không tìm thấy thông báo")));
    }

    // 🟡 USER: Đánh dấu TẤT CẢ thông báo là đã đọc ────────────────────────────
    @PutMapping("/{accountID}/read-all")
    @Transactional
    public ResponseEntity<?> markAllRead(@PathVariable Integer accountID) {

        // 🚨 CHỐT CHẶN CHỐNG NHÌN TRỘM
        if (isNotOwner(accountID)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "Từ chối truy cập: Bạn không có quyền thao tác trên dữ liệu của người khác!"));
        }

        List<Notification> unread = notificationDAO
                .findByAccount_AccountID(accountID)
                .stream()
                .filter(n -> n.getIsRead() == 0)
                .collect(Collectors.toList());

        unread.forEach(n -> {
            n.setIsRead(1);
            n.setReadAt(LocalDateTime.now());
        });
        notificationDAO.saveAll(unread);

        return ResponseEntity.ok(Map.of(
                "message", "Đã đánh dấu tất cả là đã đọc",
                "updatedCount", unread.size()));
    }

    // 🔥 ĐÃ XÓA HOÀN TOÀN HÀM XÓA THÔNG BÁO THEO CHỈ ĐẠO CỦA SẾP!
}