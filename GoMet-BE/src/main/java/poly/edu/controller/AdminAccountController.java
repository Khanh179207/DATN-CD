package poly.edu.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import poly.edu.dto.AdminAccountDTO;
import poly.edu.service.AccountService;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/admin/accounts")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")

public class AdminAccountController {

    private final AccountService accountService;

    @GetMapping
    public ResponseEntity<List<AdminAccountDTO>> getAll() {
        return ResponseEntity.ok(accountService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AdminAccountDTO> getOne(@PathVariable Integer id) {
        return ResponseEntity.ok(accountService.findById(id));
    }

    @PostMapping
    public ResponseEntity<AdminAccountDTO> create(@RequestBody AdminAccountDTO dto) {
        return ResponseEntity.ok(accountService.save(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AdminAccountDTO> update(@PathVariable Integer id, @RequestBody AdminAccountDTO dto) {
        dto.setAccountID(id);
        return ResponseEntity.ok(accountService.save(dto));
    }

    /**
     * 🔥 KHÓA TÀI KHOẢN (KÈM LÝ DO & NHẬT KÝ ADMIN)
     */
    @PatchMapping("/{id}/ban")
    public ResponseEntity<?> ban(@PathVariable Integer id, @RequestBody Map<String, Object> payload) {
        try {
            // Xử lý ép kiểu ID Admin an toàn (tránh lỗi null hoặc string)
            Integer adminId = parseInteger(payload.get("adminId"), 0);
            String adminName = (String) payload.getOrDefault("adminName", "Admin Ẩn Danh");
            String adminEmail = (String) payload.getOrDefault("adminEmail", "No Email");
            String reason = (String) payload.getOrDefault("reason", "Vi phạm tiêu chuẩn cộng đồng");

            log.info("Admin {} đang khóa tài khoản ID: {}", adminName, id);

            accountService.ban(id, adminId, adminName, adminEmail, reason);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Đã khóa tài khoản và lưu nhật ký thành công!"
            ));
        } catch (Exception e) {
            log.error("Lỗi khi ban account: ", e);
            return ResponseEntity.internalServerError().body(Map.of(
                    "success", false,
                    "message", "Lỗi hệ thống: " + e.getMessage()
            ));
        }
    }

    /**
     * 🔓 MỞ KHÓA TÀI KHOẢN
     */
    @PatchMapping("/{id}/unban")
    public ResponseEntity<?> unban(@PathVariable Integer id, @RequestBody(required = false) Map<String, Object> payload) {
        try {
            Integer adminId = 0;
            String adminName = "Hệ Thống";

            if (payload != null) {
                adminId = parseInteger(payload.get("adminId"), 0);
                adminName = (String) payload.getOrDefault("adminName", "Admin Ẩn Danh");
            }

            accountService.unban(id, adminId, adminName);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Đã mở khóa tài khoản thành công!"
            ));
        } catch (Exception e) {
            log.error("Lỗi khi unban account: ", e);
            return ResponseEntity.internalServerError().body(Map.of(
                    "success", false,
                    "message", "Lỗi hệ thống: " + e.getMessage()
            ));
        }
    }

    /**
     * 🗑️ XÓA TẠM (SOFT DELETE) - Chỉ thay đổi trạng thái IsDeleted
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> softDelete(@PathVariable Integer id) {
        try {
            accountService.delete(id);
            return ResponseEntity.ok(Map.of("message", "Đã xóa tạm tài khoản ID: " + id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    // --- HELPER PHƯƠNG THỨC ÉP KIỂU ---
    private Integer parseInteger(Object value, Integer defaultValue) {
        if (value == null) return defaultValue;
        try {
            if (value instanceof Number) return ((Number) value).intValue();
            return Integer.parseInt(value.toString());
        } catch (Exception e) {
            return defaultValue;
        }
    }

    // 🔥 ĐÃ XÓA HÀM HARD DELETE THEO YÊU CẦU CỦA SẾP.
    // Dữ liệu bây giờ an toàn tuyệt đối, không sợ bay màu khỏi SQL.
}