package poly.edu.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import poly.edu.dao.AccountDAO;
import poly.edu.dao.FollowDAO;
import poly.edu.dao.PostDAO;
import poly.edu.dto.UserProfileDTO;
import poly.edu.entity.Account;
import poly.edu.service.EmailService;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@CrossOrigin
public class UserController {

    private static final Set<String> PREMIUM_PROFILE_KEYS = Set.of(
            "profileBannerUrl",
            "premiumAvatarFrame",
            "premiumThemePrimary",
            "premiumThemeSecondary",
            "premiumThemeAccent",
            "premiumPostCardBackground",
            "profileSocialLinksJson",
            "premiumShareCardStyle"
    );

    private static final Set<String> PREMIUM_AVATAR_FRAMES = Set.of("gold", "aurora", "neon", "cosmic");
        private static final Set<String> PREMIUM_SHARE_CARD_STYLES = Set.of("spotlight", "stacked", "minimal", "creator");

    private final BCryptPasswordEncoder passwordEncoder;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final AccountDAO accountDAO;
    private final PostDAO postDAO;
    private final FollowDAO followDAO;
    private final EmailService emailService;

    @GetMapping("/{id}")
    public ResponseEntity<UserProfileDTO> getProfile(@PathVariable Integer id) {
        return accountDAO.findById(id).map(acc -> {
            UserProfileDTO dto = toDTO(acc);
            return ResponseEntity.ok(dto);
        }).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProfile(
            @PathVariable Integer id,
            @RequestBody Map<String, String> body) {
        Account acc = accountDAO.findById(id).orElse(null);
        if (acc == null) return ResponseEntity.notFound().build();

        List<String> changed = new ArrayList<>();
        boolean premiumProfileChanged = false;
        boolean isPremiumMember = Integer.valueOf(1).equals(acc.getIsPremium());

        if (!isPremiumMember && body.keySet().stream().anyMatch(PREMIUM_PROFILE_KEYS::contains)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                    "message", "Premium membership is required to customize banner, theme, avatar frame, and social links."
            ));
        }

