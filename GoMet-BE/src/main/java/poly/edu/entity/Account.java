package poly.edu.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

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
    @Column(name = "AccountID")
    private Integer accountID;

    @Column(name = "Username", nullable = false)
    private String username;

    @Column(name = "Email", nullable = false)
    private String email;

    @Column(name = "Password", nullable = false)
    private String password;

    @Column(name = "Avatar")
    private String avatar;

    @Column(name = "Bio", columnDefinition = "NVARCHAR(MAX)")
    private String bio;

    @Column(name = "Point", nullable = false)
    @Builder.Default
    private Integer point = 0;

    @Column(name = "isAdmin", nullable = false)
    @Builder.Default
    private Integer isAdmin = 0;

    @Column(name = "isPremium", nullable = false)
    @Builder.Default
    private Integer isPremium = 0;

    @Column(name = "isActive", nullable = false)
    @Builder.Default
    private Integer isActive = 1;

    // 🔥 CHỈ GIỮ LẠI 2 CỘT NÀY ĐỂ BÁO LỖI CHO USER LÚC ĐĂNG NHẬP
    @Column(name = "BanReason", columnDefinition = "NVARCHAR(MAX)")
    private String banReason;

    @Column(name = "BannedAt")
    private LocalDateTime bannedAt;

    @Column(name = "CreatedAt", nullable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "UpdatedAt")
    private LocalDateTime updatedAt;

    @Column(name = "DeletedAt")
    private LocalDateTime deletedAt;

    // ================= RELATIONSHIPS =================

    @JsonIgnore
    @OneToMany(mappedBy = "account")
    private List<Post> posts;

    @JsonIgnore
    @OneToMany(mappedBy = "account")
    private List<Comment> comments;

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
    private List<Ticket> tickets;
}