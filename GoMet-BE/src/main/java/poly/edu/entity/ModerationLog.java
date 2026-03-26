package poly.edu.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "ModerationLog")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModerationLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "LogID")
    private Integer logID;

    @Column(name = "TargetID", nullable = false)
    private Integer targetID; // ID của User, Post hoặc Comment bị xử lý

    @Column(name = "TargetType", nullable = false)
    private String targetType; // 'ACCOUNT', 'POST', 'COMMENT'

    @Column(name = "Action", nullable = false)
    private String action; // 'BAN', 'UNBAN', 'REJECT', 'HIDE', 'DELETE'

    @Column(name = "AdminID", nullable = false)
    private Integer adminID; // ID của Admin ra tay

    @Column(name = "AdminName")
    private String adminName; // Lưu luôn tên Admin cho lẹ, đỡ phải Join bảng lúc truy vấn

    @Column(name = "Reason", columnDefinition = "NVARCHAR(500)")
    private String reason; // Lý do xử lý (VD: "Spam link web cá độ")

    @Column(name = "CreatedAt", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}