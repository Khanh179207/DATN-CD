package poly.edu.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import poly.edu.dto.LikesDTO;
import poly.edu.service.LikesService;

import java.util.List;

@RestController
@RequestMapping("/api/likes")
@RequiredArgsConstructor

public class LikesController {

    private final LikesService likesService;

    // 1. Thả tim hoặc Hủy thả tim (Toggle)
    // Gọi: POST /api/likes/toggle?accountId=1&postId=5
    @PostMapping("/toggle")
    public ResponseEntity<Boolean> toggleLike(
            @RequestParam Integer accountId,
            @RequestParam Integer postId) {
        boolean isLiked = likesService.toggleLike(accountId, postId);
        return ResponseEntity.ok(isLiked);
    }

    // 2. Lấy danh sách những người đã like bài viết
    // Gọi: GET /api/likes/post/5
    @GetMapping("/post/{postId}")
    public ResponseEntity<List<LikesDTO>> getLikesOfPost(@PathVariable Integer postId) {
        return ResponseEntity.ok(likesService.getLikesByPostId(postId));
    }

    // 3. Kiểm tra xem user hiện tại đã like bài này chưa (để load icon)
    // Gọi: GET /api/likes/check?accountId=1&postId=5
    @GetMapping("/check")
    public ResponseEntity<Boolean> checkLikeStatus(
            @RequestParam Integer accountId,
            @RequestParam Integer postId) {
        return ResponseEntity.ok(likesService.checkUserLikedPost(accountId, postId));
    }
}