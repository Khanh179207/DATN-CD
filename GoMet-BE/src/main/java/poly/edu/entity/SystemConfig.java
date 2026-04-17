package poly.edu.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "SystemConfig")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SystemConfig {
    @Id
    @Column(name = "ConfigKey", length = 50)
    private String configKey;

    @Column(name = "ConfigValue", columnDefinition = "NVARCHAR(MAX)")
    private String configValue;

    @Column(name = "ConfigGroup", length = 50)
    private String configGroup;

    @Column(name = "Description")
    private String description;

    @Column(name = "UpdatedAt")
    private LocalDateTime updatedAt;
}