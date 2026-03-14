package poly.edu.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.Date;

@Data
@Entity
@Table(name = "PaymentTransaction")
public class PaymentTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TransactionID")
    private Integer transactionID;

    @Column(name = "AccountID", nullable = false)
    private Integer accountID;

    @Column(name = "OrderCode", nullable = false, unique = true)
    private String orderCode;

    @Column(name = "Amount", nullable = false)
    private Integer amount;

    @Column(name = "PlanType", nullable = false)
    private Integer planType;

    @Column(name = "Status")
    private String status = "PENDING";

    @Column(name = "CreatedAt", insertable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Column(name = "PaidAt")
    @Temporal(TemporalType.TIMESTAMP)
    private Date paidAt;
}