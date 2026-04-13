package poly.edu.entity;

import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnore; // Để tránh vòng lặp vô tận khi trả về JSON
import java.util.List;

@Entity
@Table(name = "Category")
@Data // Đã bao gồm @Getter, @Setter, @RequiredArgsConstructor, @ToString, @EqualsAndHashCode
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer categoryID;

    @Column(nullable = false)
    private String categoryName;

    // 🔥 Đã đổi tên thành CategoryImage cho đồng bộ SQL
    @Column(name = "CategoryImage", columnDefinition = "NVARCHAR(MAX)")
    private String categoryImage;

    // 🔥 Thêm thuộc tính IsActive để Admin có thể ẩn/hiện danh mục
    @Column(name = "IsActive")
    private Integer isActive = 1; // Mặc định là 1 (Active)

    // Relationship with Post
    @OneToMany(mappedBy = "category")
    @JsonIgnore // Chặn vòng lặp: Khi lấy Category không cần lôi hết Post ra, tránh đứng máy
    private List<Post> posts;
}