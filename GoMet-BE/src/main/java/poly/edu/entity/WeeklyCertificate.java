package poly.edu.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "WeeklyCertificate",
    uniqueConstraints = {
        @UniqueConstraint(name = "UQ_WeeklyCertificate_Code", columnNames = {"certificateCode"}),
        @UniqueConstraint(name = "UQ_WeeklyCertificate_Week_User", columnNames = {"weekStart", "userId"})
    }
)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeeklyCertificate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private LocalDate weekStart;

    @Column(nullable = false)
    private LocalDate weekEnd;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", nullable = false)
    private Account user;

    @Column(name = "[rank]", nullable = false)
    private Integer rank;

    @Column(nullable = false)
    private Integer score;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false)
    private LocalDateTime issuedAt;

    @Column(nullable = false, unique = true, length = 50)
    private String certificateCode;
}
