package poly.edu.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import poly.edu.dao.CategoryDAO;
import poly.edu.dto.AdminCategoryDTO;
import poly.edu.entity.Category;
import poly.edu.service.CategoryService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryDAO categoryDAO;

    /**
     * 🔥 HÀM THẦN THÁNH: Lấy toàn bộ danh sách kèm postCount đã tối ưu
     * Gọi trực tiếp hàm JPQL Constructor từ DAO
     */
    @Override
    public List<AdminCategoryDTO> findAllWithPostCount() {
        return categoryDAO.findAllWithPostCount();
    }

    /**
     * Tìm danh mục theo ID và map thủ công sang DTO
     */
    @Override
    public AdminCategoryDTO findById(Integer id) {
        Category c = categoryDAO.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục với ID: " + id));

        return AdminCategoryDTO.builder()
                .categoryID(c.getCategoryID())
                .categoryName(c.getCategoryName())
                .categoryImage(c.getCategoryImage()) // 🔥 Đồng bộ tên field mới
                .isActive(c.getIsActive())
                .postCount(c.getPosts() == null ? 0L : (long) c.getPosts().size())
                .build();
    }

    /**
     * 🔥 HÀM SAVE VẠN NĂNG: Xử lý đồng bộ Luồng 2 Bước
     */
    @Override
    @Transactional
    public AdminCategoryDTO save(AdminCategoryDTO dto) {
        Category cat;

        if (dto.getCategoryID() != null) {
            // Update
            cat = categoryDAO.findById(dto.getCategoryID())
                    .orElseThrow(() -> new RuntimeException("Danh mục không tồn tại!"));
        } else {
            // Create
            cat = new Category();
            cat.setIsActive(1); // Mặc định tạo mới là Active
        }

        cat.setCategoryName(dto.getCategoryName());
        cat.setCategoryImage(dto.getCategoryImage()); // 🔥 Nhận link ảnh Cloudinary từ Vue gửi lên

        // Nếu sếp có truyền trạng thái IsActive từ Dashboard Admin
        if (dto.getIsActive() != null) {
            cat.setIsActive(dto.getIsActive());
        }

        Category saved = categoryDAO.save(cat);

        // Trả về DTO (Lưu ý: save mới postCount sẽ bằng 0)
        return findById(saved.getCategoryID());
    }

    /**
     * 🚩 LOGIC XÓA BẢO MẬT: Chặn xóa danh mục mặc định
     */
    @Override
    @Transactional
    public void delete(Integer id) {
        // 1. Bảo vệ ID 1 (Món Ngon Tổng Hợp)
        if (id == 1) {
            throw new RuntimeException("Lỗi: Không được xóa danh mục mặc định của hệ thống!");
        }

        Category cat = categoryDAO.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục!"));

        // 2. Kiểm tra nếu còn bài viết (Ràng buộc dữ liệu)
        if (cat.getPosts() != null && !cat.getPosts().isEmpty()) {
            throw new RuntimeException("Danh mục còn bài viết, hãy chuyển bài viết sang mục khác trước khi xóa!");
        }

        categoryDAO.deleteById(id);
    }

    // --- Giữ lại hàm findAll cũ nếu sếp chưa sửa hết Controller ---
    @Override
    public List<AdminCategoryDTO> findAll() {
        return findAllWithPostCount();
    }
}