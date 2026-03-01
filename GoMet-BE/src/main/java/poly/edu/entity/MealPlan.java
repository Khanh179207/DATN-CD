package poly.edu.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "MealPlan")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MealPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PlanID")
    private Integer planId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "AccountID", nullable = false)
    private Account account;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PostID")
    private Post post;

    @Column(name = "CustomMealName", length = 255)
    private String customMealName;

    @Column(name = "PlanDate", nullable = false)
    private LocalDate planDate;

    // 'BREAKFAST', 'LUNCH', 'DINNER', 'SNACK'
    @Column(name = "MealType", nullable = false, length = 50)
    private String mealType;

    @Column(name = "Notes", columnDefinition = "NVARCHAR(MAX)")
    private String notes;

    @Column(name = "IsCompleted")
    private Integer isCompleted = 0;

    @Column(name = "CreatedAt", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.isCompleted == null) this.isCompleted = 0;
    }
}