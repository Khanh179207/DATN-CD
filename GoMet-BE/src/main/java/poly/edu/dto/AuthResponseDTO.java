package poly.edu.dto;

import lombok.Data;

@Data
public class AuthResponseDTO {
    private Integer accountID;
    private String username;
    private String email;
    private String avatar;
    private Integer isAdmin;
    private Integer isPremium;
    private String token;
}
