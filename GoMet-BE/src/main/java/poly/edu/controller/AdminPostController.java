package poly.edu.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import poly.edu.dto.AdminPostDTO;
import poly.edu.service.PostService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/posts")
@RequiredArgsConstructor
@CrossOrigin
public class AdminPostController {

    private final PostService postService;

    // Tab Tất cả
    @GetMapping
    public ResponseEntity<List<AdminPostDTO>> getAll() {
        return ResponseEntity.ok(postService.findAll());
    }

    // Tab Đã duyệt / Chờ duyệt
    @GetMapping("/approved/{status}")
    public ResponseEntity<List<AdminPostDTO>> getByApproved(@PathVariable Integer status) {
        return ResponseEntity.ok(postService.findByApproved(status));
    }

    // Duyệt bài
    @PutMapping("/approve/{id}")
    public ResponseEntity<?> approve(@PathVariable Integer id) {
        postService.approvePost(id);
        return ResponseEntity.ok(Map.of("message", "Post approved"));
    }

    // Deactive bài
    @PutMapping("/deactive/{id}")
    public ResponseEntity<?> deactive(@PathVariable Integer id) {
        postService.deactivePost(id);
        return ResponseEntity.ok(Map.of("message", "Post deactivated"));
    }

    // Xóa bài
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        postService.deletePost(id);
        return ResponseEntity.ok(Map.of("message", "Post deleted"));
    }
}
