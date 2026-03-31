package poly.edu.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import poly.edu.dto.AdminCategoryDTO;
import poly.edu.service.CategoryService;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/categories")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")

public class AdminCategoryController {

    private final CategoryService categoryService;

    // 1. Lấy toàn bộ danh sách (Kèm postCount để vẽ Dashboard)
    @GetMapping
    public ResponseEntity<List<AdminCategoryDTO>> getAll() {
        return ResponseEntity.ok(categoryService.findAllWithPostCount());
    }

    // 2. Lấy chi tiết 1 danh mục
    @GetMapping("/{id}")
    public ResponseEntity<AdminCategoryDTO> getOne(@PathVariable Integer id) {
        return ResponseEntity.ok(categoryService.findById(id));
    }

    // 3. Thêm mới: Nhận JSON (categoryName + categoryImage từ Cloudinary)
    @PostMapping
    public ResponseEntity<AdminCategoryDTO> create(@RequestBody AdminCategoryDTO dto) {
        return ResponseEntity.status(201).body(categoryService.save(dto));
    }

    // 4. Cập nhật toàn bộ thông tin
    @PutMapping("/{id}")
    public ResponseEntity<AdminCategoryDTO> update(@PathVariable Integer id, @RequestBody AdminCategoryDTO dto) {
        dto.setCategoryID(id);
        return ResponseEntity.ok(categoryService.save(dto));
    }

    // 5. 🔥 TÍNH NĂNG LUXURY: Bật/Tắt trạng thái hoạt động nhanh
    // Giúp Admin ẩn danh mục khỏi người dùng mà không cần xóa
    @PatchMapping("/{id}/toggle-status")
    public ResponseEntity<AdminCategoryDTO> toggleStatus(@PathVariable Integer id) {
        AdminCategoryDTO dto = categoryService.findById(id);
        dto.setIsActive(dto.getIsActive() == 1 ? 0 : 1);
        return ResponseEntity.ok(categoryService.save(dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        try {
            // Gọi hàm Xóa an toàn từ Service
            categoryService.delete(id);

            // Trả về JSON để Frontend đọc được success = true
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Đã xóa danh mục và chuyển các bài viết cũ về danh mục Mặc định!"
            ));
        } catch (RuntimeException e) {
            // Nếu dính lỗi (như xóa ID 1), trả về JSON kèm lỗi để Vue.js đọc và hiện Toast đỏ
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }
}