package poly.edu.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import poly.edu.dto.AdminCommentDTO;
import poly.edu.service.CommentService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/comments") // ĐƯỜNG DẪN RIÊNG CHO ADMIN
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")

public class AdminCommentController {

    private final CommentService commentService;

    @GetMapping
    public ResponseEntity<List<AdminCommentDTO>> getAll() {
        return ResponseEntity.ok(commentService.findAll());
    }

    // 🔥 Admin Khóa/Xóa bình luận (Hứng Payload ghi Log)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id, @RequestBody(required = false) Map<String, Object> payload) {
        try {
            Integer adminId = 0;
            String adminName = "Hệ Thống";

            if (payload != null) {
                adminId = Integer.valueOf(payload.getOrDefault("adminId", 0).toString());
                adminName = (String) payload.getOrDefault("adminName", "Admin Ẩn Danh");
            }

            commentService.delete(id, adminId, adminName);
            return ResponseEntity.ok(Map.of("message", "Đã khóa bình luận vi phạm"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    // 🔥 Admin Khôi phục bình luận (Hứng Payload ghi Log)
    @PutMapping("/{id}/restore")
    public ResponseEntity<?> restore(@PathVariable Integer id, @RequestBody(required = false) Map<String, Object> payload) {
        try {
            Integer adminId = 0;
            String adminName = "Hệ Thống";

            if (payload != null) {
                adminId = Integer.valueOf(payload.getOrDefault("adminId", 0).toString());
                adminName = (String) payload.getOrDefault("adminName", "Admin Ẩn Danh");
            }

            commentService.restore(id, adminId, adminName);
            return ResponseEntity.ok(Map.of("message", "Đã khôi phục bình luận"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
}