package poly.edu.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
 *   GET    /api/history/{accountID}           - list recent history (newest first)
 *   POST   /api/history                       - record a post view
 *   DELETE /api/history/{accountID}/clear     - wipe all history for user
 */
@RestController
@RequestMapping("/api/history")
@RequiredArgsConstructor
public class HistoryController {

    private final HistoryDAO    historyDAO;
    private final AccountDAO    accountDAO;
    private final PostDAO       postDAO;
    private final PostController postController;

    // ── GET history for a user ───────────────────────────────────────────
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
                .map(h -> postController.toPublicDTO(h.getPost()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }

    // ── POST record a view ───────────────────────────────────────────────
    @PostMapping
    public ResponseEntity<?> recordView(@RequestBody Map<String, Integer> body) {
        Integer accountID = body.get("accountID");
        Integer postID    = body.get("postID");

        if (accountID == null || postID == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "accountID and postID are required"));
        }

        Account account = accountDAO.findById(accountID).orElse(null);
        Post    post    = postDAO.findById(postID).orElse(null);

        if (account == null || post == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
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

    // ── DELETE clear all history ─────────────────────────────────────────
    @DeleteMapping("/{accountID}/clear")
    public ResponseEntity<?> clearHistory(@PathVariable Integer accountID) {
        historyDAO.deleteAllByAccountId(accountID);
        return ResponseEntity.ok(Map.of("message", "History cleared"));
    }
}
