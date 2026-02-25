package poly.edu.dto;

import lombok.Data;

@Data
public class RatingRequestDTO {
    private Integer accountID;
    private Integer postID;
    private Integer rate;
}
