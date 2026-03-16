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
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * User-facing notification endpoints.
 * Endpoints:
 *   GET  /api/notifications/{accountID}          - list notifications for a user
 *   PUT  /api/notifications/{id}/read            - mark single notification as read
 *   PUT  /api/notifications/{accountID}/read-all - mark all as read for a user
 *   DELETE /api/notifications/{id}               - delete a notification
 */
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationDAO notificationDAO;

    // ── List notifications for user ──────────────────────────────────────
    @GetMapping("/{accountID}")
    public ResponseEntity<List<Map<String, Object>>> getUserNotifications(
            @PathVariable Integer accountID) {

        List<Map<String, Object>> result = notificationDAO
                .findByAccount_AccountID(accountID)
                .stream()
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .map(n -> Map.<String, Object>of(
                        "notificationID", n.getNotificationID(),
                        "title",          n.getTitle(),
                        "content",        n.getContent(),
                        "type",           n.getType(),
                        "isRead",         n.getIsRead(),
                        "createdAt",      n.getCreatedAt().toString(),
                        "postID",         n.getPost() != null ? n.getPost().getPostID() : 0
                ))
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
                "updatedCount", unread.size()
        ));
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
