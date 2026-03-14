package poly.edu.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "Subscription")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer subID;

    @ManyToOne
    @JoinColumn(name = "AccountID", nullable = false)
    private Account account;

    @Column(nullable = false)
    private Integer planType;

    // Sửa trong file Subscription.java
    @Column(name = "StartAt")
    private LocalDateTime startAt; // Đổi từ Date hoặc LocalDate sang LocalDateTime

    @Column(name = "EndAt")
    private LocalDateTime endAt; // Đổi từ Date hoặc LocalDate sang LocalDateTime

    @Column(nullable = false)
    private Integer isActive;
}
