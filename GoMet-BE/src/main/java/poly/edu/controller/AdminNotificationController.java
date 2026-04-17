package poly.edu.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import poly.edu.dao.AccountDAO;
import poly.edu.dao.NotificationDAO;
import poly.edu.dto.AdminNotificationDetailDTO;
import poly.edu.dto.AdminNotificationDTO;
import poly.edu.dto.AdminNotificationReaderDTO;
import poly.edu.dto.AdminNotificationSummaryDTO;
import poly.edu.entity.Account;
import poly.edu.entity.Notification;
import poly.edu.service.AdminNotificationService;
import poly.edu.util.JwtUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/notifications")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminNotificationController {

    private final AdminNotificationService adminNotificationService;
    private final NotificationDAO notificationDAO;
    private final AccountDAO accountDAO;
    private final JwtUtils jwtUtils;
    private final HttpServletRequest request;

    private Optional<Account> getCurrentAuthenticatedAccount() {
        var authentication = org.springframework.security.core.context.SecurityContextHolder
                .getContext()
                .getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            String principalName = authentication.getName();
            if (principalName != null && !principalName.isBlank()) {
                return accountDAO.findByEmail(principalName)
                        .or(() -> accountDAO.findByUsername(principalName));
            }
        }
        return Optional.empty();
    }

    // GET all notifications (for admin list)
    @GetMapping
    public ResponseEntity<List<AdminNotificationSummaryDTO>> getAll() {
        // Return admin notifications but exclude cloned records to avoid duplicates
        List<AdminNotificationSummaryDTO> notifications = notificationDAO
                .findAdminNotificationsByTypeExcludingClones("ADMIN_MANUAL")
                .stream()
                .map(this::toSummaryDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getDetail(@PathVariable Integer id) {
        return notificationDAO.findByIdWithAccountActorAndPost(id)
                .map(notification -> {
                    if (notification.getParentNotification() != null ||
                            !"ADMIN_MANUAL".equalsIgnoreCase(notification.getType())) {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .body(Map.of("message", "Notification not found"));
                    }

                    List<AdminNotificationReaderDTO> readers = new ArrayList<>();

                    if (Boolean.TRUE.equals(notification.getIsGlobal()) || notification.getAccount() == null) {
                        readers = notificationDAO.findReadClonesByParentNotificationId(notification.getNotificationID())
                                .stream()
                                .map(this::toReaderDTO)
                                .filter(reader -> reader.getAccountID() != null)
                                .collect(Collectors.toList());
                    } else if (notification.getIsRead() != null && notification.getIsRead() == 1) {
                        readers.add(toReaderDTO(notification));
                    }

                    return ResponseEntity.ok(AdminNotificationDetailDTO.builder()
                            .notificationID(notification.getNotificationID())
                            .title(notification.getTitle())
                            .content(notification.getContent())
                            .type(notification.getType())
                            .createdAt(notification.getCreatedAt())
                            .isGlobal(notification.getIsGlobal() != null ? notification.getIsGlobal() : false)
                            .link(notification.getLink())
                            .accountID(notification.getAccount() != null ? notification.getAccount().getAccountID() : null)
                            .recipientUsername(notification.getAccount() != null ? notification.getAccount().getUsername() : null)
                            .recipientEmail(notification.getAccount() != null ? notification.getAccount().getEmail() : null)
                            .recipientAvatar(notification.getAccount() != null ? notification.getAccount().getAvatar() : null)
                            .readCount(readers.size())
                            .readers(readers)
                            .build());
                })
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "Notification not found")));
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

    private AdminNotificationSummaryDTO toSummaryDTO(Notification notification) {
        return AdminNotificationSummaryDTO.builder()
                .notificationID(notification.getNotificationID())
                .title(notification.getTitle())
                .content(notification.getContent())
                .type(notification.getType())
                .createdAt(notification.getCreatedAt())
                .isGlobal(notification.getIsGlobal() != null ? notification.getIsGlobal() : false)
                .link(notification.getLink())
                .accountID(notification.getAccount() != null ? notification.getAccount().getAccountID() : null)
                .recipientUsername(notification.getAccount() != null ? notification.getAccount().getUsername() : null)
                .build();
    }

    private AdminNotificationReaderDTO toReaderDTO(Notification notification) {
        Account account = notification.getAccount();
        return AdminNotificationReaderDTO.builder()
                .accountID(account != null ? account.getAccountID() : null)
                .username(account != null ? account.getUsername() : null)
                .email(account != null ? account.getEmail() : null)
                .avatar(account != null ? account.getAvatar() : null)
                .readAt(notification.getReadAt())
                .build();
    }
}
