package poly.edu.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; // 🔥 IMPORT THẺ BẢO VỆ
import org.springframework.web.bind.annotation.*;
import poly.edu.dto.LikesDTO;
import poly.edu.service.LikesService;

import java.util.List;

@RestController
@RequestMapping("/api/likes")
@RequiredArgsConstructor
// 🟢 KHÔNG ĐẶT KHÓA Ở CLASS: Để khách vãng lai còn xem được số lượt like
public class LikesController {

    private final LikesService likesService;

    // 🟡 USER: Bắt buộc đăng nhập mới được thả tim hoặc hủy tim
    @PreAuthorize("isAuthenticated()") // 🔥 CHỐT CHẶN VÀNG
    // Gọi: POST /api/likes/toggle?accountId=1&postId=5
    @PostMapping("/toggle")
    public ResponseEntity<Boolean> toggleLike(
            @RequestParam Integer accountId,
            @RequestParam Integer postId) {
        boolean isLiked = likesService.toggleLike(accountId, postId);
        return ResponseEntity.ok(isLiked);
    }

    // 🟢 PUBLIC: Mở toang cho ai cũng xem được danh sách người đã like
    // Gọi: GET /api/likes/post/5
    @GetMapping("/post/{postId}")
    public ResponseEntity<List<LikesDTO>> getLikesOfPost(@PathVariable Integer postId) {
        return ResponseEntity.ok(likesService.getLikesByPostId(postId));
    }

    // 🟡 USER: Bắt buộc đăng nhập mới check được trạng thái tim của chính mình
    @PreAuthorize("isAuthenticated()") // 🔥 CHỐT CHẶN VÀNG
    // Gọi: GET /api/likes/check?accountId=1&postId=5
    @GetMapping("/check")
    public ResponseEntity<Boolean> checkLikeStatus(
            @RequestParam Integer accountId,
            @RequestParam Integer postId) {
        return ResponseEntity.ok(likesService.checkUserLikedPost(accountId, postId));
    }
}