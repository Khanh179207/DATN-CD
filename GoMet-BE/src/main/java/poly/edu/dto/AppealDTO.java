package poly.edu.dto;

import java.time.LocalDateTime;

public class AppealDTO {
    private Integer appealID;
    private String email;
    private Integer accountID;
    private String reason;
    private String status;
    private String note;
    private LocalDateTime createdAt;
    private LocalDateTime resolvedAt;

    // 🔥 MỚI THÊM: Lưu thông tin Admin hiển thị lên phiếu
    private Integer adminId;
    private String adminName;

    public AppealDTO() {}

    // Getters & Setters
    public Integer getAppealID() { return appealID; }
    public void setAppealID(Integer appealID) { this.appealID = appealID; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Integer getAccountID() { return accountID; }
    public void setAccountID(Integer accountID) { this.accountID = accountID; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getResolvedAt() { return resolvedAt; }
    public void setResolvedAt(LocalDateTime resolvedAt) { this.resolvedAt = resolvedAt; }

    public Integer getAdminId() { return adminId; }
    public void setAdminId(Integer adminId) { this.adminId = adminId; }

    public String getAdminName() { return adminName; }
    public void setAdminName(String adminName) { this.adminName = adminName; }
}