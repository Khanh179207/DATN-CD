package poly.edu.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VoteDTO {  

    // 🔥 Ép Spring Boot đọc chuẩn xác chữ "accountID" (viết hoa chữ ID)
    @JsonProperty("accountID")
    private Integer accountID;

    // 🔥 Ép Spring Boot đọc chuẩn xác chữ "eventPostID"
    @JsonProperty("eventPostID")
    private Integer eventPostID;
}