package poly.edu.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class CreatePostRequestDTO {

    @NotNull(message = "accountID is required")
    private Integer accountID;

    @NotNull(message = "categoryID is required")
    private Integer categoryID;

    @NotBlank(message = "Title is required")
    @Size(min = 5, max = 255, message = "Title must be between 5 and 255 characters")
    private String title;

    @NotBlank(message = "Description is required")
    @Size(min = 20, max = 10000, message = "Description must be between 20 and 10000 characters")
    private String description;

    @NotBlank(message = "Ingredients are required")
    @Size(min = 10, max = 12000, message = "Ingredients must be between 10 and 12000 characters")
    private String ingredients;

    @Size(max = 1024, message = "Media URL must be at most 1024 characters")
    private String media;

    @Min(value = 1, message = "Level must be between 1 and 3")
    @Max(value = 3, message = "Level must be between 1 and 3")
    private Integer level;

    @Min(value = 1, message = "Cooking time must be at least 1 minute")
    @Max(value = 1440, message = "Cooking time must be at most 1440 minutes")
    private Integer cookingTime;

    @Valid
    @NotEmpty(message = "At least one cooking step is required")
    private List<CreatePostStepRequestDTO> steps;
}
