package poly.edu.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import poly.edu.dao.CategoryDAO;
import poly.edu.dto.CategoryDTO;
import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@CrossOrigin("*")
public class CategoryController {

    private final CategoryDAO categoryDAO;

    @GetMapping
    public ResponseEntity<List<CategoryDTO>> getAll() {
        // 🔥 GỌI THẲNG HÀM DAO: Lấy dữ liệu nén vào DTO ngay từ trong SQL
        // Không còn c.getPosts() nên triệt tiêu hoàn toàn lỗi 500 (Lazy Load)
        List<CategoryDTO> result = categoryDAO.findAllActiveForUser();
        return ResponseEntity.ok(result);
    }
}