package poly.edu.dto;

import lombok.Data;

@Data
public class ResetPasswordRequestDTO {
    /** The raw token from the email link query-string. */
    private String token;
    /** New plain password from user — will be BCrypt-hashed before storing. */
    private String newPassword;
}
