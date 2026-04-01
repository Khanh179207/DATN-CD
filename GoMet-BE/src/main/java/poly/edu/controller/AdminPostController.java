package poly.edu.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import poly.edu.dto.AdminPostDTO;
import poly.edu.service.AdminPostService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/posts")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")

public class AdminPostController {

    private final AdminPostService adminpostService;

    // Tab Tất cả
    @GetMapping
    public ResponseEntity<List<AdminPostDTO>> getAll() {
        return ResponseEntity.ok(adminpostService.findAll());
    }

    // Tab Đã duyệt / Chờ duyệt
    @GetMapping("/approved/{status}")
    public ResponseEntity<List<AdminPostDTO>> getByApproved(@PathVariable Integer status) {
        return ResponseEntity.ok(adminpostService.findByApproved(status));
    }

    // 🔥 DUYỆT BÀI (Hứng Payload chứa thông tin Admin)
    @PutMapping("/approve/{id}")
    public ResponseEntity<?> approve(@PathVariable Integer id, @RequestBody(required = false) Map<String, Object> payload) {
        try {
            Integer adminId = 0;
            String adminName = "Hệ Thống";

            if (payload != null) {
                adminId = Integer.valueOf(payload.getOrDefault("adminId", 0).toString());
                adminName = (String) payload.getOrDefault("adminName", "Admin Ẩn Danh");
            }

            adminpostService.approvePost(id, adminId, adminName);
            return ResponseEntity.ok(Map.of("message", "Đã duyệt bài viết thành công!"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", "Lỗi: " + e.getMessage()));
        }
    }

    // 🔥 TỪ CHỐI / ẨN BÀI (Hứng Payload chứa Lý do và thông tin Admin)
    @PutMapping("/{id}/reject")
    public ResponseEntity<?> reject(@PathVariable Integer id, @RequestBody(required = false) Map<String, Object> payload) {
        try {
            Integer adminId = 0;
            String adminName = "Hệ Thống";
            String reason = "Vi phạm tiêu chuẩn cộng đồng";

            if (payload != null) {
                adminId = Integer.valueOf(payload.getOrDefault("adminId", 0).toString());
                adminName = (String) payload.getOrDefault("adminName", "Admin Ẩn Danh");
                reason = (String) payload.getOrDefault("reason", reason);
            }

            adminpostService.rejectPost(id, adminId, adminName, reason);
            return ResponseEntity.ok(Map.of("message", "Đã từ chối bài viết thành công!"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", "Lỗi: " + e.getMessage()));
        }
    }

    // Deactive bài
    @PutMapping("/deactive/{id}")
    public ResponseEntity<?> deactive(@PathVariable Integer id) {
        adminpostService.deactivePost(id);
        return ResponseEntity.ok(Map.of("message", "Post deactivated"));
    }

    // Xóa bài (Đã đổi thành Xóa Mềm bên trong Service)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        try {
            adminpostService.deletePost(id);
            return ResponseEntity.ok(Map.of("message", "Đã trảm mềm bài viết!"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{postId}/ban-author")
    public ResponseEntity<?> banAuthor(@PathVariable Integer postId) {
        try {
            adminpostService.banAuthorByPostId(postId);
            return ResponseEntity.ok(Map.of("message", "Tài khoản tác giả đã bị khóa!"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}