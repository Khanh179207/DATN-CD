package poly.edu.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
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

    private AdminCategoryDTO toDTO(Category c) {
        AdminCategoryDTO dto = new AdminCategoryDTO();
        dto.setCategoryID(c.getCategoryID());
        dto.setCategoryName(c.getCategoryName());
        return dto;
    }

    private Category toEntity(AdminCategoryDTO dto) {
        return Category.builder()
                .categoryID(dto.getCategoryID())
                .categoryName(dto.getCategoryName())
                .build();
    }

    @Override
    public List<AdminCategoryDTO> findAll() {
        return categoryDAO.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public AdminCategoryDTO findById(Integer id) {
        return toDTO(categoryDAO.findById(id).orElseThrow());
    }

    @Override
    public AdminCategoryDTO save(AdminCategoryDTO dto) {
        return toDTO(categoryDAO.save(toEntity(dto)));
    }

    @Override
    public void delete(Integer id) {
        categoryDAO.deleteById(id);
    }
}
