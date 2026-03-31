package poly.edu.controller;

import poly.edu.dao.AccountDAO;
import poly.edu.dto.AppealDTO;
import poly.edu.entity.Account;
import poly.edu.service.AppealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; // 🔥 Import thẻ bảo vệ
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class AppealController {

    @Autowired
    private AppealService appealService;

    @Autowired
    private AccountDAO accountDAO;

    @Autowired
    private poly.edu.util.JwtUtils jwtUtils;

    // 🟢 PUBLIC: Mở toang cho User bị ban gửi đơn khiếu nại (Không cần Token)
    @PostMapping("/appeals")
    public ResponseEntity<?> createAppeal(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            String reason = request.get("reason");
            if (email == null || email.trim().isEmpty() || reason == null || reason.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Email và lý do không được bỏ trống"));
            }
            AppealDTO appeal = appealService.createAppeal(email.trim(), reason.trim());
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("success", true, "message", "Thành công", "data", appeal));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    // 🔴 ADMIN ONLY: Chỉ Admin mới được xem danh sách khiếu nại
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/appeals")
    public ResponseEntity<?> getAllAppeals() {
        return ResponseEntity.ok(Map.of("success", true, "data", appealService.getAllAppeals()));
    }

    // 🔴 ADMIN ONLY: Chỉ Admin mới được duyệt đơn khiếu nại
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/admin/appeals/{appealID}")
    public ResponseEntity<?> updateAppeal(
            @PathVariable Integer appealID,
            @RequestBody Map<String, String> request,
            HttpServletRequest httpRequest) {
        try {
            String status = request.get("status");
            String note = request.get("note");

            // 🔥 Lấy Admin thực tế từ Token trong Database (Dựa theo AuthController)
            Account admin = getAdminFromToken(httpRequest);

            AppealDTO updated = appealService.updateAppeal(
                    appealID,
                    status.trim(),
                    note != null ? note.trim() : "",
                    admin.getAccountID(),
                    admin.getUsername()
            );

            return ResponseEntity.ok(Map.of("success", true, "message", "Cập nhật thành công", "data", updated));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    // 🔴 ADMIN ONLY: Chỉ Admin mới được gỡ ban từ đơn khiếu nại
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/appeals/{appealID}/unban")
    public ResponseEntity<?> unbanAccount(@PathVariable Integer appealID, HttpServletRequest httpRequest) {
        try {
            // 🔥 Lấy Admin thực tế từ Token trong Database (Dựa theo AuthController)
            Account admin = getAdminFromToken(httpRequest);

            boolean result = appealService.unbanAccountByAppeal(appealID, admin.getAccountID(), admin.getUsername());

            return ResponseEntity.ok(Map.of("success", true, "message", "Gỡ ban thành công", "data", result));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    // 🔥 HÀM TRÍCH XUẤT ADMIN MỚI DÙNG JWT (Hàm nội bộ, không cần bảo vệ)
    private Account getAdminFromToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Lỗi Xác thực: Bạn chưa đăng nhập hoặc Token đã hết hạn!");
        }

        String token = authHeader.substring(7);

        if (!jwtUtils.validateJwtToken(token)) {
            throw new RuntimeException("Token không hợp lệ hoặc đã bị chỉnh sửa!");
        }

        String email = jwtUtils.getEmailFromJwtToken(token);

        return accountDAO.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Tài khoản không tồn tại!"));
    }
}