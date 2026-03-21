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
@CrossOrigin("*")
public class CommentController {

    private final CommentService commentService;

    @GetMapping("/post/{postID}")
    public ResponseEntity<List<CommentDTO>> getByPost(
            @PathVariable Integer postID,
            @RequestParam(required = false) Integer currentAccountID) { // Nhận ID từ Query Param

        // Gọi Service và truyền cả 2 ID vào
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

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteComment(@PathVariable Integer id) {
        try {
            commentService.delete(id);
            return ResponseEntity.ok(Map.of("message", "Đã xóa bình luận"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", e.getMessage()));
        }
    }
}