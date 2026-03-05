package poly.edu.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class MaintenanceUpdateRequestDTO {
    private boolean enabled;

    @Size(max = 255, message = "Maintenance message must be <= 255 characters")
    private String message;

    private List<ModuleMaintenanceUpdateDTO> modules;
}
