package poly.edu.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CloudinaryService {

    private final Cloudinary cloudinary;

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    public String uploadImage(MultipartFile file, String folderName) throws IOException {
        // 1. Kiểm tra và tạo thư mục local nếu chưa có (theo yêu cầu của bạn)
        Path root = Paths.get(uploadDir);
        if (!Files.exists(root)) {
            Files.createDirectories(root);
            System.out.println(">>> CloudinaryService: Đã tạo thư mục lưu trữ local: " + uploadDir);
        }

        // 2. Lưu file tạm vào local
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path filePath = root.resolve(fileName);
        Files.copy(file.getInputStream(), filePath);
        System.out.println(">>> CloudinaryService: Đã lưu file tại: " + filePath.toAbsolutePath());

        // 3. Upload lên Cloudinary
        Map params = ObjectUtils.asMap(
                "folder", "GoMet/" + folderName,
                "resource_type", "auto"
        );

        try {
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), params);
            String secureUrl = uploadResult.get("secure_url").toString();
            System.out.println(">>> CloudinaryService: Upload thành công. URL: " + secureUrl);
            return secureUrl;
        } catch (Exception e) {
            System.err.println(">>> CloudinaryService FAILED, falling back to LOCAL: " + e.getMessage());
            // Trả về đường dẫn tuyệt đối để frontend không bị nhầm origin
            String absoluteUrl = baseUrl + "/uploads/" + fileName;
            System.out.println(">>> CloudinaryService: Fallback URL: " + absoluteUrl);
            return absoluteUrl;
        }
    }
}