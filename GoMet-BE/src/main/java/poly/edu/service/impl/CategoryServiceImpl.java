package poly.edu.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import poly.edu.dao.CategoryDAO;
import poly.edu.dao.PostDAO; // 🔥 Import thêm PostDAO
import poly.edu.dto.AdminCategoryDTO;
import poly.edu.entity.Category;
import poly.edu.service.CategoryService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryDAO categoryDAO;
    private final PostDAO postDAO; // 🔥 Cần có dòng này để gọi hàm movePosts

    /**
     * 🔥 Lấy toàn bộ danh sách kèm postCount đã tối ưu
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
                .categoryImage(c.getCategoryImage())
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
            cat = categoryDAO.findById(dto.getCategoryID())
                    .orElseThrow(() -> new RuntimeException("Danh mục không tồn tại!"));
        } else {
            cat = new Category();
            cat.setIsActive(1);
        }

        cat.setCategoryName(dto.getCategoryName());
        cat.setCategoryImage(dto.getCategoryImage());

        if (dto.getIsActive() != null) {
            cat.setIsActive(dto.getIsActive());
        }

        Category saved = categoryDAO.save(cat);
        return findById(saved.getCategoryID());
    }

    /**
     * 🚩 LOGIC XÓA BẢO MẬT MỚI: Tự động gom bài viết về Category 1
     */
    @Override
    @Transactional // 🔥 BẮT BUỘC PHẢI CÓ ĐỂ NẾU LỖI SẼ ROLLBACK LẠI
    public void delete(Integer id) {
        // 1. Chặn đứng hành vi xóa danh mục gốc (Mặc định ID = 1)
        if (id == 1) {
            throw new RuntimeException("Không được phép xóa Danh mục Tổng hợp (ID = 1)!");
        }

        // 2. Chuyển toàn bộ bài viết của danh mục này sang danh mục 1
        postDAO.movePostsToDefaultCategory(id);

        // 3. Sau khi đã "dọn sạch" bài viết, tiến hành xóa Danh mục an toàn
        categoryDAO.deleteById(id);
    }

    // --- Giữ lại hàm findAll cũ nếu sếp chưa sửa hết Controller ---
    @Override
    public List<AdminCategoryDTO> findAll() {
        return findAllWithPostCount();
    }
}