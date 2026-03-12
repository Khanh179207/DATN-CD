package poly.edu.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.HtmlUtils;
import poly.edu.dao.AccountDAO;
import poly.edu.dao.FollowDAO;
import poly.edu.dao.PostDAO;
import poly.edu.entity.Account;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Locale;

@RestController
@RequiredArgsConstructor
public class ProfileShareController {

    private static final String DEFAULT_PRIMARY = "#F59E0B";
    private static final String DEFAULT_SECONDARY = "#FB7185";
    private static final String DEFAULT_ACCENT = "#0F172A";
    private static final String DEFAULT_CARD = "#FFF7ED";

    private final AccountDAO accountDAO;
    private final PostDAO postDAO;
    private final FollowDAO followDAO;
    private final ObjectMapper objectMapper;

    @Value("${app.base-url:http://localhost:8080}")
    private String appBaseUrl;

    @Value("${app.frontend.url:http://localhost:5173}")
    private String frontendUrl;

    @GetMapping(value = "/share/profile/{id}", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> getProfileSharePage(@PathVariable Integer id, HttpServletRequest request) {
        Account account = loadActiveAccount(id);
        if (account == null) {
            return ResponseEntity.notFound().build();
        }

        ShareProfileData profile = buildShareProfile(account, request);
        String html = buildShareLandingHtml(profile);

        return ResponseEntity.ok()
                .contentType(new MediaType(MediaType.TEXT_HTML, StandardCharsets.UTF_8))
                .cacheControl(CacheControl.maxAge(Duration.ofMinutes(5)).cachePublic())
                .body(html);
    }

    @GetMapping(value = "/api/share/profile/{id}/card.svg", produces = "image/svg+xml")
    public ResponseEntity<String> getProfileShareCard(@PathVariable Integer id, HttpServletRequest request) {
        Account account = loadActiveAccount(id);
        if (account == null) {
            return ResponseEntity.notFound().build();
        }

        ShareProfileData profile = buildShareProfile(account, request);
        String svg = buildShareCardSvg(profile);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, "image/svg+xml; charset=UTF-8")
                .cacheControl(CacheControl.maxAge(Duration.ofMinutes(5)).cachePublic())
                .body(svg);
    }

    private Account loadActiveAccount(Integer id) {
        return accountDAO.findById(id)
                .filter(Account::isAccountActive)
                .orElse(null);
    }

    private ShareProfileData buildShareProfile(Account account, HttpServletRequest request) {
        long postCount = postDAO.countByAccountId(account.getAccountID());
        long followerCount = followDAO.countByFollowee_AccountIDAndStatus(account.getAccountID(), 1);
        long followingCount = followDAO.countByFollower_AccountIDAndStatus(account.getAccountID(), 1);
        long socialCount = countSocialLinks(account.getProfileSocialLinksJson());

        String username = fallback(account.getUsername(), "GoMet Creator");
        String bio = fallback(account.getBio(), "Premium creator on GoMet, sharing recipes, kitchen experiments, and signature profile style.");
        String description = truncate(buildDescription(bio, postCount, followerCount, followingCount, socialCount), 180);
        String backendBase = trimTrailingSlash(resolveBaseUrl(request));
        String frontendBase = trimTrailingSlash(frontendUrl);
        String shareUrl = backendBase + "/share/profile/" + account.getAccountID();
        String profileUrl = frontendBase + "/profile/" + account.getAccountID();
        String imageUrl = backendBase + "/api/share/profile/" + account.getAccountID() + "/card.svg";
        String title = username + (Integer.valueOf(1).equals(account.getIsPremium()) ? " | GoMet Premium" : " | GoMet");

        return new ShareProfileData(
                account.getAccountID(),
                username,
                title,
                description,
                normalizeHex(account.getPremiumThemePrimary(), DEFAULT_PRIMARY),
                normalizeHex(account.getPremiumThemeSecondary(), DEFAULT_SECONDARY),
                normalizeHex(account.getPremiumThemeAccent(), DEFAULT_ACCENT),
                normalizeHex(account.getPremiumPostCardBackground(), DEFAULT_CARD),
                normalizeFrame(account.getPremiumAvatarFrame()),
                normalizeShareStyle(account.getPremiumShareCardStyle()),
                absoluteMediaUrl(account.getAvatar(), backendBase),
                absoluteMediaUrl(account.getProfileBannerUrl(), backendBase),
                shareUrl,
                profileUrl,
                imageUrl,
                Integer.valueOf(1).equals(account.getIsPremium()),
                postCount,
                followerCount,
                followingCount,
                socialCount
        );
    }

