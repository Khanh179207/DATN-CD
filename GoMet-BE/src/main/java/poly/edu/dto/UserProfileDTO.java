package poly.edu.dto;

import lombok.Data;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class UserProfileDTO {
    private Integer accountID;
    private String username;
    private String email;
    private String avatar;
    private String bio;
    private String profileBannerUrl;
    private String premiumAvatarFrame;
    private String premiumThemePrimary;
    private String premiumThemeSecondary;
    private String premiumThemeAccent;
    private String premiumPostCardBackground;
    private String profileSocialLinksJson;
    private String premiumShareCardStyle;
    private Integer point;
    private Integer isPremium;
    private Integer isAdmin;
    private LocalDateTime createdAt;
    private Long postCount;
    private Long followerCount;
    private Long followingCount;
    private Long totalViews;
    private Double avgRating;

    /** UTC timestamp of last successful login. */
    private Instant lastLoginAt;

    /** IP address of last successful login. */
    private String lastLoginIp;

    /** 0 = MFA disabled, 1 = MFA enabled. */
    private Integer mfaEnabled;
}
