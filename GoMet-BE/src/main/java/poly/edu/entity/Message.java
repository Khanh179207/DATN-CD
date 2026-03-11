package poly.edu.entity;

import com.fasterxml.jackson.annotation.JsonProperty; // <-- Thêm dòng này
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import java.util.Date;

@Data
@Entity
@Table(name = "Message")
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer messageID;

    // --- THAY ĐỔI Ở ĐÂY ---
    // Cho phép Frontend gửi ID lên (WRITE_ONLY),
    // nhưng không gửi ngược đối tượng này về Frontend để tránh vòng lặp vô tận.
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ManyToOne
    @JoinColumn(name = "ConversationID")
    private Conversation conversation;

    // Giữ nguyên phần Account
    @JsonIgnoreProperties({"posts", "comments", "ratings", "favorites", "following", "followers", "histories", "notifications", "subscriptions", "userAchievements", "errors", "reports", "password", "token"})
    @ManyToOne
    @JoinColumn(name = "SenderID")
    private Account sender;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String content;

    private Integer isRead = 0;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt = new Date();
}