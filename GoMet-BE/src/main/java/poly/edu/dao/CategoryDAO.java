package poly.edu.dao;

import poly.edu.dto.AdminCategoryDTO;
import poly.edu.dto.CategoryDTO;
import poly.edu.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface CategoryDAO extends JpaRepository<Category, Integer> {

    // 1. Dành cho ADMIN (Đã có - Giữ nguyên)
    @Query("SELECT new poly.edu.dto.AdminCategoryDTO(c.categoryID, c.categoryName, c.categoryImage, COUNT(p), c.isActive) " +
            "FROM Category c LEFT JOIN c.posts p " +
            "GROUP BY c.categoryID, c.categoryName, c.categoryImage, c.isActive " +
            "ORDER BY COUNT(p) DESC")
    List<AdminCategoryDTO> findAllWithPostCount();

    // 2. 🔥 DÀNH CHO USER (Thêm mới để sửa lỗi 500)
    // Chỉ lấy IsActive = 1 và trả về thẳng CategoryDTO
    @Query("SELECT new poly.edu.dto.CategoryDTO(c.categoryID, c.categoryName, c.categoryImage, COUNT(p)) " +
            "FROM Category c LEFT JOIN c.posts p " +
            "WHERE c.isActive = 1 " +
            "GROUP BY c.categoryID, c.categoryName, c.categoryImage")
    List<CategoryDTO> findAllActiveForUser();

    List<Category> findByIsActive(Integer isActive);
}