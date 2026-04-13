package poly.edu.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import poly.edu.dao.EventDAO;
import poly.edu.dao.EventPostsDAO;
import poly.edu.dao.PostDAO;
import poly.edu.dao.VoteDAO; // 🔥 ĐÃ THÊM IMPORT VOTE DAO
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
@CrossOrigin("*")
public class EventController {

    private final EventService eventService;
    private final EventPostsDAO eventPostsDAO;
    private final EventDAO eventDAO;
    private final PostDAO postDAO;
    private final VoteDAO voteDAO; // 🔥 BỔ SUNG KHAI BÁO VOTE DAO ĐỂ CHECK VOTE

    @GetMapping
    public ResponseEntity<List<EventDTO>> getAll() {
        return ResponseEntity.ok(eventService.getAllEvents());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventDTO> getById(@PathVariable Integer id) {
        EventDTO dto = eventService.getEventById(id);
        return dto != null ? ResponseEntity.ok(dto) : ResponseEntity.notFound().build();
    }

    @GetMapping("/active")
    public ResponseEntity<List<EventDTO>> getActive() {
        return ResponseEntity.ok(eventService.getActiveEvents());
    }

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

    // ─── API NỘP BÀI: ĐÃ THÊM LOGIC CHECK THỜI GIAN ───────────────────────
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

            // 🔥 THIẾT QUÂN LUẬT: Chỉ cho nộp bài trong khung giờ StartAt -> EndAt
            LocalDateTime now = LocalDateTime.now();
            if (now.isBefore(event.getStartAt())) {
                return ResponseEntity.badRequest().body(Map.of("message", "Sự kiện chưa mở cổng nhận bài dự thi!"));
            }
            if (now.isAfter(event.getEndAt())) {
                return ResponseEntity.badRequest().body(Map.of("message", "Sự kiện đã kết thúc nhận bài dự thi!"));
            }

            // 2. Tìm bài viết
            Post post = postDAO.findById(postId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy bài viết"));

            // 3. Kiểm tra xem bài này đã nộp vào sự kiện này chưa (Tránh nộp trùng)
            if (eventPostsDAO.existsByEvent_EventIDAndPost_PostID(eventId, postId)) {
                return ResponseEntity.badRequest().body(Map.of("message", "Bài viết này đã nộp dự thi rồi sếp!"));
            }

            // 4. Lưu vào bảng trung gian EventPosts
            EventPosts eventPost = new EventPosts();
            eventPost.setEvent(event);
            eventPost.setPost(post);
            eventPost.setVoteCount(0); // Khởi tạo 0 vote cho bài mới
            eventPost.setCreatedAt(LocalDateTime.now());

            eventPostsDAO.save(eventPost);

            return ResponseEntity.ok(Map.of("message", "Nộp bài dự thi thành công! Chúc sếp giật giải!"));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", "Lỗi hệ thống: " + e.getMessage()));
        }
    }
}