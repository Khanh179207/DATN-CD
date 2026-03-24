package poly.edu.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import poly.edu.dto.AdminCommentDTO;
import poly.edu.dto.CommentDTO;
import poly.edu.service.CommentService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
@CrossOrigin("*")
public class CommentController {

    private final CommentService commentService;

    // =======================================================
    // 1. API TRANG ADMIN
    // =======================================================
    @GetMapping("/admin/all")
    public ResponseEntity<List<AdminCommentDTO>> getAllCommentsForAdmin() {
        try {
            return ResponseEntity.ok(commentService.findAll());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Admin Khóa bình luận (isActive = -1)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCommentByAdmin(@PathVariable Integer id) {
        try {
            commentService.delete(id);
            return ResponseEntity.ok(Map.of("message", "Đã khóa bình luận vi phạm"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    // Admin Khôi phục bình luận (isActive = 1)
    @PutMapping("/{id}/restore")
    public ResponseEntity<?> restoreComment(@PathVariable Integer id) {
        try {
            commentService.restore(id);
            return ResponseEntity.ok(Map.of("message", "Đã khôi phục bình luận"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    // =======================================================
    // 2. API TRANG NGƯỜI DÙNG (POST DETAIL)
    // =======================================================
    @GetMapping("/post/{postID}")
    public ResponseEntity<List<CommentDTO>> getByPost(
            @PathVariable Integer postID,
            @RequestParam(required = false) Integer currentAccountID) {
        return ResponseEntity.ok(commentService.getCommentsByPost(postID, currentAccountID));
    }

    @PostMapping
    public ResponseEntity<?> addComment(@RequestBody CommentDTO req) {
        try {
            CommentDTO saved = commentService.saveNewComment(req);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    // Người dùng tự xóa bình luận của mình (isActive = 0)
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
}