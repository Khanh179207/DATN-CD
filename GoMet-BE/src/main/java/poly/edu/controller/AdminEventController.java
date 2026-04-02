package poly.edu.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import poly.edu.dto.AdminEventDTO;
import poly.edu.dto.AdminEventPostDTO;
import poly.edu.service.AdminEventService;

import java.time.LocalDateTime;
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

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id, @RequestBody AdminEventDTO dto) {
        AdminEventDTO existing = adminEventService.findEventById(id);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }

        // 🔥 FIX LỖI Ở ĐÂY: Chuyển String từ DTO sang LocalDateTime để so sánh
        // Giả sử existing.getStartAt() trả về String dạng "2026-04-02T15:10:00"
        LocalDateTime startAtFromDB = LocalDateTime.parse(existing.getStartAt());
        LocalDateTime now = LocalDateTime.now();

        if (now.isAfter(startAtFromDB)) {
            boolean isMaxVotesChanged = !existing.getMaxVotes().equals(dto.getMaxVotes());

            String oldReward = existing.getReward() != null ? existing.getReward() : "";
            String newReward = dto.getReward() != null ? dto.getReward() : "";
            boolean isRewardChanged = !oldReward.equals(newReward);

            if (isMaxVotesChanged || isRewardChanged) {
                return ResponseEntity.badRequest().body(Map.of(
                        "message", "Sự kiện đã bắt đầu, sếp không được chỉnh sửa Luật và Phần thưởng để đảm bảo công bằng!"
                ));
            }
        }

        dto.setEventID(id);
        AdminEventDTO saved = adminEventService.saveEvent(dto);
        return ResponseEntity.ok(saved);
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