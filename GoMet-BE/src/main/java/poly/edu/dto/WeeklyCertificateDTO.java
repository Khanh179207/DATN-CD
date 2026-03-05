package poly.edu.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeeklyCertificateDTO {
    private Integer id;
    private LocalDate weekStart;
    private LocalDate weekEnd;
    private Integer userId;
    private String username;
    private String avatar;
    private Integer rank;
    private Integer score;
    private String title;
    private LocalDateTime issuedAt;
    private String certificateCode;
}
