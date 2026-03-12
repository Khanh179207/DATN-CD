package poly.edu.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SystemPublicSettingsDTO {
    private boolean maintenanceMode;
    private String message;
    private List<ModuleMaintenanceDTO> modules;
}