    private String buildDescription(String bio, long postCount, long followerCount, long followingCount, long socialCount) {
        String metrics = String.format(Locale.ROOT,
                "%d recipes, %d followers, %d following%s",
                postCount,
                followerCount,
                followingCount,
                socialCount > 0 ? ", " + socialCount + " public social links" : "");
        return bio + " • " + metrics;
    }

    private long countSocialLinks(String rawJson) {
        if (rawJson == null || rawJson.isBlank()) {
            return 0;
        }

        try {
            JsonNode root = objectMapper.readTree(rawJson);
            if (!root.isArray()) {
                return 0;
            }

            long count = 0;
            for (JsonNode node : root) {
                String url = node.path("url").asText("").trim();
                if (!url.isEmpty()) {
                    count++;
                }
            }
            return count;
        } catch (Exception ignored) {
            return 0;
        }
    }

    private String buildShareLandingHtml(ShareProfileData profile) {
        String title = escapeHtml(profile.title());
        String description = escapeHtml(profile.description());
        String profileUrl = escapeHtml(profile.profileUrl());
        String shareUrl = escapeHtml(profile.shareUrl());
        String imageUrl = escapeHtml(profile.imageUrl());

        return """
                <!doctype html>
                <html lang="en">
                <head>
                  <meta charset="utf-8">
                  <meta name="viewport" content="width=device-width, initial-scale=1">
                  <title>%s</title>
                  <meta name="description" content="%s">
                  <meta property="og:type" content="profile">
                  <meta property="og:title" content="%s">
                  <meta property="og:description" content="%s">
                  <meta property="og:url" content="%s">
                  <meta property="og:image" content="%s">
                  <meta property="og:image:width" content="1200">
                  <meta property="og:image:height" content="630">
                  <meta property="og:image:alt" content="%s">
                  <meta name="twitter:card" content="summary_large_image">
                  <meta name="twitter:title" content="%s">
                  <meta name="twitter:description" content="%s">
                  <meta name="twitter:image" content="%s">
                  <meta name="theme-color" content="%s">
                  <link rel="canonical" href="%s">
                  <meta http-equiv="refresh" content="0;url=%s">
                  <style>
                    :root { color-scheme: light; }
                    * { box-sizing: border-box; }
                    body {
                      margin: 0;
                      min-height: 100vh;
                      display: grid;
                      place-items: center;
                      font-family: "Segoe UI", system-ui, sans-serif;
                      background:
                        radial-gradient(circle at top, rgba(245, 158, 11, 0.20), transparent 40%%),
                        linear-gradient(135deg, #0f172a, #1e293b 55%%, #111827);
                      color: #f8fafc;
                    }
                    .card {
                      width: min(92vw, 560px);
                      padding: 28px;
                      border-radius: 28px;
                      background: rgba(15, 23, 42, 0.82);
                      border: 1px solid rgba(255, 255, 255, 0.14);
                      box-shadow: 0 30px 80px rgba(15, 23, 42, 0.45);
                      backdrop-filter: blur(18px);
                    }
                    .eyebrow {
                      display: inline-flex;
                      padding: 8px 12px;
                      border-radius: 999px;
                      background: rgba(255, 255, 255, 0.10);
                      font-size: 12px;
                      letter-spacing: 0.12em;
                      text-transform: uppercase;
                    }
                    h1 { margin: 16px 0 10px; font-size: clamp(28px, 6vw, 44px); }
                    p { margin: 0 0 18px; line-height: 1.6; color: rgba(248, 250, 252, 0.82); }
                    a {
                      color: #fff;
                      text-decoration: none;
                      display: inline-flex;
                      align-items: center;
                      gap: 10px;
                      padding: 12px 18px;
                      border-radius: 999px;
                      background: linear-gradient(135deg, %s, %s);
                      font-weight: 700;
                    }
                  </style>
                </head>
                <body>
                  <main class="card">
                    <span class="eyebrow">GoMet Share</span>
                    <h1>%s</h1>
                    <p>%s</p>
                    <a href="%s">Open profile</a>
                  </main>
                  <script>window.location.replace(%s);</script>
                </body>
                </html>
                """.formatted(
                title,
                description,
                title,
                description,
                shareUrl,
                imageUrl,
                title,
                title,
                description,
                imageUrl,
                profile.primaryColor(),
                profileUrl,
                profileUrl,
                profile.primaryColor(),
                profile.secondaryColor(),
                escapeHtml(profile.username()),
                description,
                profileUrl,
                quoteForScript(profile.profileUrl())
        );
    }

