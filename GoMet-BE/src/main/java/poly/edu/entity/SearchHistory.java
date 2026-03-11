package poly.edu.entity;

import com.fasterxml.jackson.annotation.JsonIgnore; // 🔥 BƯỚC 1: Thêm import này
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "SearchHistory")
@Data
public class SearchHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SearchID")
    private Integer searchId;

    // 🔥 BƯỚC 2: Thêm @JsonIgnore để khi trả về JSON cho Vue, nó không lôi cả bảng Account theo
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "AccountID", nullable = false)
    private Account account;

    @Column(name = "Keyword", nullable = false)
    private String keyword;

    @Column(name = "SearchedAt")
    private LocalDateTime searchedAt;

    @PrePersist
    @PreUpdate
    protected void onSave() {
        searchedAt = LocalDateTime.now();
    }
}