package poly.edu.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import java.util.Date;
import java.util.List; // 🚀 Thêm import List

@Data
@Entity
@Table(name = "Message")
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer messageID;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ManyToOne
    @JoinColumn(name = "ConversationID")
    private Conversation conversation;

    @JsonIgnoreProperties({"posts", "comments", "ratings", "favorites", "following", "followers", "histories", "notifications", "subscriptions", "errors", "reports", "password", "token"})
    @ManyToOne
    @JoinColumn(name = "SenderID")
    private Account sender;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String content;

    private Integer isRead = 0;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt = new Date();

    // 🚀 1. TÍNH NĂNG REPLY (TRÍCH DẪN TIN NHẮN CHA)
// 🚀 1. TÍNH NĂNG REPLY (TRÍCH DẪN TIN NHẮN CHA)
    @ManyToOne
    @JoinColumn(name = "ParentID")
    // ĐÃ XÓA "sender" ĐỂ FE LẤY ĐƯỢC TÊN NGƯỜI BỊ TRÍCH DẪN
    @JsonIgnoreProperties({"parent", "conversation", "reactions"})
    private Message parent;
    // 🚀 2. TÍNH NĂNG REACTION (THẢ CẢM XÚC)
    // Dùng EAGER để khi load tin nhắn là lấy luôn danh sách thả tim cho nhanh
    @OneToMany(mappedBy = "message", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonIgnoreProperties("message") // Tránh việc reaction lại trỏ ngược về message gây lặp
    private List<MessageReaction> reactions;
}