    private String buildShareCardSvg(ShareProfileData profile) {
        String overlayImage = profile.bannerUrl() == null ? "" : """
                <image href="%s" x="0" y="0" width="1200" height="630" preserveAspectRatio="xMidYMid slice" opacity="0.44" />
                <rect width="1200" height="630" fill="url(#bannerFade)" />
                """.formatted(escapeXml(profile.bannerUrl()));

        String avatarImage = profile.avatarUrl() == null ? buildAvatarFallback(profile.username(), profile.primaryColor()) : """
                <image href="%s" x="64" y="188" width="168" height="168" preserveAspectRatio="xMidYMid slice" clip-path="url(#avatarClip)" />
                """.formatted(escapeXml(profile.avatarUrl()));

        String premiumTag = profile.premium() ? "<text x=\"270\" y=\"206\" fill=\"#FFFFFF\" font-size=\"20\" font-weight=\"700\">GO MET PREMIUM</text>" : "<text x=\"270\" y=\"206\" fill=\"#E2E8F0\" font-size=\"20\" font-weight=\"700\">GO MET PROFILE</text>";
        String accentLabel = switch (profile.shareCardStyle()) {
            case "creator" -> "Creator Showcase";
            case "stacked" -> "Story Card";
            case "minimal" -> "Clean Public Profile";
            default -> "Premium Profile Spotlight";
        };

        return """
                <svg xmlns="http://www.w3.org/2000/svg" width="1200" height="630" viewBox="0 0 1200 630" fill="none">
                  <defs>
                    <linearGradient id="bg" x1="84" y1="58" x2="1060" y2="600" gradientUnits="userSpaceOnUse">
                      <stop stop-color="%s" />
                      <stop offset="1" stop-color="%s" />
                    </linearGradient>
                    <linearGradient id="cardStroke" x1="120" y1="80" x2="1000" y2="560" gradientUnits="userSpaceOnUse">
                      <stop stop-color="rgba(255,255,255,0.55)" />
                      <stop offset="1" stop-color="rgba(255,255,255,0.08)" />
                    </linearGradient>
                    <linearGradient id="bannerFade" x1="0" y1="0" x2="1200" y2="630" gradientUnits="userSpaceOnUse">
                      <stop stop-color="rgba(15,23,42,0.15)" />
                      <stop offset="1" stop-color="rgba(15,23,42,0.65)" />
                    </linearGradient>
                    <linearGradient id="ring" x1="64" y1="188" x2="232" y2="356" gradientUnits="userSpaceOnUse">
                      <stop stop-color="#FFFFFF" />
                      <stop offset="1" stop-color="%s" />
                    </linearGradient>
                    <clipPath id="avatarClip">
                      <circle cx="148" cy="272" r="84" />
                    </clipPath>
                    <filter id="shadow" x="0" y="0" width="1200" height="630" filterUnits="userSpaceOnUse">
                      <feDropShadow dx="0" dy="28" stdDeviation="32" flood-color="#020617" flood-opacity="0.35" />
                    </filter>
                  </defs>

                  <rect width="1200" height="630" fill="#020617" />
                  <rect width="1200" height="630" fill="url(#bg)" />
                  %s
                  <circle cx="1030" cy="118" r="180" fill="%s" opacity="0.18" />
                  <circle cx="160" cy="540" r="160" fill="%s" opacity="0.20" />

                  <g filter="url(#shadow)">
                    <rect x="42" y="42" width="1116" height="546" rx="42" fill="rgba(15,23,42,0.76)" />
                    <rect x="42.75" y="42.75" width="1114.5" height="544.5" rx="41.25" stroke="rgba(255,255,255,0.14)" stroke-width="1.5" />
                  </g>

                  <text x="72" y="112" fill="#F8FAFC" font-size="24" font-weight="700" letter-spacing="5">GOMET PROFILE SHARE</text>
                  <text x="72" y="150" fill="rgba(248,250,252,0.72)" font-size="22">%s</text>

                  <circle cx="148" cy="272" r="98" fill="rgba(255,255,255,0.12)" />
                  <circle cx="148" cy="272" r="90" fill="url(#ring)" opacity="0.94" />
                  <circle cx="148" cy="272" r="84" fill="#FFFFFF" fill-opacity="0.14" />
                  %s

                  %s
                  <text x="270" y="258" fill="#FFFFFF" font-size="56" font-weight="800">%s</text>
                  <text x="270" y="300" fill="rgba(248,250,252,0.74)" font-size="24">%s</text>
                  <foreignObject x="270" y="326" width="640" height="120">
                    <div xmlns="http://www.w3.org/1999/xhtml" style="font-family: 'Segoe UI', sans-serif; color: rgba(248,250,252,0.82); font-size: 28px; line-height: 1.45;">
                      %s
                    </div>
                  </foreignObject>

                  <rect x="72" y="458" width="208" height="92" rx="28" fill="rgba(255,255,255,0.12)" />
                  <text x="100" y="496" fill="rgba(248,250,252,0.72)" font-size="20">Recipes</text>
                  <text x="100" y="534" fill="#FFFFFF" font-size="34" font-weight="800">%d</text>

                  <rect x="304" y="458" width="208" height="92" rx="28" fill="rgba(255,255,255,0.12)" />
                  <text x="332" y="496" fill="rgba(248,250,252,0.72)" font-size="20">Followers</text>
                  <text x="332" y="534" fill="#FFFFFF" font-size="34" font-weight="800">%d</text>

                  <rect x="536" y="458" width="208" height="92" rx="28" fill="rgba(255,255,255,0.12)" />
                  <text x="564" y="496" fill="rgba(248,250,252,0.72)" font-size="20">Social links</text>
                  <text x="564" y="534" fill="#FFFFFF" font-size="34" font-weight="800">%d</text>

                  <rect x="826" y="430" width="280" height="120" rx="32" fill="%s" fill-opacity="0.88" />
                  <text x="858" y="476" fill="%s" font-size="18" font-weight="700" letter-spacing="3">STYLE</text>
                  <text x="858" y="516" fill="%s" font-size="36" font-weight="800">%s</text>
                </svg>
                """.formatted(
                profile.primaryColor(),
                profile.secondaryColor(),
                profile.secondaryColor(),
                overlayImage,
                profile.secondaryColor(),
                profile.primaryColor(),
                escapeXml(accentLabel),
                avatarImage,
                premiumTag,
                escapeXml(truncate(profile.username(), 26)),
                escapeXml("@" + profile.accountId()),
                escapeXml(truncate(profile.description(), 130)),
                profile.postCount(),
                profile.followerCount(),
                profile.socialCount(),
                profile.cardBackground(),
                profile.accentColor(),
                profile.accentColor(),
                escapeXml(profile.shareCardStyle().toUpperCase(Locale.ROOT))
        );
    }

