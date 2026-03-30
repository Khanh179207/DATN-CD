package poly.edu.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import poly.edu.dto.CommentDTO;
import poly.edu.service.CommentService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor

public class CommentController {

    private final CommentService commentService;

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

    // API: User tự xóa bình luận của chính mình
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

    // 🔥 THÊM API MỚI NÀY VÀO: DÀNH CHO ADMIN XÓA BÌNH LUẬN
    // API Frontend gọi: DELETE /api/comments/27?adminId=1&adminName=NamAdmin
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