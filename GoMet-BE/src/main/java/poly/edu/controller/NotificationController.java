package poly.edu.controller;

import jakarta.servlet.http.HttpServletRequest;
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
import poly.edu.util.JwtUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()") // 🔥 Bắt buộc đăng nhập
public class NotificationController {

    private final NotificationDAO notificationDAO;
    private final AccountDAO accountDAO;
    private final JwtUtils jwtUtils;
    private final HttpServletRequest request;

    private Optional<Account> getCurrentAuthenticatedAccount() {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            if (jwtUtils.validateJwtToken(token)) {
                String email = jwtUtils.getEmailFromJwtToken(token);
                Optional<Account> byEmailFromToken = accountDAO.findByEmail(email);
                if (byEmailFromToken.isPresent()) {
                    return byEmailFromToken;
                }
            }
        }

        var authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();

        if (authentication == null) {
            return Optional.empty();
        }

        String principalName = authentication.getName();
        if (principalName == null || principalName.isBlank()) {
            return Optional.empty();
        }

        Optional<Account> byEmail = accountDAO.findByEmail(principalName);
        if (byEmail.isPresent()) {
            return byEmail;
        }

        Optional<Account> byUsername = accountDAO.findByUsername(principalName);
        if (byUsername.isPresent()) {
            return byUsername;
        }

