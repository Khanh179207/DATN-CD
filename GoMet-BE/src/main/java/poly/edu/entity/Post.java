package poly.edu.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "Post")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer postID;

    @ManyToOne
    @JoinColumn(name = "AccountID", nullable = false)
    private Account account;

    @ManyToOne
    @JoinColumn(name = "CategoryID", nullable = false)
    private Category category;

    @ManyToOne
    @JoinColumn(name = "EventID")
    private Event event;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "NVARCHAR(MAX)")
    private String description;

    @Column(nullable = false, columnDefinition = "NVARCHAR(MAX)")
    private String ingredients;

    private String media;

    private String video;

    @Column(nullable = false)
    private Integer level;

    @Column(nullable = false)
    private Integer cookingTime;

    @Column(nullable = false)
    private Integer views;

    @Column(nullable = false)
    private Integer isActive;

    @Column(nullable = false)
    private Integer isApproved;

    @Column(nullable = false)
    private LocalDate createdAt;

    // Relationships
    @OneToMany(mappedBy = "post")
    private List<Comment> comments;

    @OneToMany(mappedBy = "post")
    private List<Rating> ratings;

    @OneToMany(mappedBy = "post")
    private List<Favorite> favorites;

    @OneToMany(mappedBy = "post")
    private List<History> histories;

    @OneToMany(mappedBy = "post")
    private List<CookingSteps> cookingSteps;

    @OneToMany(mappedBy = "post")
    private List<Notification> notifications;

    // Thay vì mappedBy = "account", sếp phải để là "targetPost"
    @OneToMany(mappedBy = "targetPost")
    private List<Ticket> tickets;

    @Column(name = "LikeCount")
    private Integer likeCount = 0; // Thêm dòng này vào

}
