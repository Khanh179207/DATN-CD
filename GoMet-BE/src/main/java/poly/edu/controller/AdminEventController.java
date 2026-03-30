package poly.edu.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import poly.edu.dto.AdminEventDTO;
import poly.edu.dto.AdminEventPostDTO;
import poly.edu.service.AdminEventService;

import java.util.List;

@RestController
@RequestMapping("/api/admin/events")
@RequiredArgsConstructor

public class AdminEventController {

    private final AdminEventService adminEventService;

    // ĐÃ XÓA @InitBinder VÌ BÂY GIỜ DÙNG @RequestBody (JSON)
    // Mọi convert ngày tháng sẽ do Jackson tự động làm!

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
        // Khi Vue gửi lệnh Kết thúc nhanh, dto.getIsForceEnded() sẽ là TRUE
        // Cập nhật lại thời gian EndAt, VoteEndAt về hiện tại
        return adminEventService.saveEvent(dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        // TÍNH NĂNG XÓA MỀM (SOFT DELETE)
        // Lưu ý: Bên trong AdminEventService sếp phải đổi logic hàm này thành UPDATE isActive = 0 nhé!
        adminEventService.deleteEvent(id);
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
    // ===== TÍNH NĂNG KHÔI PHỤC (RESTORE) =====
    @PutMapping("/{id}/restore")
    public void restoreEvent(@PathVariable Integer id) {
        // Gọi sang Service để update trực tiếp isActive = 1
        adminEventService.restoreEvent(id);
    }
}