package poly.edu.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "Error")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Error {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer errorID;

    @ManyToOne
    @JoinColumn(name = "AccountID")
    private Account account;

    @Column(nullable = false)
    private String errorName;

    @Column(nullable = false, columnDefinition = "NVARCHAR(MAX)")
    private String description;

    @Column(nullable = false)
    private LocalDate createdAt;
}
