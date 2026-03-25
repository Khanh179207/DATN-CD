package poly.edu.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import poly.edu.dto.AdminAccountDTO;
import poly.edu.service.AccountService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/accounts")
@RequiredArgsConstructor
@CrossOrigin
public class AdminAccountController {

    private final AccountService accountService;

    @GetMapping
    public List<AdminAccountDTO> getAll() {
        return accountService.findAll();
    }

    @GetMapping("/{id}")
    public AdminAccountDTO getOne(@PathVariable Integer id) {
        return accountService.findById(id);
    }

    @PostMapping
    public AdminAccountDTO create(@RequestBody AdminAccountDTO dto) {
        return accountService.save(dto);
    }

    @PutMapping("/{id}")
    public AdminAccountDTO update(@PathVariable Integer id,
                                  @RequestBody AdminAccountDTO dto) {
        dto.setAccountID(id);
        return accountService.save(dto);
    }

    /** * 🔥 KHÓA TÀI KHOẢN (KÈM LÝ DO & THÔNG TIN ADMIN)
     * Payload: { "adminId": 1, "adminName": "admin", "adminEmail": "admin@gmail.com", "reason": "Spam" }
     */
    @PatchMapping("/{id}/ban")
    public ResponseEntity<?> ban(@PathVariable Integer id, @RequestBody Map<String, Object> payload) {
        try {
            Integer adminId = Integer.valueOf(payload.getOrDefault("adminId", 0).toString());
            String adminName = (String) payload.getOrDefault("adminName", "Admin Ẩn Danh");
            String adminEmail = (String) payload.getOrDefault("adminEmail", "Không có Email");
            String reason = (String) payload.getOrDefault("reason", "Không có lý do");

            // 🔥 Gọi sang Service với ĐỦ 5 tham số
            accountService.ban(id, adminId, adminName, adminEmail, reason);
            return ResponseEntity.ok(Map.of("message", "Đã khóa tài khoản và lưu nhật ký thành công!"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", "Lỗi: " + e.getMessage()));
        }
    }

    /** * 🔥 MỞ KHÓA TÀI KHOẢN
     */
    @PatchMapping("/{id}/unban")
    public ResponseEntity<?> unban(@PathVariable Integer id) {
        accountService.unban(id);
        return ResponseEntity.ok(Map.of("message", "Đã mở khóa tài khoản thành công!"));
    }

    @DeleteMapping("/{id}")
    public void softDelete(@PathVariable Integer id) {
        accountService.delete(id);
    }

    @DeleteMapping("/hard/{id}")
    public void hardDelete(@PathVariable Integer id) {
        accountService.hardDelete(id);
    }
}