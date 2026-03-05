package poly.edu.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ForgotPasswordRequestDTO {
    /** Can be email address OR username. */
    @NotBlank(message = "Identifier is required")
    @Size(max = 255, message = "Identifier must be at most 255 characters")
    private String identifier;
}
