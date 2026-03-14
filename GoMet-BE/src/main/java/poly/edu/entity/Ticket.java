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

    // 🔥 NEW: BUG, REPORT, FEEDBACK
    @Column(nullable = false, length = 50)
    private String ticketType;

    // 🔥 NEW: Liên kết tới bài viết nếu type là REPORT
    @ManyToOne
    @JoinColumn(name = "TargetPostID")
    @JsonIgnore
    private Post targetPost;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "NVARCHAR(MAX)")
    private String description;

    @Column(length = 500)
    private String attachment;

    // 🔥 CHANGED: 0 = Pending, 1 = Processing, 2 = Resolved, 3 = Rejected
    @Column(nullable = false)
    private Integer status;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    // 🔥 NEW: Thời gian Admin xử lý xong
    private LocalDateTime resolvedAt;
}