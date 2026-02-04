package poly.edu.service;

import poly.edu.dto.AdminCategoryDTO;

import java.util.List;

public interface CategoryService {
    List<AdminCategoryDTO> findAll();
    AdminCategoryDTO findById(Integer id);
    AdminCategoryDTO save(AdminCategoryDTO dto);
    void delete(Integer id);
}
