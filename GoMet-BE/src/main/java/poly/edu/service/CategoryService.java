package poly.edu.service;

import poly.edu.dto.AdminCategoryDTO;
import java.util.List;

public interface CategoryService {

    // 🔥 QUAN TRỌNG: Lấy danh sách kèm số lượng bài viết để vẽ Dashboard Admin
    List<AdminCategoryDTO> findAllWithPostCount();

    // Lấy chi tiết 1 danh mục
    AdminCategoryDTO findById(Integer id);

    // Lưu vạn năng (Thêm mới & Cập nhật) - Nhận JSON link ảnh từ Frontend
    AdminCategoryDTO save(AdminCategoryDTO dto);

    // Xóa danh mục (Logic chặn xóa ID 1 sẽ nằm ở phần Implementation)
    void delete(Integer id);

    // Giữ lại hàm này nếu sếp cần lấy danh sách cơ bản
    List<AdminCategoryDTO> findAll();
}