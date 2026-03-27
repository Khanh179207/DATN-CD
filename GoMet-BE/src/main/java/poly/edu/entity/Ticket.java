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
    @Column(name = "TicketID")
    private Integer ticketID;

    @ManyToOne
    @JoinColumn(name = "AccountID")
    @JsonIgnore
    private Account account;

    @Column(name = "TicketType", nullable = false, length = 50)
    private String ticketType;

    @ManyToOne
    @JoinColumn(name = "TargetPostID")
    @JsonIgnore
    private Post targetPost;

    @Transient
    public String getUsername() {
        return account != null ? account.getUsername() : "Người dùng ẩn";
    }

    @Transient
    public Integer getTargetPostId() {
        return targetPost != null ? targetPost.getPostID() : null;
    }

    @Column(name = "Title", nullable = false)
    private String title;

    @Column(name = "Description", nullable = false, columnDefinition = "NVARCHAR(MAX)")
    private String description;

    @Column(name = "Attachment", length = 500)
    private String attachment;

    @Builder.Default // 🔥 Thêm cái này để hết Warning màu vàng lúc build sếp nhé
    @Column(name = "Status", nullable = false)
    private Integer status = 0;

    @Builder.Default
    @Column(name = "CreatedAt", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "ProcessedAt")
    private LocalDateTime processedAt;

    @Column(name = "ResolvedAt")
    private LocalDateTime resolvedAt;

    // =============================================================
    // 🔥 MỚI THÊM: ĐỒNG BỘ VỚI SQL ĐỂ LƯU THÔNG TIN ADMIN XỬ LÝ
    // =============================================================

    @Column(name = "AdminID")
    private Integer adminId;

    @Column(name = "AdminName")
    private String adminName;

    @Column(name = "AdminNote", columnDefinition = "NVARCHAR(MAX)")
    private String adminNote;
}