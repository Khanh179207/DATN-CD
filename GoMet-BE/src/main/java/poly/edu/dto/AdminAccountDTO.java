package poly.edu.dto;

import lombok.Data;

@Data
public class AdminAccountDTO {
    private Integer accountID;
    private String  username;
    private String  email;
    private String  password;    // only used on CREATE, never returned
    private String  avatar;
    private Integer isAdmin;
    private Integer isPremium;
    private Integer isActive;
    private Integer point;
    private String  createdAt;
    private String  role;        // "ADMIN" | "USER" — FE convenience field
}
