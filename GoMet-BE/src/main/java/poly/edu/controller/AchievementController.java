package poly.edu.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import poly.edu.dao.AchievementDAO;
import poly.edu.dao.UserAchievementDAO;
import poly.edu.dto.AchievementDTO;
import poly.edu.dto.UserAchievementDTO;
import poly.edu.entity.Achievement;
import poly.edu.entity.UserAchievement;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Achievement catalogue and per-user earned achievements.
 * Endpoints:
 *   GET    /api/achievements              - list all achievements in the system
 *   POST   /api/achievements              - create new achievement
 *   PUT    /api/achievements/{id}         - update achievement
 *   DELETE /api/achievements/{id}         - delete achievement
 *   GET    /api/achievements/user/{id}    - achievements earned by a specific user
 */
@RestController
@RequestMapping("/api/achievements")
@RequiredArgsConstructor

public class AchievementController {

    private final AchievementDAO     achievementDAO;
    private final UserAchievementDAO userAchievementDAO;

    // â”€â”€ All achievements (catalogue) â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
    @GetMapping
    public ResponseEntity<List<AchievementDTO>> listAll() {
        List<AchievementDTO> result = achievementDAO.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    // â”€â”€ Create achievement â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
    @PostMapping
    public ResponseEntity<AchievementDTO> create(@RequestBody AchievementDTO dto) {
        Achievement a = new Achievement();
        a.setAchievementName(dto.getAchievementName());
        a.setDescription(dto.getDescription());
        a.setIcon(dto.getIcon());
        Achievement saved = achievementDAO.save(a);
        return ResponseEntity.ok(toDTO(saved));
    }

    // â”€â”€ Update achievement â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
    @PutMapping("/{id}")
    public ResponseEntity<AchievementDTO> update(@PathVariable Integer id,
                                                  @RequestBody AchievementDTO dto) {
        return achievementDAO.findById(id).map(a -> {
            if (dto.getAchievementName() != null) a.setAchievementName(dto.getAchievementName());
            if (dto.getDescription() != null)     a.setDescription(dto.getDescription());
            if (dto.getIcon() != null)             a.setIcon(dto.getIcon());
            return ResponseEntity.ok(toDTO(achievementDAO.save(a)));
        }).orElse(ResponseEntity.notFound().build());
    }

    // â”€â”€ Delete achievement â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        if (!achievementDAO.existsById(id)) return ResponseEntity.notFound().build();
        achievementDAO.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // â”€â”€ Achievements earned by a specific user â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
    @GetMapping("/user/{accountID}")
    public ResponseEntity<List<UserAchievementDTO>> getUserAchievements(
            @PathVariable Integer accountID) {

        List<UserAchievementDTO> result =
                userAchievementDAO.findByAccount_AccountIDOrderByReceivedAtDesc(accountID)
                        .stream()
                        .map(this::toUserDTO)
                        .collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }

    // â”€â”€ DTO helpers â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
    private AchievementDTO toDTO(Achievement a) {
        AchievementDTO dto = new AchievementDTO();
        dto.setAchievementID(a.getAchievementID());
        dto.setAchievementName(a.getAchievementName());
        dto.setDescription(a.getDescription());
        dto.setIcon(a.getIcon());
        return dto;
    }

    private UserAchievementDTO toUserDTO(UserAchievement ua) {
        UserAchievementDTO dto = new UserAchievementDTO();
        dto.setUaid(ua.getUaid());
        dto.setReceivedAt(ua.getReceivedAt());
        if (ua.getAchievement() != null) {
            dto.setAchievementID(ua.getAchievement().getAchievementID());
            dto.setAchievementName(ua.getAchievement().getAchievementName());
            dto.setDescription(ua.getAchievement().getDescription());
            dto.setIcon(ua.getAchievement().getIcon());
        }
        return dto;
    }
}
