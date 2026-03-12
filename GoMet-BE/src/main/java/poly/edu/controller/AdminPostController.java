package poly.edu.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import poly.edu.dto.AdminPostDTO;
import poly.edu.service.PostService;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/posts")
@RequiredArgsConstructor
@CrossOrigin
public class AdminPostController {

    private final PostService postService;

    // Tab Tất cả
    @GetMapping
    public ResponseEntity<Page<AdminPostDTO>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        int safePage = Math.max(0, page);
        int safeSize = Math.min(100, Math.max(1, size));
        Pageable pageable = PageRequest.of(safePage, safeSize);
        return ResponseEntity.ok(postService.findAll(pageable));
    }

    // Tab Đã duyệt / Chờ duyệt
    @GetMapping("/approved/{status}")
    public ResponseEntity<Page<AdminPostDTO>> getByApproved(
            @PathVariable Integer status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        int safePage = Math.max(0, page);
        int safeSize = Math.min(100, Math.max(1, size));
        Pageable pageable = PageRequest.of(safePage, safeSize);
        return ResponseEntity.ok(postService.findByApproved(status, pageable));
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
