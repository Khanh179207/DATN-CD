package poly.edu.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MealPlanDTO {
    private Integer planId;
    private Integer accountId;
    private Integer postId;
    private String customMealName;
    private LocalDate planDate;
    private String mealType;
    private String notes;
    private Boolean isCompleted;

    // 🔥 THÊM 2 TRƯỜNG NÀY ĐỂ NHẢ VỀ CHO FRONTEND
    private String postMedia; // Chứa link ảnh từ bài Post
    private String postTitle; // Chứa tiêu đề từ bài Post
    private String categoryName; // 🔥 Thêm trường này
}