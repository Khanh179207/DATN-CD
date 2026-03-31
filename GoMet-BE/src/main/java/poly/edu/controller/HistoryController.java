package poly.edu.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; // 🔥 IMPORT THẺ BẢO VỆ
import org.springframework.web.bind.annotation.*;
import poly.edu.dao.AccountDAO;
import poly.edu.dao.HistoryDAO;
import poly.edu.dao.PostDAO;
import poly.edu.dto.PublicPostDTO;
import poly.edu.entity.Account;
import poly.edu.entity.History;
import poly.edu.entity.Post;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Browsing-history feature.
 * Endpoints:
 * GET    /api/history/{accountID}           - list recent history (newest first)
 * POST   /api/history                       - record a post view
 * DELETE /api/history/{accountID}/clear     - wipe all history for user
 */
@RestController
@RequestMapping("/api/history")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()") // 🔥 CHỐT CHẶN VÀNG: Toàn bộ Lịch sử đều cần đăng nhập
public class HistoryController {

    private final HistoryDAO    historyDAO;
    private final AccountDAO    accountDAO;
    private final PostDAO       postDAO;
    private final PostController postController;

    // 🟡 USER: Test endpoint
    @GetMapping("/test")
    public String test() {
        System.out.println(">>> HistoryController: GET /api/history/test called");
        return "HistoryController is working!";
    }

    // 🟡 USER: Lấy lịch sử xem của user
    @GetMapping("/{accountID}")
    public ResponseEntity<List<PublicPostDTO>> getHistory(
            @PathVariable Integer accountID,
            @RequestParam(defaultValue = "20") int limit) {

        List<PublicPostDTO> result = historyDAO
                .findByAccount_AccountIDOrderByLastViewedAtDesc(accountID)
                .stream()
                .filter(h -> h.getPost() != null
                        && h.getPost().getIsApproved() == 1
                        && h.getPost().getIsActive() == 1)
                .limit(limit)
                // 🔥 ĐÃ FIX LỖI Ở ĐÂY: Truyền thêm accountID vào để check trạng thái Like
                .map(h -> postController.toPublicDTO(h.getPost(), accountID))
                .collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }

    // 🟡 USER: Ghi nhận 1 lượt xem (Lưu lịch sử)
    @PostMapping
    public ResponseEntity<?> recordView(@RequestBody Map<String, Integer> body) {
        System.out.println(">>> HistoryController: POST /api/history called with: " + body);
        Integer accountID = body.get("accountID");
        Integer postID    = body.get("postID");

        if (accountID == null || postID == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "accountID and postID are required"));
        }

        Account account = accountDAO.findById(accountID).orElse(null);
        Post    post    = postDAO.findById(postID).orElse(null);

        if (account == null || post == null) {
            System.out.println(">>> HistoryController: Account or Post NOT FOUND. AccountID: " + accountID + ", PostID: " + postID);
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Account or Post not found"));
        }

        // Upsert: if already viewed, only update the timestamp
        Optional<History> existing =
                historyDAO.findByAccount_AccountIDAndPost_PostID(accountID, postID);

        History history;
        if (existing.isPresent()) {
            history = existing.get();
            history.setLastViewedAt(LocalDateTime.now());
        } else {
            history = History.builder()
                    .account(account)
                    .post(post)
                    .lastViewedAt(LocalDateTime.now())
                    .build();
        }

        historyDAO.save(history);
        return ResponseEntity.ok(Map.of("message", "History recorded"));
    }

    // 🟡 USER: Xóa toàn bộ lịch sử
    @DeleteMapping("/{accountID}/clear")
    public ResponseEntity<?> clearHistory(@PathVariable Integer accountID) {
        historyDAO.deleteAllByAccountId(accountID);
        return ResponseEntity.ok(Map.of("message", "History cleared"));
    }
}