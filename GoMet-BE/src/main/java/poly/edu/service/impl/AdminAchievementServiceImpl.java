package poly.edu.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import poly.edu.dao.AchievementDAO;
import poly.edu.dto.AdminAchievementDTO;
import poly.edu.entity.Achievement;
import poly.edu.service.AdminAchievementService;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminAchievementServiceImpl implements AdminAchievementService {

    private final AchievementDAO achievementDAO;

    private AdminAchievementDTO toDTO(Achievement a) {
        AdminAchievementDTO dto = new AdminAchievementDTO();
        dto.setAchievementID(a.getAchievementID());
        dto.setAchievementName(a.getAchievementName());
        dto.setDescription(a.getDescription());
        dto.setIcon(a.getIcon());
        return dto;
    }

    private Achievement toEntity(AdminAchievementDTO dto) {
        return Achievement.builder()
                .achievementID(dto.getAchievementID())
                .achievementName(dto.getAchievementName())
                .description(dto.getDescription())
                .icon(dto.getIcon())
                .build();
    }

    @Override
    public List<AdminAchievementDTO> findAll() {
        return achievementDAO.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public AdminAchievementDTO findById(Integer id) {
        return toDTO(achievementDAO.findById(id).orElseThrow());
    }

    @Override
    public AdminAchievementDTO save(AdminAchievementDTO dto) {
        return toDTO(achievementDAO.save(toEntity(dto)));
    }

    @Override
    public void delete(Integer id) {
        achievementDAO.deleteById(id);
    }
}