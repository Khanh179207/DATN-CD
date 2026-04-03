package poly.edu.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import poly.edu.dto.AdminEventDTO;
import poly.edu.dto.AdminEventPostDTO;
import poly.edu.service.AdminEventService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/events")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminEventController {

    private final AdminEventService adminEventService;

    // ===== Event CRUD =====

    @GetMapping
    public ResponseEntity<List<AdminEventDTO>> getAll() {
        return ResponseEntity.ok(adminEventService.findAllEvents());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AdminEventDTO> getOne(@PathVariable Integer id) {
        AdminEventDTO dto = adminEventService.findEventById(id);
        return dto != null ? ResponseEntity.ok(dto) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<AdminEventDTO> create(@RequestBody AdminEventDTO dto) {
        return ResponseEntity.ok(adminEventService.saveEvent(dto));
    }

    // 🔥 HÀM UPDATE ĐÃ ĐƯỢC DỌN SẠCH, CHUẨN MVC
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id, @RequestBody AdminEventDTO dto) {
        try {
            // Ép ID từ URL vào DTO để Service biết là đang Update chứ không phải Create mới
            dto.setEventID(id);

            // Gọi Service xử lý (Logic kiểm duyệt chặn sửa Luật/Thưởng đã nằm gọn trong Service)
            AdminEventDTO saved = adminEventService.saveEvent(dto);

            return ResponseEntity.ok(saved);

        } catch (RuntimeException e) {
            // Hứng lỗi "Sự kiện đã bắt đầu..." từ Service ném ra (nếu vi phạm)
            // Trả về HTTP 400 kèm message để Frontend hiển thị thông báo đỏ
            return ResponseEntity.badRequest().body(Map.of(
                    "message", e.getMessage()
            ));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        // Soft delete (isActive = 0)
        adminEventService.deleteEvent(id);
        return ResponseEntity.ok().build();
    }

    // ===== Event Detail (Posts in Event) =====

    @GetMapping("/{id}/posts")
    public ResponseEntity<List<AdminEventPostDTO>> getPosts(@PathVariable Integer id) {
        return ResponseEntity.ok(adminEventService.getPostsOfEvent(id));
    }

    @DeleteMapping("/posts/{eventPostID}")
    public ResponseEntity<?> removePost(@PathVariable Integer eventPostID) {
        adminEventService.removePostFromEvent(eventPostID);
        return ResponseEntity.ok().build();
    }

    // ===== Restore Event =====

    @PutMapping("/{id}/restore")
    public ResponseEntity<?> restoreEvent(@PathVariable Integer id) {
        adminEventService.restoreEvent(id);
        return ResponseEntity.ok().build();
    }

    // ===== Force End Event + Auto Reward =====

    @PostMapping("/{id}/force-end")
    public ResponseEntity<?> forceEndEvent(@PathVariable Integer id) {
        adminEventService.forceEndEventWithReward(id);
        return ResponseEntity.ok(Map.of("message", "Đã đóng sự kiện và phát thưởng thành công!"));
    }
}