        return accountDAO.findByUsernameIgnoreCase(principalName);
    }

    private String sanitizeLink(Notification notification) {
        if (notification == null) {
            return null;
        }
        if ("TICKET_UPDATE".equalsIgnoreCase(notification.getType())) {
            return null;
        }
        return notification.getLink();
    }

    // 🔥 HÀM BẢO MẬT: Kiểm tra xem User có đang "nhìn trộm" dữ liệu của người khác không
    private boolean isNotOwner(Integer requestAccountId) {
        String usernameOrEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Account currentUser = accountDAO.findByUsername(usernameOrEmail)
                .orElseGet(() -> accountDAO.findByEmail(usernameOrEmail).orElse(null));
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

        // Build a clean list for the user using clone-on-read semantics
        // 1) Fetch user-specific notifications (includes clones and targeted ones)
        List<Notification> userNotifications = notificationDAO
                .findByAccount_AccountIDWithActorAndParentOrderByCreatedAtDesc(accountID);

        // Map of parentNotificationID -> clone (for quick lookup)
        Map<Integer, Notification> clonesByParent = userNotifications.stream()
                .filter(n -> n.getParentNotification() != null)
                .collect(Collectors.toMap(n -> n.getParentNotification().getNotificationID(), n -> n, (a, b) -> a));

        // Targeted notifications (directly addressed to user)
        List<Notification> targeted = userNotifications.stream()
                .filter(n -> n.getParentNotification() == null)
                .collect(Collectors.toList());

        // 2) Fetch global notifications
        List<Notification> globals = notificationDAO.findGlobalNotificationsOrderByCreatedAtDesc();

        // 3) For each global: prefer clone if exists, otherwise use global
        List<Notification> finalList = new java.util.ArrayList<>();
        finalList.addAll(targeted);

        for (Notification g : globals) {
            if (clonesByParent.containsKey(g.getNotificationID())) {
                finalList.add(clonesByParent.get(g.getNotificationID()));
            } else {
                finalList.add(g);
            }
        }

        // 4) Sort final list by createdAt desc
        finalList.sort((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()));

        List<Map<String, Object>> result = finalList.stream().map(n -> {
            Map<String, Object> map = new HashMap<>();
            map.put("notificationID", n.getNotificationID());
            map.put("title", n.getTitle());
            map.put("content", n.getContent());
            map.put("type", n.getType());
            map.put("isRead", n.getIsRead());
            map.put("isGlobal", n.getIsGlobal() != null ? n.getIsGlobal() : false);
            map.put("parentNotificationID",
                    n.getParentNotification() != null ? n.getParentNotification().getNotificationID() : null);
            map.put("createdAt", n.getCreatedAt() != null ? n.getCreatedAt().toString() : null);
            map.put("postID", n.getPost() != null ? n.getPost().getPostID() : 0);
            map.put("link", sanitizeLink(n)); // Can be null
            map.put("username", n.getActor() != null ? n.getActor().getUsername() : "Hệ thống");
            map.put("avatarUrl", n.getActor() != null ? n.getActor().getAvatar() : null);
            return map;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }

    // 🟡 USER: Đánh dấu 1 thông báo là đã đọc ────────────────────────────────
    @PutMapping("/{id}/read")
    @Transactional
    public ResponseEntity<?> markRead(@PathVariable Integer id) {
        var accountOpt = getCurrentAuthenticatedAccount();
        if (accountOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "User not found"));
        }
        var account = accountOpt.get();

        return notificationDAO.findById(id).map(n -> {
            // 🚨 CHỐT CHẶN CHỐNG NHÌN TRỘM (Tránh việc hacker tự ý đánh dấu đọc của người khác)
            // If target is user-specific, check owner. Global notifications don't have an owner in the base record.
            if (n.getAccount() != null && !n.getAccount().getAccountID().equals(account.getAccountID())) {
                 return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("message", "Từ chối truy cập: Bạn không có quyền thao tác trên thông báo này!"));
            }

            // If target is a global notification (no account) => clone-on-read
            if (Boolean.TRUE.equals(n.getIsGlobal()) || n.getAccount() == null) {
                var existingClone = notificationDAO
                        .findByParentNotification_NotificationIDAndAccount_AccountID(n.getNotificationID(),
                                account.getAccountID());
                if (existingClone.isPresent()) {
                    Notification clone = existingClone.get();
                    clone.setIsRead(1);
                    clone.setReadAt(LocalDateTime.now());
                    notificationDAO.save(clone);
                } else {
                    Notification clone = Notification.builder()
                            .title(n.getTitle())
                            .content(n.getContent())
                            .type(n.getType())
                            .actor(n.getActor())
                            .post(n.getPost())
                            .account(account)
                            .isRead(1)
                            .readAt(LocalDateTime.now())
                            .createdAt(n.getCreatedAt() != null ? n.getCreatedAt() : LocalDateTime.now())
                            .link(n.getLink())
                            .isGlobal(false)
                            .parentNotification(n)
                            .build();
                    notificationDAO.save(clone);
                }
                return ResponseEntity.ok(Map.of("message", "Đã đánh dấu là đã đọc (global cloned)"));
            } else {
                n.setIsRead(1);
                n.setReadAt(LocalDateTime.now());
                notificationDAO.save(n);
                return ResponseEntity.ok(Map.of("message", "Đã đánh dấu là đã đọc"));
            }
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

        // Mark existing user notifications as read
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

        // For global notifications: create clones marked as read for this user if not exists
        List<Notification> globals = notificationDAO.findGlobalNotificationsOrderByCreatedAtDesc();
        int created = 0;
        for (Notification g : globals) {
            var exists = notificationDAO.findByParentNotification_NotificationIDAndAccount_AccountID(
                    g.getNotificationID(), accountID);
            if (exists.isEmpty()) {
                Notification clone = Notification.builder()
                        .title(g.getTitle())
                        .content(g.getContent())
                        .type(g.getType())
                        .actor(g.getActor())
                        .post(g.getPost())
                        .account(null) // set below
                        .isRead(1)
                        .readAt(LocalDateTime.now())
                        .createdAt(g.getCreatedAt() != null ? g.getCreatedAt() : LocalDateTime.now())
                        .link(g.getLink())
                        .isGlobal(false)
                        .parentNotification(g)
                        .build();

                var accountOpt = accountDAO.findById(accountID);
                if (accountOpt.isPresent()) {
                    clone.setAccount(accountOpt.get());
                    notificationDAO.save(clone);
                    created++;
                }
            }
        }

        return ResponseEntity.ok(Map.of(
                "message", "Đã đánh dấu tất cả là đã đọc",
                "updatedCount", unread.size(),
                "createdGlobalClones", created));
    }

    // 🔥 ĐÃ XÓA HOÀN TOÀN HÀM XÓA THÔNG BÁO THEO CHỈ ĐẠO CỦA SẾP!
}
