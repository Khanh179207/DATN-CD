package poly.edu.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "Event")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer eventID;

    @Column(nullable = false)
    private String eventName;

    private Integer winner;

    @Column(nullable = false)
    private LocalDate startAt;

    @Column(nullable = false)
    private LocalDate endAt;

    // Relationships

    @OneToMany(mappedBy = "event")
    private List<Post> posts;

    @OneToMany(mappedBy = "event")
    private List<EventPosts> eventPosts;

}
