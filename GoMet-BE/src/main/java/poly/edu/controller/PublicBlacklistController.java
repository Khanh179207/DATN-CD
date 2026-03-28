package poly.edu.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import poly.edu.service.BlacklistService;

import java.util.Map;

@RestController
@RequestMapping("/api/blacklist") // 🔥 Đường dẫn Public không có chữ /admin
@RequiredArgsConstructor
@CrossOrigin("*")
public class PublicBlacklistController {

    private final BlacklistService blacklistService;

    @PostMapping("/check")
    public ResponseEntity<?> checkContentPolicy(@RequestBody Map<String, String> payload) {
        String content = payload.get("content");

        // Gọi hàm quét siêu tốc độ từ Cache RAM
        boolean hasBadWord = blacklistService.containsBadWord(content);

        return ResponseEntity.ok(Map.of("hasBadWord", hasBadWord));
    }
}