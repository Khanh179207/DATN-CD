package poly.edu.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import poly.edu.dto.AdminPostDTO;
import poly.edu.service.AdminPostService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/posts")
@RequiredArgsConstructor
@CrossOrigin
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

    // Duyệt bài
    @PutMapping("/approve/{id}")
    public ResponseEntity<?> approve(@PathVariable Integer id) {
        adminpostService.approvePost(id);
        return ResponseEntity.ok(Map.of("message", "Post approved"));
    }

    // Từ chối bài
    @PutMapping("/{id}/reject")
    public ResponseEntity<?> reject(@PathVariable Integer id, @RequestBody Map<String, String> body) {
        String reason = body.get("reason");
        adminpostService.rejectPost(id, reason);
        return ResponseEntity.ok(Map.of("message", "Post rejected"));
    }

    // Deactive bài
    @PutMapping("/deactive/{id}")
    public ResponseEntity<?> deactive(@PathVariable Integer id) {
        adminpostService.deactivePost(id);
        return ResponseEntity.ok(Map.of("message", "Post deactivated"));
    }

    // Xóa bài
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