        try {
            if (body.containsKey("username")) {
                String username = normalizeText(body.get("username"), 50, "Display name must be 1-50 characters.");
                if (!Objects.equals(username, acc.getUsername())) {
                    acc.setUsername(username);
                    changed.add("Display name (username)");
                }
            }
            if (body.containsKey("avatar")) {
                String avatar = normalizeUrl(body.get("avatar"), 1000, "Avatar must be a valid HTTP/HTTPS URL.");
                if (!Objects.equals(avatar, acc.getAvatar())) {
                    acc.setAvatar(avatar);
                    changed.add("Profile photo (avatar)");
                }
            }
            if (body.containsKey("bio")) {
                String newBio = normalizeNullableText(body.get("bio"), 300, "Bio must be 300 characters or fewer.");
                if (!Objects.equals(newBio, acc.getBio())) {
                    acc.setBio(newBio);
                    changed.add("Bio / About me");
                }
            }
            if (body.containsKey("password") && body.get("password") != null && !body.get("password").isBlank()) {
                if (body.get("password").length() < 8) {
                    return ResponseEntity.badRequest().body(Map.of("message", "Password must be at least 8 characters."));
                }
                acc.setPassword(passwordEncoder.encode(body.get("password")));
                changed.add("Password");
            }

            if (isPremiumMember) {
                if (body.containsKey("profileBannerUrl")) {
                    String bannerUrl = normalizeUrl(body.get("profileBannerUrl"), 1000, "Profile banner must be a valid HTTP/HTTPS URL.");
                    if (!Objects.equals(bannerUrl, acc.getProfileBannerUrl())) {
                        acc.setProfileBannerUrl(bannerUrl);
                        premiumProfileChanged = true;
                    }
                }
                if (body.containsKey("premiumAvatarFrame")) {
                    String avatarFrame = normalizeAvatarFrame(body.get("premiumAvatarFrame"));
                    if (!Objects.equals(avatarFrame, acc.getPremiumAvatarFrame())) {
                        acc.setPremiumAvatarFrame(avatarFrame);
                        premiumProfileChanged = true;
                    }
                }
                if (body.containsKey("premiumThemePrimary")) {
                    String value = normalizeHexColor(body.get("premiumThemePrimary"), "Primary theme color must be a hex color like #F59E0B.");
                    if (!Objects.equals(value, acc.getPremiumThemePrimary())) {
                        acc.setPremiumThemePrimary(value);
                        premiumProfileChanged = true;
                    }
                }
                if (body.containsKey("premiumThemeSecondary")) {
                    String value = normalizeHexColor(body.get("premiumThemeSecondary"), "Secondary theme color must be a hex color like #FB7185.");
                    if (!Objects.equals(value, acc.getPremiumThemeSecondary())) {
                        acc.setPremiumThemeSecondary(value);
                        premiumProfileChanged = true;
                    }
                }
                if (body.containsKey("premiumThemeAccent")) {
                    String value = normalizeHexColor(body.get("premiumThemeAccent"), "Accent color must be a hex color like #111827.");
                    if (!Objects.equals(value, acc.getPremiumThemeAccent())) {
                        acc.setPremiumThemeAccent(value);
                        premiumProfileChanged = true;
                    }
                }
                if (body.containsKey("premiumPostCardBackground")) {
                    String value = normalizeHexColor(body.get("premiumPostCardBackground"), "Post card background must be a hex color like #FFF7ED.");
                    if (!Objects.equals(value, acc.getPremiumPostCardBackground())) {
                        acc.setPremiumPostCardBackground(value);
                        premiumProfileChanged = true;
                    }
                }
                if (body.containsKey("profileSocialLinksJson")) {
                    String value = normalizeSocialLinksJson(body.get("profileSocialLinksJson"));
                    if (!Objects.equals(value, acc.getProfileSocialLinksJson())) {
                        acc.setProfileSocialLinksJson(value);
                        premiumProfileChanged = true;
                    }
                }
                if (body.containsKey("premiumShareCardStyle")) {
                    String value = normalizeShareCardStyle(body.get("premiumShareCardStyle"));
                    if (!Objects.equals(value, acc.getPremiumShareCardStyle())) {
                        acc.setPremiumShareCardStyle(value);
                        premiumProfileChanged = true;
                    }
                }
            }
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("message", ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Invalid premium social links payload."));
        }

        if (premiumProfileChanged) {
            changed.add("Premium profile styling");
        }

        accountDAO.save(acc);

        // Send email notification only when at least one field actually changed
        if (!changed.isEmpty() && acc.getEmail() != null) {
            emailService.sendProfileUpdateEmail(acc.getEmail(), acc.getUsername(), changed);
        }

        return ResponseEntity.ok(toDTO(acc));
    }

    @GetMapping("/{id}/stats")
    public ResponseEntity<?> getStats(@PathVariable Integer id) {
        Account acc = accountDAO.findById(id).orElse(null);
        if (acc == null) return ResponseEntity.notFound().build();

        long postCount = postDAO.countByAccountId(id);
        long followerCount = followDAO.countByFollowee_AccountIDAndStatus(id, 1);
        long followingCount = followDAO.countByFollower_AccountIDAndStatus(id, 1);

        return ResponseEntity.ok(Map.of(
                "postCount", postCount,
                "followerCount", followerCount,
                "followingCount", followingCount,
                "point", acc.getPoint()
        ));
    }

    private UserProfileDTO toDTO(Account acc) {
        UserProfileDTO dto = new UserProfileDTO();
        dto.setAccountID(acc.getAccountID());
        dto.setUsername(acc.getUsername());
        dto.setEmail(acc.getEmail());
        dto.setAvatar(acc.getAvatar());
        dto.setPoint(acc.getPoint());
        dto.setIsPremium(acc.getIsPremium());
        dto.setIsAdmin(acc.getIsAdmin());
        dto.setBio(acc.getBio());
        dto.setProfileBannerUrl(acc.getProfileBannerUrl());
        dto.setPremiumAvatarFrame(acc.getPremiumAvatarFrame());
        dto.setPremiumThemePrimary(acc.getPremiumThemePrimary());
        dto.setPremiumThemeSecondary(acc.getPremiumThemeSecondary());
        dto.setPremiumThemeAccent(acc.getPremiumThemeAccent());
        dto.setPremiumPostCardBackground(acc.getPremiumPostCardBackground());
        dto.setProfileSocialLinksJson(acc.getProfileSocialLinksJson());
        dto.setPremiumShareCardStyle(acc.getPremiumShareCardStyle());
        dto.setCreatedAt(acc.getCreatedAt());

        long postCount = acc.getPosts() != null ? acc.getPosts().stream()
                .filter(p -> p.getIsApproved() == 1 && p.getIsActive() == 1).count() : 0;
        dto.setPostCount(postCount);

        long followerCount = followDAO.countByFollowee_AccountIDAndStatus(acc.getAccountID(), 1);
        dto.setFollowerCount(followerCount);

        long followingCount = followDAO.countByFollower_AccountIDAndStatus(acc.getAccountID(), 1);
        dto.setFollowingCount(followingCount);

        long totalViews = acc.getPosts() != null ? acc.getPosts().stream()
            .mapToLong(p -> Integer.valueOf(0).equals(p.getViews()) ? 0L : (p.getViews() == null ? 0L : p.getViews().longValue())).sum() : 0;
        dto.setTotalViews(totalViews);

        dto.setLastLoginAt(acc.getLastLoginAt());
        dto.setLastLoginIp(acc.getLastLoginIp());
        dto.setMfaEnabled(acc.getMfaEnabled());

        return dto;
    }

