package poly.edu.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import poly.edu.entity.SearchHistory;
import poly.edu.service.SearchHistoryService;

import java.util.List;

@RestController
@RequestMapping("/api/search-history")
@RequiredArgsConstructor
@CrossOrigin("*") // Cho phép Frontend (cổng 5173) gọi vào
public class SearchHistoryController {

    private final SearchHistoryService searchHistoryService;

    // 1. Lấy lịch sử
    @GetMapping("/{accountId}")
    public ResponseEntity<?> getHistory(@PathVariable Integer accountId) {
        try {
            List<SearchHistory> histories = searchHistoryService.getHistoryByAccountId(accountId);
            return ResponseEntity.ok(histories);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Lỗi khi lấy lịch sử: " + e.getMessage());
        }
    }
    // 2. Lưu từ khóa
    @PostMapping("")
    public ResponseEntity<?> saveHistory(@RequestParam Integer accountId, @RequestParam String keyword) {
        searchHistoryService.saveKeyword(accountId, keyword);
        return ResponseEntity.ok().body("{\"message\": \"Đã lưu lịch sử\"}");
    }

    // 3. Xóa 1 từ khóa
    @DeleteMapping("/{searchId}")
    public ResponseEntity<?> deleteHistory(@PathVariable Integer searchId) {
        searchHistoryService.deleteHistoryById(searchId);
        return ResponseEntity.ok().body("{\"message\": \"Đã xóa lịch sử\"}");
    }

    // 4. Xóa tất cả lịch sử của 1 người
    @DeleteMapping("/clear/{accountId}")
    public ResponseEntity<?> clearHistory(@PathVariable Integer accountId) {
        searchHistoryService.clearAllHistory(accountId);
        return ResponseEntity.ok().body("{\"message\": \"Đã xóa toàn bộ lịch sử\"}");
    }
}