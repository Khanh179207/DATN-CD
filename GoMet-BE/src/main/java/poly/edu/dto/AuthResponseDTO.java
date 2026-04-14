package poly.edu.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AuthResponseDTO {
    @JsonProperty("accountID")
    private Integer accountID;

    @JsonProperty("username")
    private String username;

    @JsonProperty("email")
    private String email;

    @JsonProperty("avatar")
    private String avatar;

    @JsonProperty("isAdmin")
    private Integer isAdmin;

    @JsonProperty("isPremium")
    private Integer isPremium;

    @JsonProperty("token")
    private String token;

    @JsonProperty("point")
    private Integer point;
}
