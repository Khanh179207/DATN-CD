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
    private Integer postId; // Truyền lên null nếu ăn món tự nhập ở ngoài
    private String customMealName;
    private LocalDate planDate;
    private String mealType;
    private String notes;
    private Boolean isCompleted; // FE gửi lên true/false, BE nhả về true/false
}