    private String normalizeText(String value, int maxLength, String errorMessage) {
        String normalized = value == null ? null : value.trim();
        if (normalized == null || normalized.isBlank() || normalized.length() > maxLength) {
            throw new IllegalArgumentException(errorMessage);
        }
        return normalized;
    }

    private String normalizeNullableText(String value, int maxLength, String errorMessage) {
        if (value == null) {
            return null;
        }
        String normalized = value.trim();
        if (normalized.isEmpty()) {
            return null;
        }
        if (normalized.length() > maxLength) {
            throw new IllegalArgumentException(errorMessage);
        }
        return normalized;
    }

    private String normalizeUrl(String value, int maxLength, String errorMessage) {
        if (value == null) {
            return null;
        }
        String normalized = value.trim();
        if (normalized.isEmpty()) {
            return null;
        }
        if (normalized.length() > maxLength) {
            throw new IllegalArgumentException(errorMessage);
        }
        if (!(normalized.startsWith("http://") || normalized.startsWith("https://") || normalized.startsWith("/api/media/"))) {
            throw new IllegalArgumentException(errorMessage);
        }
        return normalized;
    }

    private String normalizeHexColor(String value, String errorMessage) {
        if (value == null) {
            return null;
        }
        String normalized = value.trim();
        if (normalized.isEmpty()) {
            return null;
        }
        if (!normalized.matches("^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{8})$")) {
            throw new IllegalArgumentException(errorMessage);
        }
        return normalized.toUpperCase();
    }

    private String normalizeAvatarFrame(String value) {
        if (value == null || value.isBlank()) {
            return "gold";
        }
        String normalized = value.trim().toLowerCase();
        if (!PREMIUM_AVATAR_FRAMES.contains(normalized)) {
            throw new IllegalArgumentException("Avatar frame must be one of: gold, aurora, neon, cosmic.");
        }
        return normalized;
    }

    private String normalizeShareCardStyle(String value) {
        if (value == null || value.isBlank()) {
            return "spotlight";
        }
        String normalized = value.trim().toLowerCase();
        if (!PREMIUM_SHARE_CARD_STYLES.contains(normalized)) {
            throw new IllegalArgumentException("Share card style must be one of: spotlight, stacked, minimal, creator.");
        }
        return normalized;
    }

    private String normalizeSocialLinksJson(String value) throws Exception {
        if (value == null) {
            return null;
        }
        String normalized = value.trim();
        if (normalized.isEmpty()) {
            return null;
        }

        JsonNode root = objectMapper.readTree(normalized);
        if (!root.isArray()) {
            throw new IllegalArgumentException("Social links payload must be a JSON array.");
        }

        ArrayNode sanitized = objectMapper.createArrayNode();
        for (JsonNode item : root) {
            if (!item.isObject()) {
                continue;
            }
            String platform = item.path("platform").asText("").trim().toLowerCase();
            String url = normalizeUrl(item.path("url").asText(null), 1000, "Each social link must use a valid HTTP/HTTPS URL.");
            if (platform.isEmpty() || url == null) {
                continue;
            }
            ObjectNode entry = objectMapper.createObjectNode();
            entry.put("platform", platform);
            entry.put("url", url);
            sanitized.add(entry);
        }

        return sanitized.isEmpty() ? null : objectMapper.writeValueAsString(sanitized);
    }
}
