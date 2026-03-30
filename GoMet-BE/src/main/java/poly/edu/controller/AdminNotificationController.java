package poly.edu.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import poly.edu.dao.NotificationDAO;
import poly.edu.dto.AdminNotificationDTO;
import poly.edu.entity.Notification;
import poly.edu.service.AdminNotificationService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/notifications")
@RequiredArgsConstructor

public class AdminNotificationController {

    private final AdminNotificationService adminNotificationService;
    private final NotificationDAO notificationDAO;

    // GET all notifications (for admin list)
    @GetMapping
    public ResponseEntity<List<Notification>> getAll() {
        return ResponseEntity.ok(notificationDAO.findByTypeOrderByCreatedAtDesc("ADMIN_MANUAL"));
    }

    // Gửi cho tất cả user
    @PostMapping("/all")
    public ResponseEntity<?> sendAll(@RequestBody AdminNotificationDTO dto) {
        adminNotificationService.sendToAll(dto);
        return ResponseEntity.ok(Map.of("message", "Sent to all users"));
    }

    // Gửi cho 1 user
    @PostMapping("/user/{accountID}")
    public ResponseEntity<?> sendOne(@PathVariable Integer accountID,
            @RequestBody AdminNotificationDTO dto) {
        adminNotificationService.sendToOne(accountID, dto);
        return ResponseEntity.ok(Map.of("message", "Sent to user " + accountID));
    }

    // Xóa thông báo
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        adminNotificationService.delete(id);
        return ResponseEntity.ok(Map.of("message", "Deleted"));
    }
}
