package poly.edu.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
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
    private String media;
    private Integer level;
    private Integer cookingTime;

    private Integer views;
    private Integer likeCount; // 🔥 Rất quan trọng để hiển thị lượt Thích

    // Các thông tin phụ để Frontend hiển thị cho đẹp
    private String username;
    private String authorAvatar;
    private String categoryName;
    private java.time.LocalDate createdAt;
    private Integer isApproved; // Vẫn nên giữ để báo cho User biết bài mình đã được duyệt chưa

    // Danh sách các bước nấu ăn (Vue đang gửi lên 1 mảng Steps)
    private List<CookingStepDTO> steps;
}