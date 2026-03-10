package poly.edu.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import poly.edu.dto.AdminEventDTO;
import poly.edu.dto.AdminEventPostDTO;
import poly.edu.service.AdminEventService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/events")
@RequiredArgsConstructor
@CrossOrigin
public class AdminEventController {

    private final AdminEventService adminEventService;

    // Đường dẫn lưu file trong project (Spring Boot sẽ tự nhận diện thư mục static)
    private final String UPLOAD_DIRECTORY = "src/main/resources/static/uploads/events/";

    @GetMapping
    public List<AdminEventDTO> getAll() {
        return adminEventService.findAllEvents();
    }

    @GetMapping("/{id}")
    public AdminEventDTO getOne(@PathVariable Integer id) {
        return adminEventService.findEventById(id);
    }

    @PostMapping
    public AdminEventDTO create(
            @ModelAttribute AdminEventDTO dto,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile) {

        handleImageUpload(dto, imageFile); // 🔥 Gọi hàm xử lý lưu ảnh
        return adminEventService.saveEvent(dto);
    }

    @PutMapping("/{id}")
    public AdminEventDTO update(
            @PathVariable Integer id,
            @ModelAttribute AdminEventDTO dto,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile) {

        dto.setEventID(id);
        handleImageUpload(dto, imageFile); // 🔥 Gọi hàm xử lý lưu ảnh
        return adminEventService.saveEvent(dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        adminEventService.deleteEvent(id);
    }

    // --- HÀM XỬ LÝ LƯU FILE ẢNH (BÍ KÍP ĐÂY SẾP) ---
    private void handleImageUpload(AdminEventDTO dto, MultipartFile imageFile) {
        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                // 1. Tạo thư mục nếu chưa có
                Path uploadPath = Paths.get(UPLOAD_DIRECTORY);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                // 2. Tạo tên file duy nhất (Tránh trùng tên)
                String fileName = UUID.randomUUID().toString() + "_" + imageFile.getOriginalFilename();
                Path filePath = uploadPath.resolve(fileName);

                // 3. Lưu file vật lý vào ổ cứng
                Files.copy(imageFile.getInputStream(), filePath);

                // 4. Gán đường dẫn Web vào DTO để lưu xuống DB
                // Link sẽ có dạng: /uploads/events/abc-xyz.jpg
                dto.setBannerImage("/uploads/events/" + fileName);

            } catch (IOException e) {
                System.err.println("Lỗi lưu file rồi sếp ơi: " + e.getMessage());
            }
        }
        // Nếu không có imageFile, dto.bannerImage sẽ giữ nguyên giá trị (link cũ hoặc null)
    }

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