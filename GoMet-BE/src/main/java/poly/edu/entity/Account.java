package poly.edu.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "Account")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer accountID;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    private String avatar;


    private String bio;

    @Column(nullable = false)
    private String token;

    @Column(nullable = false)
    private Integer point;

    @Column(nullable = false)
    private Integer isAdmin;

    @Column(nullable = false)
    private Integer isPremium;

    @Column(nullable = false)
    private Integer isActive;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    // Relationships
    @JsonIgnore
    @OneToMany(mappedBy = "account")
    private List<Post> posts;

    @JsonIgnore
    @OneToMany(mappedBy = "account")
    private List<Comment> comments;

    @JsonIgnore
    @OneToMany(mappedBy = "account")
    private List<Rating> ratings;

    @JsonIgnore
    @OneToMany(mappedBy = "account")
    private List<Favorite> favorites;

    @JsonIgnore
    @OneToMany(mappedBy = "follower")
    private List<Follow> following;

    @JsonIgnore
    @OneToMany(mappedBy = "followee")
    private List<Follow> followers;

    @JsonIgnore
    @OneToMany(mappedBy = "account")
    private List<History> histories;

    @JsonIgnore
    @OneToMany(mappedBy = "account")
    private List<Notification> notifications;

    @JsonIgnore
    @OneToMany(mappedBy = "account")
    private List<Subscription> subscriptions;

    @JsonIgnore
    @OneToMany(mappedBy = "account")
    private List<UserAchievement> userAchievements;

    @JsonIgnore
    @OneToMany(mappedBy = "account")
    private List<Ticket> tickets;

}
