package poly.edu.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import poly.edu.dao.*;
import poly.edu.entity.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin/stats")
@RequiredArgsConstructor
@CrossOrigin
public class AdminStatsController {

    private final AccountDAO accountDAO;
    private final PostDAO postDAO;
    private final ReportDAO reportDAO;
    private final NotificationDAO notificationDAO;
    private final FollowDAO followDAO;
    private final FavoriteDAO favoriteDAO;
    private final EventDAO eventDAO;
    private final EventPostsDAO eventPostsDAO;
    private final UserAchievementDAO userAchievementDAO;
    private final CommentDAO commentDAO;
    private final RatingDAO ratingDAO;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getStats() {
        List<Account> allAccounts = accountDAO.findAll();
        List<Post> allPosts = postDAO.findAll();

        long totalUsers = allAccounts.size();
        long totalPosts = allPosts.size();
        long pendingPosts = allPosts.stream().filter(p -> p.getIsApproved() == 0 && p.getIsActive() == 1).count();
        long approvedPosts = allPosts.stream().filter(p -> p.getIsApproved() == 1 && p.getIsActive() == 1).count();
        long totalReports = reportDAO.count();
        long totalNotifications = notificationDAO.count();
        long bannedUsers = allAccounts.stream().filter(a -> a.getIsActive() == 0).count();
        long premiumUsers = allAccounts.stream().filter(a -> a.getIsPremium() == 1).count();
        long adminUsers = allAccounts.stream().filter(a -> a.getIsAdmin() == 1).count();

        LocalDate today = LocalDate.now();
        long newUsersToday = allAccounts.stream()
                .filter(a -> a.getCreatedAt() != null && a.getCreatedAt().equals(today))
                .count();
        long newPostsToday = allPosts.stream()
                .filter(p -> p.getCreatedAt() != null && p.getCreatedAt().equals(today))
                .count();

        long totalViews = allPosts.stream()
                .mapToLong(p -> p.getViews() != null ? p.getViews() : 0)
                .sum();

        long estimatedRevenue = premiumUsers * 99000L;

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", totalUsers);
        stats.put("totalPosts", totalPosts);
        stats.put("pendingPosts", pendingPosts);
        stats.put("approvedPosts", approvedPosts);
        stats.put("totalReports", totalReports);
        stats.put("totalNotifications", totalNotifications);
        stats.put("bannedUsers", bannedUsers);
        stats.put("premiumUsers", premiumUsers);
        stats.put("adminUsers", adminUsers);
        stats.put("newUsersToday", newUsersToday);
        stats.put("newPostsToday", newPostsToday);
        stats.put("totalViews", totalViews);
        stats.put("estimatedRevenue", estimatedRevenue);
        stats.put("activeUsers", totalUsers - bannedUsers);

        return ResponseEntity.ok(stats);
    }

