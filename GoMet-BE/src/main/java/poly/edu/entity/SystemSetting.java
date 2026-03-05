package poly.edu.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "SystemSetting")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SystemSetting {

    @Id
    @Column(name = "[key]", nullable = false, length = 50)
    private String key;

    @Column(name = "[value]", nullable = false, length = 255)
    private String value;

    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
