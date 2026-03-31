package poly.edu.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; // 🔥 IMPORT THẺ BẢO VỆ
import org.springframework.web.bind.annotation.*;
import poly.edu.entity.SearchHistory;
import poly.edu.service.SearchHistoryService;

import java.util.List;

@RestController
@RequestMapping("/api/search-history")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()") // 🔥 CHỐT CHẶN VÀNG: Bắt buộc đăng nhập cho TẤT CẢ các hành động với lịch sử tìm kiếm
public class SearchHistoryController {

    private final SearchHistoryService searchHistoryService;

    // 🟡 USER: Lấy lịch sử tìm kiếm của chính mình
    @GetMapping("/{accountId}")
    public ResponseEntity<?> getHistory(@PathVariable Integer accountId) {
        try {
            List<SearchHistory> histories = searchHistoryService.getHistoryByAccountId(accountId);
            return ResponseEntity.ok(histories);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Lỗi khi lấy lịch sử: " + e.getMessage());
        }
    }

    // 🟡 USER: Lưu từ khóa vừa tìm kiếm vào DB
    @PostMapping("")
    public ResponseEntity<?> saveHistory(@RequestParam Integer accountId, @RequestParam String keyword) {
        searchHistoryService.saveKeyword(accountId, keyword);
        return ResponseEntity.ok().body("{\"message\": \"Đã lưu lịch sử\"}");
    }

    // 🟡 USER: Xóa 1 từ khóa cụ thể trong lịch sử
    @DeleteMapping("/{searchId}")
    public ResponseEntity<?> deleteHistory(@PathVariable Integer searchId) {
        searchHistoryService.deleteHistoryById(searchId);
        return ResponseEntity.ok().body("{\"message\": \"Đã xóa lịch sử\"}");
    }

    // 🟡 USER: Xóa sạch sành sanh lịch sử tìm kiếm
    @DeleteMapping("/clear/{accountId}")
    public ResponseEntity<?> clearHistory(@PathVariable Integer accountId) {
        searchHistoryService.clearAllHistory(accountId);
        return ResponseEntity.ok().body("{\"message\": \"Đã xóa toàn bộ lịch sử\"}");
    }
}