package poly.edu.dto;

import java.time.LocalDateTime;

public class AppealDTO {
    private Integer appealID;
    private String email;
    private String reason;
    private String status;
    private String note;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String ipAddress;

    public AppealDTO() {}

    public AppealDTO(Integer appealID, String email, String reason, String status, LocalDateTime createdAt) {
        this.appealID = appealID;
        this.email = email;
        this.reason = reason;
        this.status = status;
        this.createdAt = createdAt;
    }

    // Getters & Setters
    public Integer getAppealID() {
        return appealID;
    }

    public void setAppealID(Integer appealID) {
        this.appealID = appealID;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
}
