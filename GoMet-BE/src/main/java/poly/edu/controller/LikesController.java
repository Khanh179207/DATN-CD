package poly.edu.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; // 🔥 IMPORT THẺ BẢO VỆ
import org.springframework.web.bind.annotation.*;
import poly.edu.dao.InteractionLogDAO;
import poly.edu.dto.LikesDTO;
import poly.edu.entity.InteractionLog;
import poly.edu.service.LikesService;
import java.util.List;

@RestController
@RequestMapping("/api/likes")
@RequiredArgsConstructor
// 🟢 KHÔNG ĐẶT KHÓA Ở CLASS: Để khách vãng lai còn xem được số lượt like
public class LikesController {

    private final LikesService likesService;
    private final InteractionLogDAO interactionLogDAO; // 🔥 GIỮ LẠI TỪ NHÁNH CỦA SẾP

    // 🟡 USER: Bắt buộc đăng nhập mới được thả tim hoặc hủy tim
    @PreAuthorize("isAuthenticated()") // 🔥 CHỐT CHẶN VÀNG TỪ NHÁNH KIA
    // Gọi: POST /api/likes/toggle?accountId=1&postId=5
    @PostMapping("/toggle")
    public ResponseEntity<Boolean> toggleLike(
            @RequestParam Integer accountId,
            @RequestParam Integer postId) {
        boolean isLiked = likesService.toggleLike(accountId, postId);

        try {
            if (isLiked) {
                interactionLogDAO.save(new InteractionLog(postId, "LIKE", 1));
            } else {
                interactionLogDAO.save(new InteractionLog(postId, "LIKE", -1));
            }
        } catch (Exception e) {
            System.err.println("Lỗi lưu InteractionLog LIKE: " + e.getMessage());
        }

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