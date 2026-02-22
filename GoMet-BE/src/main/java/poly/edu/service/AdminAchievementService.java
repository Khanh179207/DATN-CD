package poly.edu.service;

import poly.edu.dto.AdminAchievementDTO;

import java.util.List;

public interface AdminAchievementService {

    List<AdminAchievementDTO> findAll();
    AdminAchievementDTO findById(Integer id);
    AdminAchievementDTO save(AdminAchievementDTO dto);
    void delete(Integer id);
}