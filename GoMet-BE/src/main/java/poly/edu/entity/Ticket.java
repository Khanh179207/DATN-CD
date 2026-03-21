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
    // @JsonIgnore  <-- Sếp tạm thời comment hoặc xóa dòng này đi
    private Account account;

    @Column(nullable = false, length = 50)
    private String ticketType;

    @ManyToOne
    @JoinColumn(name = "TargetPostID")
    // @JsonIgnore <-- Sếp tạm thời comment hoặc xóa dòng này đi
    private Post targetPost;

    // 🔥 HELPER: Tạo "cổng phụ" để FE gọi .username và .targetPostID dễ dàng
    @Transient // Không lưu xuống DB, chỉ dùng để trả dữ liệu về cho FE
    public String getUsername() {
        return account != null ? account.getUsername() : "Người dùng ẩn";
    }

    @Transient
    public Integer getTargetPostId() {
        return targetPost != null ? targetPost.getPostID() : null;
    }

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "NVARCHAR(MAX)")
    private String description;

    @Column(length = 500)
    private String attachment;

    @Column(nullable = false)
    private Integer status; // 0: Pending, 1: Processing, 2: Resolved, 3: Rejected

    // Ngày người dùng gửi
    @Column(nullable = false)
    private LocalDateTime createdAt;

    // 🔥 Ngày Admin bấm nút "Tiếp nhận" (MỚI)
    private LocalDateTime processedAt;

    // Ngày Admin bấm nút "Giải quyết xong"
    private LocalDateTime resolvedAt;
}