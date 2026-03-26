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
@RequestMapping("/api/comments") // ĐƯỜNG DẪN CỦA USER BÌNH THƯỜNG
@RequiredArgsConstructor
@CrossOrigin("*")
public class CommentController {

    private final CommentService commentService;

    // Lấy danh sách bình luận trong chi tiết bài viết
    @GetMapping("/post/{postID}")
    public ResponseEntity<List<CommentDTO>> getByPost(
            @PathVariable Integer postID,
            @RequestParam(required = false) Integer currentAccountID) {
        return ResponseEntity.ok(commentService.getCommentsByPost(postID, currentAccountID));
    }

    // User thêm bình luận
    @PostMapping
    public ResponseEntity<?> addComment(@RequestBody CommentDTO req) {
        try {
            CommentDTO saved = commentService.saveNewComment(req);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    // Người dùng TỰ XÓA bình luận của chính mình (isActive = 0)
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