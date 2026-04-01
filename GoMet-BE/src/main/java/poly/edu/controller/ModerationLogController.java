package poly.edu.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; // 🔥 IMPORT THẺ BẢO VỆ ADMIN
import org.springframework.web.bind.annotation.*;
import poly.edu.dao.AccountDAO;
import poly.edu.entity.Account;
import poly.edu.entity.ModerationLog;
import poly.edu.service.ModerationLogService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/moderation-logs")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')") // 🔴 CHỐT CHẶN ĐỎ: Chỉ Admin mới được bước qua cánh cửa này!
public class ModerationLogController {

    private final ModerationLogService moderationLogService;
    private final AccountDAO accountDAO; // 🔥 Thêm AccountDAO để bốc Token tìm Admin
    private final poly.edu.util.JwtUtils jwtUtils;

    // 🔴 ADMIN ONLY: Xem lịch sử kiểm duyệt
    @GetMapping
    public ResponseEntity<List<ModerationLog>> getAll() {
        return ResponseEntity.ok(moderationLogService.getAllLogs());
    }

    // 🔴 ADMIN ONLY: Đẩy log kiểm duyệt lên
    // 🔥 MỞ CỬA POST CHO FRONTEND ĐẨY LOG LÊN
    @PostMapping
    public ResponseEntity<?> createLog(@RequestBody Map<String, Object> payload, HttpServletRequest request) {
        try {
            // 1. Tóm Admin từ Token
            Account admin = getAdminFromToken(request);

            // 2. Nhận dữ liệu từ Frontend gửi lên
            String action = (String) payload.get("action");
            String targetType = (String) payload.get("targetType");
            Integer targetId = Integer.valueOf(payload.get("targetID").toString());
            String reason = (String) payload.get("reason");

            // 3. Gọi hàm logAction "phát ăn luôn" của sếp
            moderationLogService.logAction(
                    targetId,
                    targetType,
                    action,
                    admin.getAccountID(),
                    admin.getUsername(),
                    reason
            );

            return ResponseEntity.ok(Map.of("success", true, "message", "Ghi log thành công"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    // Hàm nội bộ hỗ trợ bóc tách Token
    private Account getAdminFromToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Token không hợp lệ hoặc chưa đăng nhập!");
        }
        String token = authHeader.substring(7);

        // 1. Kiểm tra JWT có hợp lệ không
        if (!jwtUtils.validateJwtToken(token)) {
            throw new RuntimeException("Token đã hết hạn hoặc bị giả mạo!");
        }

        // 2. Lấy Email từ Token
        String email = jwtUtils.getEmailFromJwtToken(token);

        // 3. Tìm Admin bằng Email
        return accountDAO.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Admin với email này!"));
    }
}