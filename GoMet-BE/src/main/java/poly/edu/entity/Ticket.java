package poly.edu.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "Ticket")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer ticketID;

    @ManyToOne
    @JoinColumn(name = "AccountID")
    @JsonIgnore
    private Account account;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "NVARCHAR(MAX)")
    private String description;

    private String attachment;

    @Column(nullable = false)
    private Boolean isRead;

    @Column(nullable = false)
    private LocalDateTime createdAt;
}