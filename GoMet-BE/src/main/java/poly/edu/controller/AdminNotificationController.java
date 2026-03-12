package poly.edu.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
@CrossOrigin
public class AdminNotificationController {

    private final AdminNotificationService adminNotificationService;
    private final NotificationDAO notificationDAO;

    // GET all notifications with optional pagination
    @GetMapping
    public ResponseEntity<?> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        if (page == 0 && size >= 1000) {
            // Legacy: return all (keep backward compat for simple calls)
            List<AdminNotificationDTO> notifications = notificationDAO.findAll(Sort.by(Sort.Direction.DESC, "createdAt"))
                .stream()
                .map(this::toDTO)
                .toList();
            return ResponseEntity.ok(notifications);
        }
        Page<Notification> result = notificationDAO.findAll(
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")));
        return ResponseEntity.ok(Map.of(
            "content", result.getContent().stream().map(this::toDTO).toList(),
                "totalElements", result.getTotalElements(),
                "totalPages", result.getTotalPages(),
                "page", page
        ));
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

    private AdminNotificationDTO toDTO(Notification notification) {
        AdminNotificationDTO dto = new AdminNotificationDTO();
        dto.setNotificationID(notification.getNotificationID());
        dto.setTitle(notification.getTitle());
        dto.setContent(notification.getContent());
        dto.setType(notification.getType());
        dto.setIsRead(notification.getIsRead());
        dto.setReadAt(notification.getReadAt());
        dto.setCreatedAt(notification.getCreatedAt());
        if (notification.getAccount() != null) {
            dto.setAccountID(notification.getAccount().getAccountID());
            dto.setAccountName(notification.getAccount().getUsername());
        }
        if (notification.getPost() != null) {
            dto.setPostID(notification.getPost().getPostID());
        }
        return dto;
    }
}

