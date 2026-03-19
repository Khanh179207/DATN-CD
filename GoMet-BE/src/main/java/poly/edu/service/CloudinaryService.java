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

    public String uploadMedia(MultipartFile file, String folderName) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IOException("File bị trống, không thể upload!");
        }

        // Cấu trúc folder: GoMet/categories, GoMet/events, ...
        Map params = ObjectUtils.asMap(
                "folder", "GoMet/" + folderName,
                "resource_type", "auto", // Tự động nhận diện ảnh/video/raw
                "overwrite", true
        );

        Map uploadResult = cloudinary.uploader().upload(file.getBytes(), params);

        // Trả về secure_url (https) cho an toàn và chuyên nghiệp
        return uploadResult.get("secure_url").toString();
    }
}