package poly.edu.entity;

import jakarta.persistence.*;
import lombok.*;
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
    @Column(name = "SubID")
    private Integer subID;

    @ManyToOne
    @JoinColumn(name = "AccountID", nullable = false)
    private Account account;

    @Column(name = "PlanType", nullable = false)
    private Integer planType;

    @Column(name = "StartAt", nullable = false)
    private LocalDateTime startAt;

    @Column(name = "EndAt", nullable = false)
    private LocalDateTime endAt;

    @Column(name = "IsActive", nullable = false)
    private Integer isActive;

    // 🔥 MỚI: Móc nối với bảng Hóa đơn thanh toán (TransactionID)
    @OneToOne
    @JoinColumn(name = "TransactionID")
    private PaymentTransaction paymentTransaction;
}