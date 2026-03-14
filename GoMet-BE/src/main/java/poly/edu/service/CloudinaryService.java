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
        if (file.isEmpty()) {
            throw new IOException("File is empty");
        }

        Map params = ObjectUtils.asMap(
                "folder", "GoMet/" + folderName,
                "resource_type", "auto", // 🔥 Quan trọng: Để Cloudinary tự phân biệt Image/Video
                "overwrite", true,
                "resource_type", "auto"
        );

        // Đối với video lớn (> 20MB), Cloudinary khuyến khích dùng upload Large
        // nhưng với giới hạn 50MB của sếp, hàm upload thường vẫn xử lý tốt.
        Map uploadResult = cloudinary.uploader().upload(file.getBytes(), params);

        return uploadResult.get("secure_url").toString();
    }
}