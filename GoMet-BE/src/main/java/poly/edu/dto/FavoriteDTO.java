package poly.edu.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteDTO {
    private Integer favoriteID;
    private Integer postID;
    private String title;
    private String media;
    private Integer cookingTime;
    private Integer level;
    private String categoryName;

    private Double rating;
    private Integer views;
    private String userName;
    private String avatar;
}
