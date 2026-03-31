package poly.edu.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import poly.edu.dto.AdminEventDTO;
import poly.edu.dto.AdminEventPostDTO;
import poly.edu.dto.RewardedUserDTO;
import poly.edu.service.AdminEventService;
import poly.edu.service.EventRewardService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/events")
@RequiredArgsConstructor

public class AdminEventController {

    private final AdminEventService adminEventService;
    private final EventRewardService eventRewardService;

    @GetMapping
    public List<AdminEventDTO> getAll() {
        return adminEventService.findAllEvents();
    }

    @GetMapping("/{id}")
    public AdminEventDTO getOne(@PathVariable Integer id) {
        return adminEventService.findEventById(id);
    }

    @PostMapping
    public AdminEventDTO create(@RequestBody AdminEventDTO dto) {
        // Cả Event mới và ảnh Cloudinary đều đã nằm gọn trong DTO
        return adminEventService.saveEvent(dto);
    }

    @PutMapping("/{id}")
    public AdminEventDTO update(@PathVariable Integer id, @RequestBody AdminEventDTO dto) {
        dto.setEventID(id);
        return adminEventService.saveEvent(dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        adminEventService.deleteEvent(id);
    }

    // ===== REWARD ENDPOINT: Trigger thưởng người thắng =====
    @PostMapping("/{id}/reward")
    public ResponseEntity<?> rewardTopUsers(@PathVariable Integer id) {
        try {
            List<RewardedUserDTO> rewardedUsers = eventRewardService.rewardTopUsersForEvent(id);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Thưởng người thắng thành công!",
                    "rewardedUsers", rewardedUsers
            ));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "success", false,
                    "message", "Lỗi hệ thống: " + e.getMessage()
            ));
        }
    }

    // ===== Event Detail (Quản lý bài thi trong sự kiện) =====
    @GetMapping("/{id}/posts")
    public List<AdminEventPostDTO> getPosts(@PathVariable Integer id) {
        return adminEventService.getPostsOfEvent(id);
    }

    @DeleteMapping("/posts/{eventPostID}")
    public void removePost(@PathVariable Integer eventPostID) {
        adminEventService.removePostFromEvent(eventPostID);
    }
    
    // ===== 🛑 FORCE END EVENT ENDPOINT =====
    @PostMapping("/{id}/force-end")
    public ResponseEntity<?> forceEndEvent(@PathVariable Integer id) {
        try {
            adminEventService.forceEndEventWithReward(id);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Kết thúc sự kiện và thưởng người thắng thành công!"
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "success", false,
                    "message", "Lỗi hệ thống: " + e.getMessage()
            ));
        }
    }
    // ===== TÍNH NĂNG KHÔI PHỤC (RESTORE) =====
    @PutMapping("/{id}/restore")
    public void restoreEvent(@PathVariable Integer id) {
        // Gọi sang Service để update trực tiếp isActive = 1
        adminEventService.restoreEvent(id);
    }
}