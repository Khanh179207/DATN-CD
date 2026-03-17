package poly.edu.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import poly.edu.dao.AccountDAO;
import poly.edu.dao.PostDAO;
import poly.edu.dao.RatingDAO;
import poly.edu.dto.RatingDTO;
import poly.edu.dto.RatingRequestDTO;
import poly.edu.entity.Account;
import poly.edu.entity.Post;
import poly.edu.entity.Rating;
import poly.edu.service.NotificationService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/ratings")
@RequiredArgsConstructor
@CrossOrigin("*")
public class RatingController {

    private final RatingDAO ratingDAO;
    private final PostDAO postDAO;
    private final AccountDAO accountDAO;
    private final NotificationService notificationService;

    @PostMapping
    public ResponseEntity<?> rate(@RequestBody RatingRequestDTO req) {
        if (req.getAccountID() == null || req.getPostID() == null || req.getRate() == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Thiếu thông tin đánh giá"));
        }
        if (req.getRate() < 1 || req.getRate() > 5) {
            return ResponseEntity.badRequest().body(Map.of("message", "Điểm đánh giá phải từ 1 đến 5"));
        }

        Post post = postDAO.findById(req.getPostID()).orElse(null);
        Account account = accountDAO.findById(req.getAccountID()).orElse(null);
        if (post == null || account == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Không tìm thấy bài viết hoặc tài khoản"));
        }

        Optional<Rating> existing = ratingDAO.findByAccount_AccountIDAndPost_PostID(req.getAccountID(),
                req.getPostID());
        Rating rating;
        boolean isNewRating = false;
        if (existing.isPresent()) {
            rating = existing.get();
            rating.setRate(req.getRate());
            rating.setComment(req.getComment());
            rating.setAttachments(req.getImageUrls());
        } else {
            rating = Rating.builder()
                    .account(account)
                    .post(post)
                    .rate(req.getRate())
                    .comment(req.getComment())
                    .attachments(req.getImageUrls())
                    .build();
            isNewRating = true;
        }

        ratingDAO.save(rating);

        // Create notification for the post owner if this is a new rating and rater is not the owner
        if (isNewRating) {
            Integer postOwnerId = post.getAccount().getAccountID();
            if (!req.getAccountID().equals(postOwnerId)) {
                notificationService.notifyRating(account.getUsername(), postOwnerId, req.getPostID());
            }
        }

        return ResponseEntity.ok(Map.of("message", "Đánh giá thành công", "rate", rating.getRate()));
    }

    @GetMapping("/check")
    public ResponseEntity<?> checkRating(
            @RequestParam Integer accountID,
            @RequestParam Integer postID) {
        Optional<Rating> rating = ratingDAO.findByAccount_AccountIDAndPost_PostID(accountID, postID);
        if (rating.isPresent()) {
            return ResponseEntity.ok(Map.of("rated", true, "rate", rating.get().getRate()));
        }
        return ResponseEntity.ok(Map.of("rated", false));
    }

    @GetMapping("/post/{postId}")
    public ResponseEntity<?> getRatingsForPost(
            @PathVariable Integer postId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Integer star,
            @RequestParam(required = false) Boolean hasComment,
            @RequestParam(required = false) Boolean hasImage) {

        // Fetch all ratings for the post and filter in memory (acceptable for moderate
        // dataset)
        List<Rating> all = ratingDAO.findAllByPost_PostID(postId);
        List<Rating> filtered = all.stream()
                .filter(r -> star == null || (r.getRate() != null && r.getRate().equals(star)))
                .filter(r -> hasComment == null
                        || hasComment.equals(r.getComment() != null && !r.getComment().trim().isEmpty()))
                .filter(r -> hasImage == null
                        || hasImage.equals(r.getAttachments() != null && !r.getAttachments().isEmpty()))
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .collect(Collectors.toList());

        int start = Math.min(page * size, filtered.size());
        int end = Math.min(start + size, filtered.size());
        List<Rating> pageItems = filtered.subList(start, end);

        Map<String, Object> result = new HashMap<>();
        result.put("items", pageItems.stream().map(this::toDto).collect(Collectors.toList()));
        result.put("page", page);
        result.put("size", size);
        result.put("total", filtered.size());
        result.put("totalPages", (int) Math.ceil((double) filtered.size() / size));

        return ResponseEntity.ok(result);
    }

    @GetMapping("/post/{postId}/summary")
    public ResponseEntity<?> getRatingSummary(@PathVariable Integer postId) {
        Double avg = ratingDAO.findAvgRateByPost(postId);
        List<Object[]> counts = ratingDAO.countByRateGroupBy(postId);

        Map<Integer, Long> distribution = new HashMap<>();
        // Ensure 1..5 keys exist
        for (int i = 1; i <= 5; i++)
            distribution.put(i, 0L);
        for (Object[] row : counts) {
            if (row.length == 2 && row[0] instanceof Number && row[1] instanceof Number) {
                distribution.put(((Number) row[0]).intValue(), ((Number) row[1]).longValue());
            }
        }

        long total = distribution.values().stream().mapToLong(Long::longValue).sum();

        // Additional metadata for UI filters
        long withComments = allRatingsForPost(postId).stream()
                .filter(r -> r.getComment() != null && !r.getComment().trim().isEmpty())
                .count();
        long withImages = allRatingsForPost(postId).stream()
                .filter(r -> r.getAttachments() != null && !r.getAttachments().isEmpty())
                .count();

        Map<String, Object> result = Map.of(
                "average", avg != null ? Math.round(avg * 10.0) / 10.0 : 0.0,
                "total", total,
                "distribution", distribution,
                "withComments", withComments,
                "withImages", withImages);
        return ResponseEntity.ok(result);
    }

    private List<Rating> allRatingsForPost(Integer postId) {
        return ratingDAO.findAllByPost_PostID(postId);
    }

    private RatingDTO toDto(Rating r) {
        RatingDTO dto = new RatingDTO();
        dto.setRatingID(r.getRatingID());
        dto.setPostID(r.getPost() != null ? r.getPost().getPostID() : null);
        if (r.getAccount() != null) {
            dto.setAccountID(r.getAccount().getAccountID());
            dto.setAuthorName(r.getAccount().getUsername());
            dto.setAuthorAvatar(r.getAccount().getAvatar());
        }
        dto.setRate(r.getRate());
        dto.setComment(r.getComment());
        dto.setImageUrls(r.getAttachments());
        dto.setCreatedAt(r.getCreatedAt());
        return dto;
    }
}
