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

    @Column(name = "Reason", nullable = false, columnDefinition = "NVARCHAR(MAX)")
    private String reason;

    @Column(name = "Status", nullable = false, length = 50)
    private String status; // Pending, Review, Resolved, Rejected

    @Column(name = "CreatedAt", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "UpdatedAt")
    private LocalDateTime updatedAt;

    @Column(name = "CreatedByIP", length = 45)
    private String ipAddress;

    @Column(name = "Note", columnDefinition = "NVARCHAR(MAX)")
    private String note;

    // Constructors
    public Appeal() {
        this.status = "Pending";
        this.createdAt = LocalDateTime.now();
    }

    public Appeal(String email, String reason, String ipAddress) {
        this();
        this.email = email;
        this.reason = reason;
        this.ipAddress = ipAddress;
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

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    @Override
    public String toString() {
        return "Appeal{" +
                "appealID=" + appealID +
                ", email='" + email + '\'' +
                ", status='" + status + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
