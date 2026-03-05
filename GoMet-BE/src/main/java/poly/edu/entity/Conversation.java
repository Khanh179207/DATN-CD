package poly.edu.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.Date;

@Data
@Entity
@Table(name = "Conversation")
public class Conversation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer conversationID;

    @ManyToOne
    @JoinColumn(name = "UserOneID")
    private Account userOne;

    @ManyToOne
    @JoinColumn(name = "UserTwoID")
    private Account userTwo;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt = new Date();
}