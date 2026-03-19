package poly.edu.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "PaymentTransaction")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TransactionID")
    private Integer transactionID;

    // 🔥 SỬA: Dùng Object Account thay vì Integer accountID để JPA tự động join bảng
    @ManyToOne
    @JoinColumn(name = "AccountID", nullable = false)
    private Account account;

    @Column(name = "OrderCode", nullable = false, unique = true)
    private String orderCode;

    @Column(name = "Amount", nullable = false)
    private Integer amount;

    @Column(name = "PlanType", nullable = false)
    private Integer planType;

    @Column(name = "Status")
    private String status = "PENDING";

    // 🔥 SỬA: Đồng bộ dùng LocalDateTime giống bảng Subscription
    @Column(name = "CreatedAt", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "PaidAt")
    private LocalDateTime paidAt;
}