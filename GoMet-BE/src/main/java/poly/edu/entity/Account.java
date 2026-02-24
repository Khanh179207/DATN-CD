package poly.edu.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

    // ✅ Email unique
    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    private String avatar;

    // OTP
    @Transient
    private String otp;

    @Transient
    private LocalDate otpExpiredAt;


    // ✅ Token chỉ có sau LOGIN → cho phép null
    @Column
    private String token;

    @Column(nullable = false)
    private Integer point = 0;

    @Column(nullable = false)
    private Integer isAdmin = 0;

    @Column(nullable = false)
    private Integer isPremium = 0;

    @Column(nullable = false)
    private Integer isActive = 1;

    @Column(nullable = false)
    private LocalDate createdAt;

    private LocalDate updatedAt;
    private LocalDate deletedAt;

    // ================= RELATIONSHIPS =================

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
    private List<Error> errors;

    @JsonIgnore
    @OneToMany(mappedBy = "account")
    private List<Report> reports;
}
