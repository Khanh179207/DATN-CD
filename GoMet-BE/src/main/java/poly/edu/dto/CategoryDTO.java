package poly.edu.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDTO {
    private Integer categoryID;
    private String categoryName;
    private String categoryImage; // Thêm ảnh để User dễ chọn món
    private Long postCount;
}