package poly.edu.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import poly.edu.service.CloudinaryService;

import java.util.Map;

@RestController
@RequestMapping("/api/upload")
@RequiredArgsConstructor
@CrossOrigin("*") // Cho phép Vue.js gọi API mà không bị lỗi CORS
public class UploadController {

    private final CloudinaryService cloudinaryService;

    @PostMapping("/image")
    public ResponseEntity<?> uploadImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "folder", defaultValue = "general") String folder) {
        try {
            // Gọi Service để ném ảnh lên mây
            String imageUrl = cloudinaryService.uploadImage(file, folder);

            // Trả về JSON chứa link ảnh
            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "url", imageUrl
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "error",
                    "message", "Lỗi upload ảnh: " + e.getMessage()
            ));
        }
    }
}