package poly.edu.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; // 🔥 IMPORT THẺ BẢO VỆ
import org.springframework.web.bind.annotation.*;
import poly.edu.service.BlacklistService;

import java.util.Map;

@RestController
@RequestMapping("/api/blacklist") // 🔥 Đường dẫn cho User/Frontend sử dụng
@RequiredArgsConstructor
public class PublicBlacklistController {

    private final BlacklistService blacklistService;

    // 🟡 USER/ADMIN: Bắt buộc đăng nhập mới được sử dụng tính năng kiểm tra nội dung
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/check")
    public ResponseEntity<?> checkContentPolicy(@RequestBody Map<String, String> payload) {
        String content = payload.get("content");

        if (content == null || content.isBlank()) {
            return ResponseEntity.ok(Map.of("hasBadWord", false));
        }

        // Gọi hàm quét siêu tốc độ từ Cache RAM
        boolean hasBadWord = blacklistService.containsBadWord(content);

        return ResponseEntity.ok(Map.of("hasBadWord", hasBadWord));
    }
}