package poly.edu.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Votes")
public class Votes {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer voteID;

    @ManyToOne
    @JoinColumn(name = "AccountID")
    private Account account;

    @ManyToOne
    @JoinColumn(name = "EventPostID")
    private EventPosts eventPost;

    private LocalDateTime createdAt = LocalDateTime.now();
}