    @GetMapping("/posts-by-month")
    public ResponseEntity<List<Map<String, Object>>> getPostsByMonth() {
        List<Post> allPosts = postDAO.findAll();
        Map<String, Long> byMonth = new java.util.LinkedHashMap<>();

        for (int i = 11; i >= 0; i--) {
            LocalDate month = LocalDate.now().minusMonths(i);
            String key = month.getMonthValue() + "/" + month.getYear();
            byMonth.put(key, 0L);
        }

        for (Post p : allPosts) {
            if (p.getCreatedAt() != null) {
                String key = p.getCreatedAt().getMonthValue() + "/" + p.getCreatedAt().getYear();
                byMonth.merge(key, 1L, Long::sum);
            }
        }

        List<Map<String, Object>> result = byMonth.entrySet().stream()
                .map(e -> {
                    Map<String, Object> m = new HashMap<>();
                    m.put("month", e.getKey());
                    m.put("posts", e.getValue());
                    return m;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }

    @GetMapping("/users-by-month")
    public ResponseEntity<List<Map<String, Object>>> getUsersByMonth() {
        List<Account> allAccounts = accountDAO.findAll();
        Map<String, Long> byMonth = new java.util.LinkedHashMap<>();

        for (int i = 11; i >= 0; i--) {
            LocalDate month = LocalDate.now().minusMonths(i);
            String key = month.getMonthValue() + "/" + month.getYear();
            byMonth.put(key, 0L);
        }

        for (Account a : allAccounts) {
            if (a.getCreatedAt() != null) {
                String key = a.getCreatedAt().getMonthValue() + "/" + a.getCreatedAt().getYear();
                byMonth.merge(key, 1L, Long::sum);
            }
        }

        List<Map<String, Object>> result = byMonth.entrySet().stream()
                .map(e -> {
                    Map<String, Object> m = new HashMap<>();
                    m.put("month", e.getKey());
                    m.put("users", e.getValue());
                    return m;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }

    // â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
    // USER DETAIL STATS
    // â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
    @GetMapping("/users-detail")
    public ResponseEntity<Map<String, Object>> getUsersDetail() {
        List<Account> allAccounts = accountDAO.findAll();
        List<Follow> allFollows = followDAO.findAll();
        List<Favorite> allFavorites = favoriteDAO.findAll();
        List<Event> allEvents = eventDAO.findAll();
        List<EventPosts> allEventPosts = eventPostsDAO.findAll();
        List<UserAchievement> allUserAchievements = userAchievementDAO.findAll();

        // follower count per user (status = 1 means active)
        Map<Integer, Long> followerMap = allFollows.stream()
                .filter(f -> f.getStatus() != null && f.getStatus() == 1)
                .collect(Collectors.groupingBy(f -> f.getFollowee().getAccountID(), Collectors.counting()));

        // total likes received across all posts
        Map<Integer, Long> likeMap = allFavorites.stream()
                .collect(Collectors.groupingBy(f -> f.getPost().getAccount().getAccountID(), Collectors.counting()));

        // event wins per user (Event.winner = accountID)
        Map<Integer, Long> winsMap = allEvents.stream()
                .filter(e -> e.getWinner() != null)
                .collect(Collectors.groupingBy(Event::getWinner, Collectors.counting()));

        // achievement count per user
        Map<Integer, Long> achieveMap = allUserAchievements.stream()
                .collect(Collectors.groupingBy(ua -> ua.getAccount().getAccountID(), Collectors.counting()));

        // event participations per user (number of event posts submitted)
        Map<Integer, Long> participMap = allEventPosts.stream()
                .collect(Collectors.groupingBy(ep -> ep.getPost().getAccount().getAccountID(), Collectors.counting()));

        // post count per user
        Map<Integer, Long> postMap = allAccounts.stream()
                .collect(Collectors.toMap(
                        Account::getAccountID,
                        a -> a.getPosts() != null ? (long) a.getPosts().size() : 0L
                ));

        Map<String, Object> result = new HashMap<>();
        result.put("topByFollowers",         buildUserRank(allAccounts, followerMap, 10));
        result.put("topByLikes",             buildUserRank(allAccounts, likeMap, 10));
        result.put("topByEventWins",         buildUserRank(allAccounts, winsMap, 10));
        result.put("topByAchievements",      buildUserRank(allAccounts, achieveMap, 10));
        result.put("topByEventParticipations", buildUserRank(allAccounts, participMap, 10));
        result.put("topByPostCount",         buildUserRank(allAccounts, postMap, 10));
        return ResponseEntity.ok(result);
    }

    private List<Map<String, Object>> buildUserRank(List<Account> accounts, Map<Integer, Long> valueMap, int limit) {
        return accounts.stream()
                .sorted((a, b) -> Long.compare(
                        valueMap.getOrDefault(b.getAccountID(), 0L),
                        valueMap.getOrDefault(a.getAccountID(), 0L)))
                .limit(limit)
                .map(a -> {
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("accountID", a.getAccountID());
                    m.put("username",  a.getUsername());
                    m.put("avatar",    a.getAvatar());
                    m.put("value",     valueMap.getOrDefault(a.getAccountID(), 0L));
                    return m;
                })
                .collect(Collectors.toList());
    }

    // â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
    // POST DETAIL STATS
    // â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
    @GetMapping("/posts-detail")
    public ResponseEntity<Map<String, Object>> getPostsDetail() {
        List<Post> allPosts = postDAO.findAll().stream()
                .filter(p -> p.getIsApproved() == 1 && p.getIsActive() == 1)
                .collect(Collectors.toList());
        List<Comment>  allComments  = commentDAO.findAll();
        List<Favorite> allFavorites = favoriteDAO.findAll();
        List<Rating>   allRatings   = ratingDAO.findAll();

        // comment count per post
        Map<Integer, Long> commentCountMap = allComments.stream()
                .collect(Collectors.groupingBy(c -> c.getPost().getPostID(), Collectors.counting()));

        // like (favorite) count per post
        Map<Integer, Long> likeCountMap = allFavorites.stream()
                .collect(Collectors.groupingBy(f -> f.getPost().getPostID(), Collectors.counting()));

        // average rating per post
        Map<Integer, Double> avgRatingMap = allRatings.stream()
                .collect(Collectors.groupingBy(r -> r.getPost().getPostID(),
                         Collectors.averagingInt(Rating::getRate)));

        // top commented
        List<Map<String, Object>> topByComments = allPosts.stream()
                .sorted((a, b) -> Long.compare(commentCountMap.getOrDefault(b.getPostID(), 0L),
                                               commentCountMap.getOrDefault(a.getPostID(), 0L)))
                .limit(10)
                .map(p -> buildPostEntry(p, commentCountMap.getOrDefault(p.getPostID(), 0L)))
                .collect(Collectors.toList());

        // top liked
        List<Map<String, Object>> topByLikes = allPosts.stream()
                .sorted((a, b) -> Long.compare(likeCountMap.getOrDefault(b.getPostID(), 0L),
                                               likeCountMap.getOrDefault(a.getPostID(), 0L)))
                .limit(10)
                .map(p -> buildPostEntry(p, likeCountMap.getOrDefault(p.getPostID(), 0L)))
                .collect(Collectors.toList());

        // top by rating (only posts that have at least 1 rating)
        List<Map<String, Object>> topByRating = allPosts.stream()
                .filter(p -> avgRatingMap.containsKey(p.getPostID()))
                .sorted((a, b) -> Double.compare(avgRatingMap.getOrDefault(b.getPostID(), 0.0),
                                                 avgRatingMap.getOrDefault(a.getPostID(), 0.0)))
                .limit(10)
                .map(p -> {
                    Map<String, Object> m = buildPostEntry(p, 0L);
                    m.put("value", Math.round(avgRatingMap.getOrDefault(p.getPostID(), 0.0) * 10.0) / 10.0);
                    return m;
                })
                .collect(Collectors.toList());

        // posts grouped by level
        Map<Integer, Long> byLevelMap = allPosts.stream()
                .filter(p -> p.getLevel() != null)
                .collect(Collectors.groupingBy(Post::getLevel, Collectors.counting()));

        List<Map<String, Object>> byLevel = byLevelMap.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(e -> {
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("level", e.getKey());
                    m.put("label", "Cáº¥p " + e.getKey());
                    m.put("count", e.getValue());
                    return m;
                })
                .collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("topByComments", topByComments);
        result.put("topByLikes",    topByLikes);
        result.put("topByRating",   topByRating);
        result.put("byLevel",       byLevel);
        return ResponseEntity.ok(result);
    }

    private Map<String, Object> buildPostEntry(Post p, long value) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("postID",   p.getPostID());
        m.put("title",    p.getTitle());
        m.put("author",   p.getAccount() != null ? p.getAccount().getUsername() : "");
        m.put("avatar",   p.getAccount() != null ? p.getAccount().getAvatar() : "");
        m.put("level",    p.getLevel());
        m.put("media",    p.getMedia());
        m.put("value",    value);
        return m;
    }

    // â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
    // EVENT DETAIL STATS
    // â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
    @GetMapping("/events-detail")
    public ResponseEntity<Map<String, Object>> getEventsDetail() {
        List<Event>      allEvents     = eventDAO.findAll();
        List<EventPosts> allEventPosts = eventPostsDAO.findAll();

        // participation count per event
        Map<Integer, Long> participMap = allEventPosts.stream()
                .collect(Collectors.groupingBy(ep -> ep.getEvent().getEventID(), Collectors.counting()));

        // distinct participant count per event (unique users who submitted a post)
        Map<Integer, Long> uniqueParticipantMap = allEventPosts.stream()
                .collect(Collectors.groupingBy(
                        ep -> ep.getEvent().getEventID(),
                        Collectors.collectingAndThen(
                                Collectors.mapping(ep -> ep.getPost().getAccount().getAccountID(),
                                                   Collectors.toSet()),
                                set -> (long) set.size())));

        List<Map<String, Object>> topByParticipations = allEvents.stream()
                .sorted((a, b) -> Long.compare(
                        participMap.getOrDefault(b.getEventID(), 0L),
                        participMap.getOrDefault(a.getEventID(), 0L)))
                .limit(10)
                .map(e -> {
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("eventID",      e.getEventID());
                    m.put("eventName",    e.getEventName());
                    m.put("startAt",      e.getStartAt() != null ? e.getStartAt().toString() : null);
                    m.put("endAt",        e.getEndAt() != null ? e.getEndAt().toString() : null);
                    m.put("winner",       e.getWinner());
                    m.put("totalPosts",   participMap.getOrDefault(e.getEventID(), 0L));
                    m.put("uniqueUsers",  uniqueParticipantMap.getOrDefault(e.getEventID(), 0L));
                    return m;
                })
                .collect(Collectors.toList());

        // overview event stats
        long totalEvents   = allEvents.size();
        LocalDateTime now      = LocalDateTime.now();
        long activeEvents  = allEvents.stream()
                .filter(e -> e.getStartAt() != null && e.getEndAt() != null
                          && !now.isBefore(e.getStartAt()) && !now.isAfter(e.getEndAt()))
                .count();
        long totalEntries  = allEventPosts.size();

        Map<String, Object> result = new HashMap<>();
        result.put("topByParticipations", topByParticipations);
        result.put("totalEvents",   totalEvents);
        result.put("activeEvents",  activeEvents);
        result.put("totalEntries",  totalEntries);
        return ResponseEntity.ok(result);
    }
}
