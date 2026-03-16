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
@CrossOrigin("*")
public class UploadController {

    private final CloudinaryService cloudinaryService;

    @PostMapping("/image")
    public ResponseEntity<Map<String, Object>> uploadImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "folder", defaultValue = "general") String folder) {
        
        System.out.println(">>> [API POST /api/upload/image] Request received");
        
        Map<String, Object> response = new HashMap<>();

        if (file == null || file.isEmpty()) {
            System.err.println(">>> UploadController: File is empty or null");
            response.put("status", "error");
            response.put("message", "Vui lòng chọn file để upload");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            System.out.println(">>> Uploading file: " + file.getOriginalFilename() + " to folder: " + folder);
            String imageUrl = cloudinaryService.uploadImage(file, folder);

            response.put("status", "success");
            response.put("url", imageUrl);
            response.put("originalName", file.getOriginalFilename());
            
            System.out.println(">>> Upload successful: " + imageUrl);
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