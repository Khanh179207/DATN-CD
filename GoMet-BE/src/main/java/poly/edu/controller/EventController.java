package poly.edu.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; // 🔥 IMPORT THẺ BẢO VỆ
import org.springframework.web.bind.annotation.*;
import poly.edu.dao.EventDAO;
import poly.edu.dao.EventPostsDAO;
import poly.edu.dao.PostDAO;
import poly.edu.dao.VoteDAO;
import poly.edu.dto.EventDTO;
import poly.edu.entity.Event;
import poly.edu.entity.EventPosts;
import poly.edu.entity.Post;
import poly.edu.service.EventService;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
// 🟢 KHÔNG ĐẶT KHÓA Ở CLASS: Khách vãng lai vẫn được hóng hớt Event thoải mái
public class EventController {

    private final EventService eventService;
    private final EventPostsDAO eventPostsDAO;
    private final EventDAO eventDAO;
    private final PostDAO postDAO;
    private final VoteDAO voteDAO;

    // 🟢 PUBLIC: Mở toang cho mọi người xem danh sách Event
    @GetMapping
    public ResponseEntity<List<EventDTO>> getAll() {
        return ResponseEntity.ok(eventService.getAllEvents());
    }

    // 🟢 PUBLIC: Mở toang cho mọi người xem chi tiết Event
    @GetMapping("/{id}")
    public ResponseEntity<EventDTO> getById(@PathVariable Integer id) {
        EventDTO dto = eventService.getEventById(id);
        return dto != null ? ResponseEntity.ok(dto) : ResponseEntity.notFound().build();
    }

    // 🟢 PUBLIC: Mở toang cho mọi người xem Event đang diễn ra
    @GetMapping("/active")
    public ResponseEntity<List<EventDTO>> getActive() {
        return ResponseEntity.ok(eventService.getActiveEvents());
    }

    // 🟢 PUBLIC: Mở toang cho mọi người xem các bài thi của Event
    // 🔥 ĐÃ FIX API NÀY: Trả về tên, avatar và check trạng thái Vote của user
    @GetMapping("/{eventId}/posts")
    public ResponseEntity<?> getEventPosts(
            @PathVariable Integer eventId,
            @RequestParam(required = false) Integer accountId) { // Nhận thêm accountId từ Vue

        // Lấy danh sách EventPosts (Sếp nhớ đảm bảo EventPostsDAO có hàm findByEvent_EventID nhé)
        List<EventPosts> rawPosts = eventPostsDAO.findByEvent_EventID(eventId);

        List<Map<String, Object>> response = rawPosts.stream().map(ep -> {
            Map<String, Object> map = new HashMap<>();
            map.put("eventPostID", ep.getEventPostID());
            map.put("postID", ep.getPost().getPostID());
            map.put("postTitle", ep.getPost().getTitle());
            map.put("postImage", ep.getPost().getMedia());
            map.put("voteCount", ep.getVoteCount() != null ? ep.getVoteCount() : 0);
            map.put("isActive", ep.getPost().getIsActive());
            map.put("isApproved", ep.getPost().getIsApproved());

            // 1. Lấy thông tin tác giả từ Account
            if (ep.getPost().getAccount() != null) {
                map.put("authorName", ep.getPost().getAccount().getUsername());
                map.put("username", ep.getPost().getAccount().getUsername());
                map.put("authorAvatar", ep.getPost().getAccount().getAvatar());
            } else {
                map.put("authorName", "Ẩn danh");
                map.put("username", "Ẩn danh");
            }

            // 2. Check xem User hiện tại đã vote bài này chưa
            boolean isVoted = false;
            if (accountId != null) {
                isVoted = voteDAO.findByAccount_AccountIDAndEventPost_EventPostID(accountId, ep.getEventPostID()).isPresent();
            }
            map.put("voted", isVoted); // Truyền xuống cho Vue đổi màu nút

            return map;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    // 🟡 USER ONLY: Muốn nộp bài dự thi thì bắt buộc phải đăng nhập!
    @PreAuthorize("isAuthenticated()") // 🔥 CHỐT CHẶN VÀNG NẰM Ở ĐÂY
    // ... các import giữ nguyên ...

    @PostMapping("/submit")
    public ResponseEntity<?> submitToEvent(@RequestBody Map<String, Object> payload) {
        try {
            Object rawEventId = payload.get("EventID") != null ? payload.get("EventID") : payload.get("eventID");
            Object rawPostId = payload.get("PostID") != null ? payload.get("PostID") : payload.get("postID");

            if (rawEventId == null || rawPostId == null) {
                return ResponseEntity.badRequest().body(Map.of("message", "Thiếu EventID hoặc PostID sếp ơi!"));
            }

            Integer eventId = Integer.parseInt(rawEventId.toString());
            Integer postId = Integer.parseInt(rawPostId.toString());

            // 1. Tìm sự kiện
            Event event = eventDAO.findById(eventId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy sự kiện"));

            // Logic check thời gian (Giữ nguyên)
            LocalDateTime now = LocalDateTime.now();
            if (now.isBefore(event.getStartAt())) {
                return ResponseEntity.badRequest().body(Map.of("message", "Sự kiện chưa mở cổng nhận bài dự thi!"));
            }
            if (now.isAfter(event.getEndAt())) {
                return ResponseEntity.badRequest().body(Map.of("message", "Sự kiện đã kết thúc nhận bài dự thi!"));
            }

            // 2. Tìm bài viết sếp định nộp
            Post post = postDAO.findById(postId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy bài viết"));

            // 🔥 LOGIC MỚI: CHẶN 1 NGƯỜI - 1 BÀI
            // Lấy ID người nộp từ bài viết
            Integer authorId = post.getAccount().getAccountID();

            // Check xem trong sự kiện này, Account này đã có bài nào chưa
            if (eventPostsDAO.existsByEventIdAndAuthorId(eventId, authorId)) {
                return ResponseEntity.badRequest().body(Map.of(
                        "message", "Sếp đã nộp bài dự thi cho sự kiện này rồi! Mỗi người chỉ được tham gia 1 bài thôi nhé."
                ));
            }

            // Check phụ: Bài viết này có đang nộp cho sự kiện khác không (nếu sếp muốn chặn luôn)
            // Hiện tại chỉ chặn theo Account trong 1 Event này thôi.

            // 4. Lưu vào bảng trung gian EventPosts
            EventPosts eventPost = new EventPosts();
            eventPost.setEvent(event);
            eventPost.setPost(post);
            eventPost.setVoteCount(0);
            eventPost.setCreatedAt(LocalDateTime.now());

            eventPostsDAO.save(eventPost);

            return ResponseEntity.ok(Map.of("message", "Nộp bài dự thi thành công! Chúc sếp giật giải!"));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", "Lỗi hệ thống: " + e.getMessage()));
        }
    }}