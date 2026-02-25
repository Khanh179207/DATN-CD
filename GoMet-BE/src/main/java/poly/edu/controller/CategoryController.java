package poly.edu.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import poly.edu.dao.CategoryDAO;
import poly.edu.dto.CategoryDTO;
import poly.edu.entity.Category;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryDAO categoryDAO;

    @GetMapping
    public ResponseEntity<List<CategoryDTO>> getAll() {
        List<CategoryDTO> result = categoryDAO.findAll().stream().map(c -> {
            CategoryDTO dto = new CategoryDTO();
            dto.setCategoryID(c.getCategoryID());
            dto.setCategoryName(c.getCategoryName());
            dto.setPostCount(c.getPosts() == null ? 0L :
                    c.getPosts().stream().filter(p -> p.getIsApproved() == 1 && p.getIsActive() == 1).count());
            return dto;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryDTO> getById(@PathVariable Integer id) {
        return categoryDAO.findById(id).map(c -> {
            CategoryDTO dto = new CategoryDTO();
            dto.setCategoryID(c.getCategoryID());
            dto.setCategoryName(c.getCategoryName());
            dto.setPostCount(c.getPosts() == null ? 0L :
                    c.getPosts().stream().filter(p -> p.getIsApproved() == 1 && p.getIsActive() == 1).count());
            return ResponseEntity.ok(dto);
        }).orElse(ResponseEntity.notFound().build());
    }
}
