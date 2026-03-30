package poly.edu.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import poly.edu.service.CloudinaryService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/upload")
@RequiredArgsConstructor

public class UploadController {

    private final CloudinaryService cloudinaryService;

    /**
     * CHỐT: Dùng @PostMapping không có path phụ để đồng bộ với Frontend hiện tại trên Develop.
     * Sử dụng phương thức uploadMedia từ CloudinaryService (bản mới nhất của team).
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> uploadImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "folder", defaultValue = "general") String folder) {

        System.out.println(">>> [API POST /api/upload] Yêu cầu upload nhận được");
        Map<String, Object> response = new HashMap<>();

        // Kiểm tra file đầu vào (Logic bảo vệ của sếp)
        if (file == null || file.isEmpty()) {
            response.put("status", "error");
            response.put("message", "Vui lòng chọn file để upload");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            // Log chi tiết để sếp dễ debug khi chạy
            System.out.println(">>> Đang đẩy file: " + file.getOriginalFilename() + " lên Cloudinary...");

            // CHỐT: Gọi hàm uploadMedia (tên hàm mới trên Develop)
            String imageUrl = cloudinaryService.uploadMedia(file, folder);

            response.put("status", "success");
            response.put("url", imageUrl);
            response.put("originalName", file.getOriginalFilename());

            System.out.println(">>> Upload thành công! URL: " + imageUrl);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println(">>> UploadController ERROR: " + e.getMessage());
            e.printStackTrace();

            response.put("status", "error");
            response.put("message", "Lỗi server: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}