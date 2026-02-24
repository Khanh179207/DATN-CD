package poly.edu.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import poly.edu.dto.AdminNotificationDTO;
import poly.edu.service.AdminNotificationService;

@RestController
@RequestMapping("/api/admin/notifications")
@RequiredArgsConstructor
@CrossOrigin
public class AdminNotificationController {

    private final AdminNotificationService adminNotificationService;

    // Gửi cho tất cả user
    @PostMapping("/all")
    public void sendAll(@RequestBody AdminNotificationDTO dto) {
        adminNotificationService.sendToAll(dto);
    }

    // Gửi cho 1 user
    @PostMapping("/user/{accountID}")
    public void sendOne(@PathVariable Integer accountID,
                        @RequestBody AdminNotificationDTO dto) {
        adminNotificationService.sendToOne(accountID, dto);
    }

    // Xóa thông báo
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        adminNotificationService.delete(id);
    }
}

