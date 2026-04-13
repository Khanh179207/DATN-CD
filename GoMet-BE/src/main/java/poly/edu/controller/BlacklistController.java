package poly.edu.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import poly.edu.entity.BlacklistWord;
import poly.edu.service.BlacklistService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/blacklist") // Đổi tên đường dẫn ngắn gọn dễ gọi
@RequiredArgsConstructor
@CrossOrigin("*")
public class BlacklistController {

    private final BlacklistService blacklistService;

    // ==========================================
    // API PUBLIC CHO FRONTEND GỌI ĐỂ KIỂM TRA
    // ==========================================
    @PostMapping("/check")
    public ResponseEntity<?> checkContentPolicy(@RequestBody Map<String, String> payload) {
        String content = payload.get("content");
        boolean hasBadWord = blacklistService.containsBadWord(content);
        return ResponseEntity.ok(Map.of("hasBadWord", hasBadWord));
    }

    // ==========================================
    // API DÀNH RIÊNG CHO ADMIN (Thêm, Sửa, Xóa)
    // ==========================================
    @GetMapping("/admin")
    public ResponseEntity<List<BlacklistWord>> getAll() {
        return ResponseEntity.ok(blacklistService.getAllWords());
    }

    @PostMapping("/admin")
    public ResponseEntity<?> addWord(@RequestBody Map<String, String> payload) {
        try {
            String word = payload.get("word");
            if (word == null || word.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("message", "Từ cấm không được để trống"));
            }
            return ResponseEntity.ok(blacklistService.addWord(word));
        } catch (Exception e) {
            // Lỗi trùng lặp từ cấm (Do set UNIQUE trong Database)
            return ResponseEntity.badRequest().body(Map.of("message", "Từ này đã tồn tại trong danh sách cấm!"));
        }
    }

    @DeleteMapping("/admin/{id}")
    public ResponseEntity<?> deleteWord(@PathVariable Integer id) {
        try {
            blacklistService.deleteWord(id);
            return ResponseEntity.ok(Map.of("message", "Đã xóa từ cấm thành công"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", "Lỗi khi xóa từ cấm!"));
        }
    }
}