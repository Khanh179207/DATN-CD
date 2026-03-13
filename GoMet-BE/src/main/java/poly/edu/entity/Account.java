package poly.edu.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
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

    @Column(columnDefinition = "NVARCHAR(500)")
    private String bio;

    @Column(name = "profile_banner_url", length = 1000)
    private String profileBannerUrl;

    @Column(name = "premium_avatar_frame", length = 32)
    private String premiumAvatarFrame;

    @Column(name = "premium_theme_primary", length = 32)
    private String premiumThemePrimary;

    @Column(name = "premium_theme_secondary", length = 32)
    private String premiumThemeSecondary;

    @Column(name = "premium_theme_accent", length = 32)
    private String premiumThemeAccent;

    @Column(name = "premium_post_card_background", length = 32)
    private String premiumPostCardBackground;

    @Column(name = "profile_social_links_json", columnDefinition = "NVARCHAR(MAX)")
    private String profileSocialLinksJson;

    @Column(name = "premium_share_card_style", length = 32)
    private String premiumShareCardStyle;

    /** Legacy UUID bearer token — kept for backward-compat during JWT migration. */
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

    // ─── Security fields (Phase 0) ────────────────────────────────────────────

    /** UTC instant of the last password change; used to invalidate sessions issued before this time. */
    private Instant passwordUpdatedAt;

    /** Count of consecutive failed login attempts since last success. */
    @Column(nullable = false, columnDefinition = "INT DEFAULT 0")
    @Builder.Default
    private Integer failedLoginCount = 0;

    /** Account is locked until this time (progressive lockout). NULL = not locked. */
    private Instant lockUntil;

    /** 0 = TOTP disabled, 1 = TOTP enabled. */
    @Column(nullable = false, columnDefinition = "INT DEFAULT 0")
    @Builder.Default
    private Integer mfaEnabled = 0;

    /** AES-256 encrypted TOTP secret (Base64-encoded cipher text). */
    @Column(length = 512)
    private String mfaSecretEncrypted;

    /** When the current MFA secret was generated (rotation tracking). */
    private Instant mfaSecretCreatedAt;

    /** UTC instant of last successful login. */
    private Instant lastLoginAt;

    /** IP address of last successful login (max 45 chars for IPv6). */
    @Column(length = 45)
    private String lastLoginIp;

    /** Device fingerprint from last successful login. */
    private String lastLoginDeviceId;

    // ─── Relationships ────────────────────────────────────────────────────────

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

    // ─── Convenience helpers ──────────────────────────────────────────────────

    public boolean isLocked() {
        return lockUntil != null && lockUntil.isAfter(Instant.now());
    }

    public boolean isMfaEnabled() {
        return mfaEnabled != null && mfaEnabled == 1;
    }

    public boolean isAdminAccount() {
        return isAdmin != null && isAdmin == 1;
    }

    public boolean isAccountActive() {
        return isActive != null && isActive == 1;
    }
}
