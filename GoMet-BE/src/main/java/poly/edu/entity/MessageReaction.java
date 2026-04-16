package poly.edu.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import java.util.Date;

@Data
@Entity
@Table(name = "MessageReaction")
public class MessageReaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer reactionID;

    @ManyToOne
    @JoinColumn(name = "MessageID")
    private Message message;

    @ManyToOne
    @JoinColumn(name = "AccountID")
    @JsonIgnoreProperties({"posts", "comments", "ratings", "favorites", "following", "followers", "histories", "notifications", "subscriptions", "errors", "reports", "password", "token"})
    private Account user;

    @Column(length = 10)
    private String emoji; // Lưu "❤️", "👍", "😂"...

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt = new Date();
}