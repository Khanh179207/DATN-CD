package poly.edu.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; // 🔥 1. Nhớ import cái này sếp nhé
import org.springframework.web.bind.annotation.*;
import poly.edu.entity.BlacklistWord;
import poly.edu.service.BlacklistService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/blacklist") // 🔥 Đường dẫn cũ của Frontend Admin
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')") // 🔥 2. ĐẶT CHỐT TRẠM ADMIN TẠI ĐÂY
public class AdminBlacklistController {

    // 🔥 Bắt buộc dùng Service để nó tự động Refresh Cache trên RAM
    private final BlacklistService blacklistService;

    // Các hàm bên dưới tự động được kế thừa "Haki Bá Vương" của Role ADMIN
    // Sếp không cần gắn thêm gì cho từng hàm nữa.

    @GetMapping
    public ResponseEntity<List<BlacklistWord>> getAllWords() {
        return ResponseEntity.ok(blacklistService.getAllWords());
    }

    @PostMapping
    public ResponseEntity<?> addWord(@RequestBody Map<String, String> payload) {
        try {
            String word = payload.get("word");
            if (word == null || word.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("message", "Từ khóa không hợp lệ"));
            }
            return ResponseEntity.ok(blacklistService.addWord(word));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", "Từ khóa đã tồn tại trong danh sách!"));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteWord(@PathVariable Integer id) {
        try {
            blacklistService.deleteWord(id);
            return ResponseEntity.ok(Map.of("message", "Đã xóa từ khóa thành công"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", "Lỗi khi xóa từ khóa"));
        }
    }
}