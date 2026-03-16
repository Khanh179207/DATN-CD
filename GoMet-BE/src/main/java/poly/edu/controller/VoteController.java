package poly.edu.controller;

import poly.edu.dao.*;
import poly.edu.entity.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import poly.edu.service.NotificationService;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/votes")
@RequiredArgsConstructor
@CrossOrigin("*")
public class VoteController {

    private final VoteDAO voteDAO;
    private final EventPostsDAO eventPostsDAO;
    private final AccountDAO accountDAO;
    private final NotificationService notificationService;

    @PostMapping("/toggle")
    public ResponseEntity<?> toggleVote(@RequestBody Map<String, Object> payload) {

        // In ra console để sếp nhìn tận mắt Vue đang gửi cái quái gì lên
        System.out.println("====== DỮ LIỆU TỪ VUE GỬI LÊN ======");
        System.out.println(payload);

        Object rawAccountId = payload.get("accountID") != null ? payload.get("accountID") : payload.get("accountId");
        Object rawEventPostId = payload.get("eventPostID") != null ? payload.get("eventPostID") : payload.get("eventPostId");

        // 🔥 TÁCH LỖI RA ĐỂ BIẾT CHÍNH XÁC THIẾU CÁI NÀO
        if (rawAccountId == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "LỖI TỪ VUE: Đang bị thiếu ID của Sếp (accountID)"));
        }
        if (rawEventPostId == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "LỖI TỪ VUE: Đang bị thiếu ID của Bài Dự Thi (eventPostID)"));
        }

        // Ép kiểu sang Integer
        Integer accId = Integer.parseInt(rawAccountId.toString());
        Integer epId = Integer.parseInt(rawEventPostId.toString());

        // 1. Kiểm tra bài dự thi & sự kiện
        EventPosts ep = eventPostsDAO.findById(epId)
                .orElseThrow(() -> new RuntimeException("Bài dự thi không tồn tại"));
        Event ev = ep.getEvent();

        // Xử lý an toàn khi lấy Account
        Account account = accountDAO.findById(accId)
                .orElseThrow(() -> new RuntimeException("Tài khoản không tồn tại"));

        // 2. Kiểm tra khung giờ bình chọn
        LocalDateTime now = LocalDateTime.now();
        if (ev.getVoteStartAt() == null || ev.getVoteEndAt() == null ||
                now.isBefore(ev.getVoteStartAt()) || now.isAfter(ev.getVoteEndAt())) {
            return ResponseEntity.badRequest().body(Map.of("message", "Cổng bình chọn đang đóng!"));
        }

        // 3. Logic Toggle
        var existingVote = voteDAO.findByAccount_AccountIDAndEventPost_EventPostID(accId, epId);
        boolean isVotedNow;

        if (existingVote.isPresent()) {
            voteDAO.delete(existingVote.get());
            isVotedNow = false;
        } else {
            // 4. Lấy số lượng vote tối đa từ DB (nếu null thì mặc định 3)
            int limit = (ev.getMaxVotes() != null) ? ev.getMaxVotes() : 3;

            long votedCount = voteDAO.countVotesByUserInEvent(accId, ev.getEventID());
            if (votedCount >= limit) {
                return ResponseEntity.badRequest().body(Map.of("message", "Sếp đã dùng hết " + limit + " phiếu bầu!"));
            }

            // 5. Lưu phiếu bầu mới
            Votes v = new Votes();
            v.setAccount(account);
            v.setEventPost(ep);
            voteDAO.save(v);
            isVotedNow = true;

            // Create notification for the post owner if voter is not the owner
            Integer postOwnerId = ep.getPost().getAccount().getAccountID();
            if (!accId.equals(postOwnerId)) {
                notificationService.notifyEventVote(account.getUsername(), postOwnerId, ep.getPost().getPostID());
            }
        }

        // 🔥 TÍNH TOÁN LẠI TỔNG SỐ VOTE SAU KHI CLICK (Để trả về cho Vue update ngay lập tức)
        long newCount = voteDAO.countByEventPost_EventPostID(epId);

        return ResponseEntity.ok(Map.of(
                "isVoted", isVotedNow,
                "newVoteCount", newCount,
                "message", isVotedNow ? "Bình chọn thành công" : "Đã hủy bình chọn"
        ));
    }
}