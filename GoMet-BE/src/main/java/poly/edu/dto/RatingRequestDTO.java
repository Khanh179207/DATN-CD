package poly.edu.dto;

import lombok.Data;

import java.util.List;

@Data
public class RatingRequestDTO {
    private Integer accountID;
    private Integer postID;
    private Integer rate;
    private String comment;
    private List<String> imageUrls;
}
