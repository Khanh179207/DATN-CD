package poly.edu.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostDTO {
    private Integer postID;
    private Integer accountID;
    private Integer categoryID;
    private Integer eventID; // 🔥 Dùng để Vue gửi ID sự kiện lên khi tạo bài

    private String title;
    private String description;
    private String ingredients; // Vue đang gửi lên dạng String (đã join bằng dấu phẩy)
    private String media;       // Gửi Link Cloudinary
    private String video;
    private Integer level;
    private Integer cookingTime;

    private Integer views;
    private Integer likeCount; // 🔥 Rất quan trọng để hiển thị lượt Thích
    private Integer totalPts;

    // Các thông tin phụ để Frontend hiển thị cho đẹp
    private String username;
    private String authorAvatar;
    private String categoryName;
    // Trong PostDTO.java và FavoriteDTO.java
    private LocalDateTime createdAt;
    private Integer isApproved;

    // 🔥 Bây giờ nó sẽ tự động hiểu và dùng cái file StepRequestDTO.java riêng biệt ở trên
    // Trong file PostDTO.java
    private List<StepRequestDTO> steps; // Dùng cái này là chuẩn nhất vì FE đang gửi lên mảng này
}