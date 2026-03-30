package poly.edu.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import poly.edu.dto.CommentLikeRequest;   // Import đúng file mới
import poly.edu.dto.CommentLikeResponse;  // Import đúng file mới
import poly.edu.service.CommentLikeService;

@RestController
@RequestMapping("/api/comments")

@RequiredArgsConstructor
public class CommentLikeController {

    private final CommentLikeService commentLikeService;

    @PostMapping("/{commentId}/like")
    public ResponseEntity<CommentLikeResponse> toggleLike(
            @PathVariable Integer commentId,
            @RequestBody CommentLikeRequest requestDTO) {
        try {
            if (requestDTO.getAccountID() == null) {
                return ResponseEntity.badRequest().body(
                        CommentLikeResponse.builder().success(false).message("Thiếu AccountID").build()
                );
            }

            // Gọi Service xử lý logic
            boolean isLiked = commentLikeService.toggleLike(requestDTO.getAccountID(), commentId);

            return ResponseEntity.ok(
                    CommentLikeResponse.builder()
                            .success(true)
                            .isLiked(isLiked)
                            .message(isLiked ? "Đã thích" : "Đã bỏ thích")
                            .build()
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    CommentLikeResponse.builder().success(false).message(e.getMessage()).build()
            );
        }
    }
}