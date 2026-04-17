package poly.edu.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Event")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer eventID;

    private String eventName;
    private Integer winner;


    // Trong file Event.java
    @Column(name = "StartAt")
    private LocalDateTime startAt;

    @Column(name = "EndAt")
    private LocalDateTime endAt;

    @Column(name = "VoteStartAt")
    private LocalDateTime voteStartAt;

    @Column(name = "VoteEndAt")
    private LocalDateTime voteEndAt;

    private String bannerImage;

    @OneToMany(mappedBy = "event")
    @JsonIgnore
    private List<EventPosts> eventPosts;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String description;

    @Column(name = "reward", length = 255, columnDefinition = "NVARCHAR(255)")
    private String reward;

    @Column(name = "MaxVotes") // 🔥 Sếp sửa đúng tên cột có từ đầu của sếp ở đây
    private Integer maxVotes;

    @Column(name = "IsActive")
    private Integer isActive;

    @Column(name = "IsForceEnded")
    private Integer isForceEnded;
}