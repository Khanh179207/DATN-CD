package poly.edu.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import poly.edu.dto.MaintenanceSettingsResponseDTO;
import poly.edu.dto.MaintenanceUpdateRequestDTO;
import poly.edu.service.SystemSettingService;

@RestController
@RequestMapping("/api/admin/system/settings")
@RequiredArgsConstructor
public class AdminSystemSettingController {

    private final SystemSettingService systemSettingService;

    @GetMapping
    public ResponseEntity<MaintenanceSettingsResponseDTO> getAdminSettings() {
        return ResponseEntity.ok(systemSettingService.getAdminSettings());
    }

    @PutMapping("/maintenance")
    public ResponseEntity<MaintenanceSettingsResponseDTO> updateMaintenance(@Valid @RequestBody MaintenanceUpdateRequestDTO request) {
        return ResponseEntity.ok(systemSettingService.updateMaintenance(request.isEnabled(), request.getMessage(), request.getModules()));
    }
}
