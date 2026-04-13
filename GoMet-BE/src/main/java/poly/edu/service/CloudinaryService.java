package poly.edu.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CloudinaryService {

    private final Cloudinary cloudinary;

    /**
     * CHỐT: Tên hàm là uploadMedia để khớp với Controller và hỗ trợ đa phương tiện (ảnh/video).
     */
    public String uploadMedia(MultipartFile file, String folderName) throws IOException {

        // 1. Kiểm tra file đầu vào (Logic an toàn)
        if (file == null || file.isEmpty()) {
            throw new IOException("File bị trống, Cloudinary không thể xử lý!");
        }

        // 2. Cấu hình tham số Upload (Giữ cấu trúc folder GOMET của sếp)
        Map params = ObjectUtils.asMap(
                "folder", "GoMet/" + folderName,
                "resource_type", "auto", // Tự động nhận diện ảnh/video/raw (Rất Pro)
                "overwrite", true
        );

        try {
            System.out.println(">>> [Cloudinary] Đang đẩy file '" + file.getOriginalFilename() + "' lên mây...");

            // 3. Thực hiện Upload trực tiếp từ mảng Byte (Không lưu tạm ở Local cho sạch máy)
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), params);

            String secureUrl = uploadResult.get("secure_url").toString();
            System.out.println(">>> [Cloudinary] SUCCESS! URL: " + secureUrl);

            // Trả về secure_url (https) để đảm bảo bảo mật và chuẩn SEO
            return secureUrl;

        } catch (Exception e) {
            System.err.println(">>> [Cloudinary] UPLOAD FAILED: " + e.getMessage());
            throw new IOException("Không thể upload file lên Cloudinary: " + e.getMessage());
        }
    }
}