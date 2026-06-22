package poly.edu.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import poly.edu.dao.AccountDAO;
import poly.edu.dao.HistoryDAO;
import poly.edu.dao.PostDAO;
import poly.edu.dao.SystemConfigDAO;
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
@PreAuthorize("isAuthenticated()")
public class HistoryController {

    private final HistoryDAO    historyDAO;
    private final AccountDAO    accountDAO;
    private final PostDAO       postDAO;
    private final PostController postController;
    private final SystemConfigDAO systemConfigDAO;

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

        // 🔥 SỬA LỖI LOGIC: NÚT BẬT TẮT LÀ CÔNG TẮC TỔNG
        boolean isHolidayActive = false;
        try {
            String freeAccess = systemConfigDAO.findById("FREE_ACCESS_EVENT")
                    .map(c -> c.getConfigValue()).orElse("FALSE");

            if ("TRUE".equalsIgnoreCase(freeAccess)) {
                String startStr = systemConfigDAO.findById("HOLIDAY_START")
                        .map(c -> c.getConfigValue()).orElse(null);
                String endStr = systemConfigDAO.findById("HOLIDAY_END")
                        .map(c -> c.getConfigValue()).orElse(null);

                if (startStr != null && endStr != null && !startStr.isBlank() && !endStr.isBlank()) {
                    LocalDateTime startTime = LocalDateTime.parse(startStr);
                    LocalDateTime endTime = LocalDateTime.parse(endStr);
                    LocalDateTime now = LocalDateTime.now();

                    if (!now.isBefore(startTime) && !now.isAfter(endTime)) {
                        isHolidayActive = true;
                    }
                } else {
                    isHolidayActive = true; // Bật nhưng không set lịch -> auto kích hoạt
                }
            }
        } catch (Exception e) {
            System.err.println("Lỗi kiểm tra chế độ lễ hội: " + e.getMessage());
        }

        // Nếu đang lễ hội, trả về OK luôn, không đi tiếp xuống database
        if (isHolidayActive) {
            return ResponseEntity.ok(Map.of("message", "Sự kiện lễ hội đang diễn ra, bỏ qua lưu lịch sử"));
        }

        // 🔥 BƯỚC 2: LOGIC LƯU LỊCH SỬ BÌNH THƯỜNG (Nếu không phải lễ hội)
        Account account = accountDAO.findById(accountID).orElse(null);
        Post    post    = postDAO.findById(postID).orElse(null);

        if (account == null || post == null) {
            System.out.println(">>> HistoryController: Account or Post NOT FOUND. AccountID: " + accountID + ", PostID: " + postID);
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Account or Post not found"));
        }

        // Upsert: if already viewed, only update the timestamp
        List<History> existing =
                historyDAO.findByAccount_AccountIDAndPost_PostID(accountID, postID);

        History history;
        if (!existing.isEmpty()) {
            history = existing.get(0);
            history.setLastViewedAt(LocalDateTime.now());

            if (existing.size() > 1) {
                for (int i = 1; i < existing.size(); i++) {
                    historyDAO.delete(existing.get(i));
                }
            }
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