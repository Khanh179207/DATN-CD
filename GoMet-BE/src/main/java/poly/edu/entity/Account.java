package poly.edu.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
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
    private LocalDate createdAt;

    private LocalDate updatedAt;
    private LocalDate deletedAt;

    // Relationships

    @OneToMany(mappedBy = "account")
    private List<Post> posts;

    @OneToMany(mappedBy = "account")
    private List<Comment> comments;

    @OneToMany(mappedBy = "account")
    private List<Rating> ratings;

    @OneToMany(mappedBy = "account")
    private List<Favorite> favorites;

    @OneToMany(mappedBy = "follower")
    private List<Follow> following;

    @OneToMany(mappedBy = "followee")
    private List<Follow> followers;

    @OneToMany(mappedBy = "account")
    private List<History> histories;

    @OneToMany(mappedBy = "account")
    private List<Notification> notifications;

    @OneToMany(mappedBy = "account")
    private List<Subscription> subscriptions;

    @OneToMany(mappedBy = "account")
    private List<UserAchievement> userAchievements;

    @OneToMany(mappedBy = "account")
    private List<Error> errors;

    @OneToMany(mappedBy = "account")
    private List<Report> reports;

}
