package poly.edu.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import poly.edu.dto.AdminEventDTO;
import poly.edu.dto.AdminEventPostDTO;
import poly.edu.service.AdminEventService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/admin/events")
@RequiredArgsConstructor
@CrossOrigin
public class AdminEventController {

    private final AdminEventService adminEventService;

    // Dán đoạn này vào Controller để fix triệt để lỗi NULL ngày tháng
    @InitBinder
    public void initBinder(org.springframework.web.bind.WebDataBinder binder) {
        java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        binder.registerCustomEditor(LocalDateTime.class, new java.beans.PropertyEditorSupport() {
            @Override
            public void setAsText(String text) {
                if (text == null || text.trim().isEmpty()) {
                    setValue(null);
                } else {
                    setValue(LocalDateTime.parse(text, formatter));
                }
            }
        });
    }


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
        // Vue gửi bannerImage là link Cloudinary nằm sẵn trong dto rồi
        // Java chỉ việc ném thẳng vào Service để lưu xuống DB thôi
        return adminEventService.saveEvent(dto);
    }

    @PutMapping("/{id}")
    public AdminEventDTO update(@PathVariable Integer id, @RequestBody AdminEventDTO dto) {
        dto.setEventID(id);
        // Tương tự, bannerImage đã là link Cloudinary mới (hoặc cũ)
        return adminEventService.saveEvent(dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        adminEventService.deleteEvent(id);
    }

    // ✅ ĐÃ XÓA HÀM handleImageUpload LỖI THỜI!

    // ===== Event Detail =====
    @GetMapping("/{id}/posts")
    public List<AdminEventPostDTO> getPosts(@PathVariable Integer id) {
        return adminEventService.getPostsOfEvent(id);
    }

    @DeleteMapping("/posts/{eventPostID}")
    public void removePost(@PathVariable Integer eventPostID) {
        adminEventService.removePostFromEvent(eventPostID);
    }
}