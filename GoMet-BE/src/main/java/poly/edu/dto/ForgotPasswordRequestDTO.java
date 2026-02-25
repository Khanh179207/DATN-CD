package poly.edu.dto;

import lombok.Data;

@Data
public class ForgotPasswordRequestDTO {
    /** Can be email address OR username. */
    private String identifier;
}
