package poly.edu.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import poly.edu.dao.CategoryDAO;
import poly.edu.dao.PostDAO;
import poly.edu.dto.CategoryDTO;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryDAO categoryDAO;
    private final PostDAO postDAO;

    @GetMapping
    public ResponseEntity<List<CategoryDTO>> getAll() {
        List<CategoryDTO> result = categoryDAO.findAll().stream().map(c -> {
            CategoryDTO dto = new CategoryDTO();
            dto.setCategoryID(c.getCategoryID());
            dto.setCategoryName(c.getCategoryName());
            dto.setPostCount(postDAO.countVisibleByCategoryId(c.getCategoryID()));
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
            dto.setPostCount(postDAO.countVisibleByCategoryId(c.getCategoryID()));
            return ResponseEntity.ok(dto);
        }).orElse(ResponseEntity.notFound().build());
    }
}
