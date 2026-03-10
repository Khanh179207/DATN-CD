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

    // Deactive bài
    @PutMapping("/deactive/{id}")
    public ResponseEntity<?> deactive(@PathVariable Integer id) {
        adminpostService.deactivePost(id);
        return ResponseEntity.ok(Map.of("message", "Post deactivated"));
    }

    // Xóa bài
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        adminpostService.deletePost(id);
        return ResponseEntity.ok(Map.of("message", "Post deleted"));
    }
}
