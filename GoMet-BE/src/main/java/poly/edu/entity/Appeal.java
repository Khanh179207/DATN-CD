package poly.edu.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "Appeals")
public class Appeal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "AppealID")
    private Integer appealID;

    @Column(name = "Email", nullable = false, length = 254)
    private String email;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "AccountID")
    private Account account;

    @Column(name = "Reason", nullable = false, columnDefinition = "NVARCHAR(MAX)")
    private String reason;

    @Column(name = "Status", nullable = false, length = 50)
    private String status; // Pending, Resolved, Rejected

    @Column(name = "CreatedAt", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "ResolvedAt")
    private LocalDateTime resolvedAt;

    @Column(name = "Note", columnDefinition = "NVARCHAR(MAX)")
    private String note;

    // 🔥 MỚI THÊM: Cột lưu Admin vào DB
    @Column(name = "AdminID")
    private Integer adminId;

    @Column(name = "AdminName", length = 255)
    private String adminName;

    public Appeal() {
        this.status = "Pending";
        this.createdAt = LocalDateTime.now();
    }

    public Appeal(String email, String reason, Account account) {
        this();
        this.email = email;
        this.reason = reason;
        this.account = account;
    }

    // ================= Getters & Setters =================

    public Integer getAppealID() { return appealID; }
    public void setAppealID(Integer appealID) { this.appealID = appealID; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Account getAccount() { return account; }
    public void setAccount(Account account) { this.account = account; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getResolvedAt() { return resolvedAt; }
    public void setResolvedAt(LocalDateTime resolvedAt) { this.resolvedAt = resolvedAt; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public Integer getAdminId() { return adminId; }
    public void setAdminId(Integer adminId) { this.adminId = adminId; }

    public String getAdminName() { return adminName; }
    public void setAdminName(String adminName) { this.adminName = adminName; }
}