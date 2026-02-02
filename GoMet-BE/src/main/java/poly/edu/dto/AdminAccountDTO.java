package poly.edu.dto;

import lombok.Data;

@Data
public class AdminAccountDTO {
    private Integer accountID;
    private String username;
    private String email;
    private String password;
    private String avatar;
    private Integer isPremium;
    private Integer isActive;
}
