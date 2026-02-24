package poly.edu.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import poly.edu.dto.AdminCategoryDTO;
import poly.edu.service.CategoryService;
import java.util.List;

@RestController
@RequestMapping("/api/admin/categories")
@RequiredArgsConstructor
@CrossOrigin
public class AdminCategoryController {
    private final CategoryService categoryService;

    @GetMapping
    public List<AdminCategoryDTO> getAll() {
        return categoryService.findAll();
    }

    @GetMapping("/{id}")
    public AdminCategoryDTO getOne(@PathVariable Integer id) {
        return categoryService.findById(id);
    }

    @PostMapping
    public AdminCategoryDTO create(@RequestBody AdminCategoryDTO dto) {
        return categoryService.save(dto);
    }

    @PutMapping("/{id}")
    public AdminCategoryDTO update(@PathVariable Integer id,
                              @RequestBody AdminCategoryDTO dto) {
        dto.setCategoryID(id);
        return categoryService.save(dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        categoryService.delete(id);
    }
}
