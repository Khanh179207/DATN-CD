package poly.edu.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor // 🔥 BẮT BUỘC PHẢI CÓ ĐỂ DAO GỌI
public class AdminCategoryDTO {
    private Integer categoryID;
    private String categoryName;
    private String categoryImage;
    private Long postCount;    // 🔥 PHẢI LÀ LONG, vì hàm COUNT() trong SQL trả về kiểu Long
    private Integer isActive;
}