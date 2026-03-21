package poly.edu.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import poly.edu.dao.AccountDAO;
import poly.edu.dao.FollowDAO;
import poly.edu.dao.PostDAO;
import poly.edu.dto.LeaderboardPostDTO;
import poly.edu.dto.LeaderboardUserDTO;
import poly.edu.entity.Account;
import poly.edu.entity.Comment; // 🔥 Đã đổi sang import Comment
import poly.edu.entity.Post;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/leaderboard")
@RequiredArgsConstructor
public class LeaderboardController {

    private final PostDAO postDAO;
    private final AccountDAO accountDAO;
    private final FollowDAO followDAO;

    @GetMapping("/posts")
    public ResponseEntity<List<LeaderboardPostDTO>> getTopPosts(@RequestParam(defaultValue = "10") int limit) {
        List<Post> posts = postDAO.findByIsApprovedAndIsActive(1, 1);

        AtomicInteger rank = new AtomicInteger(1);
        List<LeaderboardPostDTO> result = posts.stream()
                .map(p -> {
                    LeaderboardPostDTO dto = new LeaderboardPostDTO();
                    dto.setPostID(p.getPostID());
                    dto.setTitle(p.getTitle());
                    dto.setMedia(p.getMedia());
                    dto.setViews(p.getViews());

                    // 🔥 FIX LOGIC RATING: Lấy từ Comment thay vì Rating Entity cũ
                    List<Comment> comments = p.getComments();
                    if (comments != null && !comments.isEmpty()) {
                        // Lọc ra những comment có đánh giá sao
                        List<Comment> ratingComments = comments.stream()
                                .filter(c -> c.getRating() != null && c.getRating() > 0)
                                .collect(Collectors.toList());

                        if (!ratingComments.isEmpty()) {
                            dto.setAvgRating(ratingComments.stream().mapToInt(Comment::getRating).average().orElse(0));
                            dto.setRatingCount((long) ratingComments.size());
                        } else {
                            dto.setAvgRating(0.0);
                            dto.setRatingCount(0L);
                        }
                    } else {
                        dto.setAvgRating(0.0);
                        dto.setRatingCount(0L);
                    }

                    dto.setFavoriteCount(p.getFavorites() != null ? (long) p.getFavorites().size() : 0L);

                    // Score formula: views * 0.3 + avgRating * 1500 + favorites * 50 + ratingCount * 100
                    double score = (p.getViews() != null ? p.getViews() * 0.3 : 0)
                            + (dto.getAvgRating() != null ? dto.getAvgRating() * 1500 : 0)
                            + (dto.getFavoriteCount() != null ? dto.getFavoriteCount() * 50 : 0)
                            + (dto.getRatingCount() != null ? dto.getRatingCount() * 100 : 0);
                    dto.setScore(Math.round(score * 10.0) / 10.0);

                    if (p.getAccount() != null) {
                        dto.setAuthorID(p.getAccount().getAccountID());
                        dto.setAuthorName(p.getAccount().getUsername());
                        dto.setAuthorAvatar(p.getAccount().getAvatar());
                    }
                    if (p.getCategory() != null) {
                        dto.setCategoryName(p.getCategory().getCategoryName());
                    }
                    return dto;
                })
                .sorted(Comparator.comparingDouble((LeaderboardPostDTO d) -> d.getScore() != null ? d.getScore() : 0).reversed())
                .limit(limit)
                .collect(Collectors.toList());

        // Assign rank
        result.forEach(d -> d.setRank(rank.getAndIncrement()));
        return ResponseEntity.ok(result);
    }

    @GetMapping("/users")
    public ResponseEntity<List<LeaderboardUserDTO>> getTopUsers(@RequestParam(defaultValue = "10") int limit) {
        List<Account> accounts = accountDAO.findAll().stream()
                .filter(a -> a.getIsActive() == 1)
                .collect(Collectors.toList());

        AtomicInteger rank = new AtomicInteger(1);
        List<LeaderboardUserDTO> result = accounts.stream()
                .map(a -> {
                    LeaderboardUserDTO dto = new LeaderboardUserDTO();
                    dto.setAccountID(a.getAccountID());
                    dto.setUsername(a.getUsername());
                    dto.setAvatar(a.getAvatar());
                    dto.setPoint(a.getPoint() != null ? a.getPoint() : 0);

                    long postCount = a.getPosts() != null ? a.getPosts().stream()
                            .filter(p -> p.getIsApproved() == 1 && p.getIsActive() == 1).count() : 0;
                    dto.setPostCount(postCount);

                    long followerCount = followDAO.countByFollowee_AccountIDAndStatus(a.getAccountID(), 1);
                    dto.setFollowerCount(followerCount);

                    return dto;
                })
                .sorted(Comparator.comparingLong((LeaderboardUserDTO d) -> {
                    long score = (d.getPoint() != null ? d.getPoint() : 0)
                            + (d.getFollowerCount() != null ? d.getFollowerCount() * 10 : 0)
                            + (d.getPostCount() != null ? d.getPostCount() * 50 : 0);
                    return score;
                }).reversed())
                .limit(limit)
                .collect(Collectors.toList());

        result.forEach(d -> d.setRank(rank.getAndIncrement()));
        return ResponseEntity.ok(result);
    }
}