package poly.edu.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CreatePostRequestDTO {
    private Integer accountID;
    private Integer categoryID;
    private String title;
    private String description;
    private String ingredients;
    private String media;
    private Integer level;
    private Integer cookingTime;
    private List<CreatePostStepRequestDTO> steps = new ArrayList<>();
}