package poly.edu.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContactDTO {
    private Integer accountID;
    private String username;
    private String avatar;
    private String email;
}
