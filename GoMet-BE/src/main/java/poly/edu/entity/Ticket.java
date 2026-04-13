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
    @Column(name = "TicketID") // Đảm bảo khớp PascalCase với SQL
    private Integer ticketID;

    @ManyToOne
    @JoinColumn(name = "AccountID")
    @JsonIgnore // 🔥 ĐÃ MỞ LẠI PHONG ẤN: Chặn vòng lặp JSON vô tận
    private Account account;

    @Column(name = "TicketType", nullable = false, length = 50)
    private String ticketType;

    @ManyToOne
    @JoinColumn(name = "TargetPostID")
    @JsonIgnore // 🔥 ĐÃ MỞ LẠI PHONG ẤN: Ngăn Jackson mò sang bảng Post và Rating
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

    @Column(name = "Title", nullable = false)
    private String title;

    @Column(name = "Description", nullable = false, columnDefinition = "NVARCHAR(MAX)")
    private String description;

    @Column(name = "Attachment", length = 500)
    private String attachment;

    @Column(name = "Status", nullable = false)
    private Integer status = 0;

    @Column(name = "CreatedAt", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "ProcessedAt")
    private LocalDateTime processedAt;

    @Column(name = "ResolvedAt")
    private LocalDateTime resolvedAt;
}