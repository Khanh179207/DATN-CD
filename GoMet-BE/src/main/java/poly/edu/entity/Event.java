package poly.edu.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Data
@Builder // 🔥 Thêm cái này để fix lỗi AdminEventServiceImpl
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
    @JsonIgnore // 🔥 Thêm cái này để fix lỗi getEventPosts() trong EventServiceImpl
    private List<EventPosts> eventPosts;

    // Thêm 3 dòng này vào file Event.java
    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String description;

    @Column(name = "reward", length = 255, columnDefinition = "NVARCHAR(255)")
    private String reward;

    @Column(name = "MaxVotes")
    private Integer maxVotes;

    @Column(name = "IsActive")
    private Integer isActive; // Đổi từ Boolean sang Integer

    @Column(name = "IsForceEnded")
    private Integer isForceEnded; // Đổi từ Boolean sang Integer
}