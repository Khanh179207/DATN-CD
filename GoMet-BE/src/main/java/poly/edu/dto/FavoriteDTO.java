package poly.edu.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime; // 🔥 Đổi sang LocalDateTime

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteDTO {
    private Integer favoriteID;
    private Integer postID;
    private String title;
    private String media;
    private Integer cookingTime;
    private Integer level;
    private String categoryName;

    private Double rating;
    private Integer views;
    private String userName;
    private String avatar;

    // 🔥 Đã nâng cấp để hiện Time Ago chuẩn xịn
    private LocalDateTime createdAt;

    // 🔥 Hai "vũ khí" mới để cứu lấy trái tim của sếp
    private Integer favoriteCount; // Tổng số lượt like của bài viết
    private Boolean isLiked;       // Trạng thái đã like của sếp hay chưa
}