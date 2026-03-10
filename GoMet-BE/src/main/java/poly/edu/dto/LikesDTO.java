package poly.edu.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LikesDTO {
    private Integer likeID;
    private Integer accountID;
    private String username;
    private String avatar;
    private Integer postID;
    private LocalDateTime createdAt;
}