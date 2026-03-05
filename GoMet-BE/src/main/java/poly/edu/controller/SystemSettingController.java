package poly.edu.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import poly.edu.dto.SystemPublicSettingsDTO;
import poly.edu.service.SystemSettingService;

@RestController
@RequestMapping("/api/system/settings")
@RequiredArgsConstructor
public class SystemSettingController {

    private final SystemSettingService systemSettingService;

    @GetMapping("/public")
    public ResponseEntity<SystemPublicSettingsDTO> getPublicSettings() {
        return ResponseEntity.ok(systemSettingService.getPublicSettings());
    }
}
