package poly.edu.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; // 🔥 IMPORT THẺ BẢO VỆ
import org.springframework.web.bind.annotation.*;
import poly.edu.dto.CommentDTO;
import poly.edu.service.CommentService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
// 🟢 KHÔNG ĐẶT KHÓA Ở CLASS: Để khách vãng lai còn lướt xem bình luận được
public class CommentController {

    private final CommentService commentService;

    // 🟢 PUBLIC: Mở toang cửa cho tất cả mọi người vào xem bình luận
    @GetMapping("/post/{postID}")
    public ResponseEntity<List<CommentDTO>> getByPost(
            @PathVariable Integer postID,
            @RequestParam(required = false) Integer currentAccountID) {
        return ResponseEntity.ok(commentService.getCommentsByPost(postID, currentAccountID));
    }

    // 🟡 USER: Phải đăng nhập (có Token) mới được đăng bình luận
    @PreAuthorize("isAuthenticated()")
    @PostMapping
    public ResponseEntity<?> addComment(@RequestBody CommentDTO req) {
        try {
            CommentDTO saved = commentService.saveNewComment(req);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    // 🟡 USER: Phải đăng nhập mới được xóa bình luận (Service sẽ tự check xem có phải chủ Cmt không)
    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/user/{id}")
    public ResponseEntity<?> deleteCommentByUser(
            @PathVariable Integer id,
            @RequestParam Integer currentAccountID) {
        try {
            commentService.deleteByUser(id, currentAccountID);
            return ResponseEntity.ok(Map.of("message", "Đã xóa bình luận của bạn"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    // 🔴 ADMIN ONLY: Vùng cấm, chỉ Admin mới có quyền xóa bình luận của người khác
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCommentByAdmin(
            @PathVariable Integer id,
            @RequestParam Integer adminId,
            @RequestParam String adminName) {
        try {
            // Gọi vào hàm delete() xịn xò trong CommentServiceImpl của sếp
            commentService.delete(id, adminId, adminName);
            return ResponseEntity.ok(Map.of("message", "Admin đã xóa bình luận thành công"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
}