    private String buildAvatarFallback(String username, String primaryColor) {
        String initial = escapeXml(username.substring(0, 1).toUpperCase(Locale.ROOT));
        return """
                <circle cx="148" cy="272" r="84" fill="%s" />
                <text x="148" y="294" text-anchor="middle" fill="#FFFFFF" font-size="68" font-weight="800">%s</text>
                """.formatted(primaryColor, initial);
    }

    private String absoluteMediaUrl(String value, String baseUrl) {
        if (value == null || value.isBlank()) {
            return null;
        }
        if (value.startsWith("http://") || value.startsWith("https://")) {
            return value;
        }
        if (value.startsWith("/")) {
            return baseUrl + value;
        }
        return baseUrl + "/" + value;
    }

    private String resolveBaseUrl(HttpServletRequest request) {
        try {
            return ServletUriComponentsBuilder.fromRequestUri(request)
                    .replacePath(null)
                    .replaceQuery(null)
                    .build()
                    .toUriString();
        } catch (Exception ignored) {
            return appBaseUrl;
        }
    }

    private String normalizeHex(String value, String fallback) {
        if (value == null) {
            return fallback;
        }
        String normalized = value.trim().toUpperCase(Locale.ROOT);
        return normalized.matches("^#([A-F0-9]{6}|[A-F0-9]{8})$") ? normalized : fallback;
    }

    private String normalizeFrame(String value) {
        if (value == null || value.isBlank()) {
            return "gold";
        }
        return value.trim().toLowerCase(Locale.ROOT);
    }

    private String normalizeShareStyle(String value) {
        if (value == null || value.isBlank()) {
            return "spotlight";
        }
        return value.trim().toLowerCase(Locale.ROOT);
    }

    private String fallback(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value.trim();
    }

    private String trimTrailingSlash(String value) {
        if (value == null || value.isBlank()) {
            return "";
        }
        return value.endsWith("/") ? value.substring(0, value.length() - 1) : value;
    }

    private String truncate(String value, int maxLength) {
        if (value == null || value.length() <= maxLength) {
            return value;
        }
        return value.substring(0, Math.max(0, maxLength - 3)).trim() + "...";
    }

    private String escapeHtml(String value) {
        return HtmlUtils.htmlEscape(value == null ? "" : value, StandardCharsets.UTF_8.name());
    }

    private String escapeXml(String value) {
        if (value == null) {
            return "";
        }
        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }

    private String quoteForScript(String value) {
        return "\"" + value.replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
    }

    private record ShareProfileData(
            Integer accountId,
            String username,
            String title,
            String description,
            String primaryColor,
            String secondaryColor,
            String accentColor,
            String cardBackground,
            String avatarFrame,
            String shareCardStyle,
            String avatarUrl,
            String bannerUrl,
            String shareUrl,
            String profileUrl,
            String imageUrl,
            boolean premium,
            long postCount,
            long followerCount,
            long followingCount,
            long socialCount
    ) {
    }
}