package poly.edu.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/** Sent by the frontend to refresh an access token. */
@Data
public class RefreshTokenRequestDTO {

    @NotBlank(message = "refreshToken is required")
    private String refreshToken;

    /** Optional device fingerprint, used to update last-seen on the session. */
    private String deviceId;
}
