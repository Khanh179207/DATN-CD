package poly.edu.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import poly.edu.dto.AdminAchievementDTO;
import poly.edu.service.AdminAchievementService;
import java.util.List;

@RestController
@RequestMapping("/api/admin/achievements")
@RequiredArgsConstructor
@CrossOrigin
public class AdminAchievementController {

    private final AdminAchievementService adminAchievementService;

    @GetMapping
    public List<AdminAchievementDTO> getAll() {
        return adminAchievementService.findAll();
    }

    @GetMapping("/{id}")
    public AdminAchievementDTO getOne(@PathVariable Integer id) {
        return adminAchievementService.findById(id);
    }

    @PostMapping
    public AdminAchievementDTO create(@RequestBody AdminAchievementDTO dto) {
        return adminAchievementService.save(dto);
    }

    @PutMapping("/{id}")
    public AdminAchievementDTO update(@PathVariable Integer id,
                                      @RequestBody AdminAchievementDTO dto) {
        dto.setAchievementID(id);
        return adminAchievementService.save(dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        adminAchievementService.delete(id);
    }
}