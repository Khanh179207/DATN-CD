package poly.edu.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreatePostStepRequestDTO {
    @Size(max = 5000, message = "Step description must be at most 5000 characters")
    private String desc;

    @Size(max = 1024, message = "Step image URL must be at most 1024 characters")
    private String image;
}
