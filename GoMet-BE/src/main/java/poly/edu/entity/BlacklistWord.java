package poly.edu.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "BlacklistWord")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BlacklistWord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "WordID")
    private Integer wordID;

    @Column(name = "Word")
    private String word;

    @Column(name = "CreatedAt")
    private LocalDateTime createdAt = LocalDateTime.now();
}