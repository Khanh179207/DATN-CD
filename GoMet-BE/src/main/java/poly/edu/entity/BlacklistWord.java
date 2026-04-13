package poly.edu.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "BlacklistWord")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BlacklistWord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "WordID")
    private Integer wordID;

    @Column(name = "Word", nullable = false, unique = true)
    private String word;

    @Column(name = "CreatedAt")
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}