package poly.edu.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import poly.edu.dao.NotificationDAO;
import poly.edu.dto.AdminNotificationDTO;
import poly.edu.entity.Notification;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * User-facing notification endpoints.
 * Endpoints:
 * GET /api/notifications/{accountID} - list notifications for a user
 * PUT /api/notifications/{id}/read - mark single notification as read
 * PUT /api/notifications/{accountID}/read-all - mark all as read for a user
 * DELETE /api/notifications/{id} - delete a notification
 */
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationDAO notificationDAO;

    // ── List notifications for user ──────────────────────────────────────
    @GetMapping("/{accountID}")
    @Transactional
    public ResponseEntity<List<Map<String, Object>>> getUserNotifications(
            @PathVariable Integer accountID) {

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
                    map.put("link", n.getLink()); // Can be null
                    map.put("username", n.getActor() != null ? n.getActor().getUsername() : "Hệ thống");
                    map.put("avatarUrl", n.getActor() != null ? n.getActor().getAvatar() : null);
                    return map;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }

    // ── Mark single notification as read ────────────────────────────────
    @PutMapping("/{id}/read")
    @Transactional
    public ResponseEntity<?> markRead(@PathVariable Integer id) {
        return notificationDAO.findById(id).map(n -> {
            n.setIsRead(1);
            n.setReadAt(LocalDateTime.now());
            notificationDAO.save(n);
            return ResponseEntity.ok(Map.of("message", "Marked as read"));
        }).orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", "Notification not found")));
    }

    // ── Mark ALL as read for a user ──────────────────────────────────────
    @PutMapping("/{accountID}/read-all")
    @Transactional
    public ResponseEntity<?> markAllRead(@PathVariable Integer accountID) {
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
                "message", "All notifications marked as read",
                "updatedCount", unread.size()));
    }

    // ── Delete a notification ────────────────────────────────────────────
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteNotification(@PathVariable Integer id) {
        if (!notificationDAO.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Notification not found"));
        }
        notificationDAO.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Notification deleted"));
    }
}
