package poly.edu.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import poly.edu.dao.*;
import poly.edu.dto.*;
import poly.edu.entity.*;
import poly.edu.service.ModerationService;
import poly.edu.service.PostAntiSpamService;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostDAO postDAO;
    private final RatingDAO ratingDAO;
    private final FavoriteDAO favoriteDAO;
    private final CommentDAO commentDAO;
    private final FollowDAO followDAO;
    private final AccountDAO accountDAO;
    private final CategoryDAO categoryDAO;
    private final CookingStepsDAO cookingStepsDAO;
    private final ModerationService moderationService;
    private final PostAntiSpamService postAntiSpamService;

    // ─── List latest posts (home feed) ─────────────────────────────────
    @GetMapping("/latest")
    public ResponseEntity<List<PublicPostDTO>> getLatest(
            @RequestParam(defaultValue = "8") int limit) {
        List<Post> posts = postDAO.findLatest().stream()
                .limit(limit)
                .collect(Collectors.toList());
        return ResponseEntity.ok(posts.stream().map(this::toPublicDTO).collect(Collectors.toList()));
    }

    // ─── List all approved posts (paginated) ───────────────────────────
    @GetMapping
    public ResponseEntity<List<PublicPostDTO>> getAll(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "12") int size) {
        List<Post> all = postDAO.findByIsApprovedAndIsActive(1, 1);
        int from = Math.min((page - 1) * size, all.size());
        int to   = Math.min(from + size, all.size());
        List<Post> paged = all.subList(from, to);
        return ResponseEntity.ok(paged.stream().map(this::toPublicDTO).collect(Collectors.toList()));
    }

    // ─── Post detail ───────────────────────────────────────────────────
    @GetMapping("/{id}")
    public ResponseEntity<?> getDetail(@PathVariable Integer id) {
        return postDAO.findById(id).map(post -> {
            // Visibility: APPROVED is public. PENDING/REJECTED/HIDDEN only visible to owner.
            if (post.getStatus() != PostStatus.APPROVED) {
                Integer callerId = resolveCallerAccountId();
                Integer ownerId  = post.getAccount() != null ? post.getAccount().getAccountID() : null;
                if (callerId == null || !callerId.equals(ownerId)) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).<Object>build();
                }
            }
            // Increment view count only for APPROVED posts
            if (post.getStatus() == PostStatus.APPROVED) {
                post.setViews(post.getViews() + 1);
                postDAO.save(post);
            }
            PostDetailDTO dto = toDetailDTO(post);
            return ResponseEntity.ok(dto);
        }).orElse(ResponseEntity.notFound().build());
    }

    // ─── Search ────────────────────────────────────────────────────────
    @GetMapping("/search")
    public ResponseEntity<List<PublicPostDTO>> search(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(required = false) Integer categoryID,
            @RequestParam(defaultValue = "newest") String sort) {
        List<Post> posts;
        if (keyword.isBlank() && categoryID != null) {
            posts = postDAO.findByCategory_CategoryIDAndIsApprovedAndIsActive(categoryID, 1, 1);
        } else if (!keyword.isBlank() && categoryID != null) {
            posts = postDAO.searchByKeywordAndCategory(keyword, categoryID);
        } else if (!keyword.isBlank()) {
            posts = postDAO.searchByKeyword(keyword);
        } else {
            posts = postDAO.findByIsApprovedAndIsActive(1, 1);
        }

        List<PublicPostDTO> result = posts.stream().map(this::toPublicDTO).collect(Collectors.toList());
        if ("views".equals(sort)) {
            result.sort(Comparator.comparingInt((PublicPostDTO p) -> p.getViews() != null ? p.getViews() : 0).reversed());
        } else if ("rating".equals(sort)) {
            result.sort(Comparator.comparingDouble((PublicPostDTO p) -> p.getAvgRating() != null ? p.getAvgRating() : 0).reversed());
        } else {
            result.sort(Comparator.comparing((PublicPostDTO p) -> p.getCreatedAt() != null ? p.getCreatedAt().toString() : "").reversed());
        }
        return ResponseEntity.ok(result);
    }

    // ─── Related posts (same category) ────────────────────────────────
    @GetMapping("/{id}/related")
    public ResponseEntity<List<PublicPostDTO>> getRelated(
            @PathVariable Integer id,
            @RequestParam(defaultValue = "4") int limit) {
        return postDAO.findById(id).map(post -> {
            List<Post> related = postDAO.findRelated(post.getCategory().getCategoryID(), id);
            List<PublicPostDTO> result = related.stream()
                    .limit(limit)
                    .map(this::toPublicDTO)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(result);
        }).orElse(ResponseEntity.notFound().build());
    }

    // ─── Posts by user ─────────────────────────────────────────────────
    @GetMapping("/by-user/{accountID}")
    public ResponseEntity<List<PublicPostDTO>> getByUser(@PathVariable Integer accountID) {
        List<Post> posts = postDAO.findByAccount_AccountIDAndIsApprovedAndIsActive(accountID, 1, 1);
        return ResponseEntity.ok(posts.stream().map(this::toPublicDTO).collect(Collectors.toList()));
    }

    // ─── Suggest posts (for SuggestionsPage) ──────────────────────────
    @GetMapping("/suggest")
    public ResponseEntity<List<PublicPostDTO>> suggest(
            @RequestParam(required = false) Integer categoryID,
            @RequestParam(required = false) Integer difficulty,
            @RequestParam(required = false) Integer maxTime,
            @RequestParam(defaultValue = "0") Integer excludeId) {
        List<Post> all = postDAO.findByIsApprovedAndIsActive(1, 1);
        List<Post> filtered = all.stream()
                .filter(p -> categoryID == null || categoryID.equals(p.getCategory().getCategoryID()))
                .filter(p -> difficulty == null || difficulty.equals(p.getLevel()))
                .filter(p -> maxTime == null || p.getCookingTime() <= maxTime)
                .filter(p -> !p.getPostID().equals(excludeId))
                .collect(Collectors.toList());
        return ResponseEntity.ok(filtered.stream()
                .limit(20)
                .map(this::toPublicDTO)
                .collect(Collectors.toList()));
    }

    // ─── Trending (leaderboard-worthy) ────────────────────────────────
    @GetMapping("/trending")
    public ResponseEntity<List<PublicPostDTO>> getTrending(@RequestParam(defaultValue = "10") int limit) {
        List<Post> posts = postDAO.findByIsApprovedAndIsActive(1, 1);
        List<PublicPostDTO> result = posts.stream().map(this::toPublicDTO)
                .sorted(Comparator.comparingDouble((PublicPostDTO p) -> {
                    double views = p.getViews() != null ? p.getViews() / 1000.0 : 0;
                    double rating = p.getAvgRating() != null ? p.getAvgRating() : 0;
                    double fav = p.getFavoriteCount() != null ? p.getFavoriteCount() / 10.0 : 0;
                    return views + rating + fav;
                }).reversed())
                .limit(limit)
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    // ─── Mapping helpers ──────────────────────────────────────────────

    PublicPostDTO toPublicDTO(Post p) {
        PublicPostDTO dto = new PublicPostDTO();
        dto.setPostID(p.getPostID());
        dto.setTitle(p.getTitle());
        dto.setDescription(p.getDescription());
        dto.setMedia(p.getMedia());
        dto.setLevel(p.getLevel());
        dto.setCookingTime(p.getCookingTime());
        dto.setViews(p.getViews());
        dto.setCreatedAt(p.getCreatedAt());

        if (p.getAccount() != null) {
            dto.setAuthorID(p.getAccount().getAccountID());
            dto.setAuthorName(p.getAccount().getUsername());
            dto.setAuthorAvatar(p.getAccount().getAvatar());
        }
        if (p.getCategory() != null) {
            dto.setCategoryID(p.getCategory().getCategoryID());
            dto.setCategoryName(p.getCategory().getCategoryName());
        }

        // Aggregate: ratings
        List<Rating> ratings = p.getRatings();
        if (ratings != null && !ratings.isEmpty()) {
            dto.setAvgRating(ratings.stream()
                    .mapToInt(Rating::getRate)
                    .average()
                    .orElse(0));
            dto.setRatingCount((long) ratings.size());
        } else {
            dto.setAvgRating(0.0);
            dto.setRatingCount(0L);
        }

        long commentCount = p.getComments() != null ? p.getComments().size() : 0;
        dto.setCommentCount(commentCount);

        long favCount = p.getFavorites() != null ? p.getFavorites().size() : 0;
        dto.setFavoriteCount(favCount);

        return dto;
    }

    private PostDetailDTO toDetailDTO(Post p) {
        PostDetailDTO dto = new PostDetailDTO();
        dto.setPostID(p.getPostID());
        dto.setTitle(p.getTitle());
        dto.setDescription(p.getDescription());
        dto.setIngredients(p.getIngredients());
        dto.setMedia(p.getMedia());
        dto.setLevel(p.getLevel());
        dto.setCookingTime(p.getCookingTime());
        dto.setViews(p.getViews());
        dto.setCreatedAt(p.getCreatedAt());

        if (p.getAccount() != null) {
            Account author = p.getAccount();
            dto.setAuthorID(author.getAccountID());
            dto.setAuthorName(author.getUsername());
            dto.setAuthorAvatar(author.getAvatar());

            long postCount = author.getPosts() != null ? author.getPosts().stream()
                    .filter(ap -> ap.getIsApproved() == 1 && ap.getIsActive() == 1).count() : 0;
            dto.setAuthorPostCount(postCount);

            long followerCount = followDAO.countByFollowee_AccountIDAndStatus(author.getAccountID(), 1);
            dto.setAuthorFollowerCount(followerCount);
        }
        if (p.getCategory() != null) {
            dto.setCategoryID(p.getCategory().getCategoryID());
            dto.setCategoryName(p.getCategory().getCategoryName());
        }
        if (p.getEvent() != null) {
            dto.setEventID(p.getEvent().getEventID());
            dto.setEventName(p.getEvent().getEventName());
        }

        // Ratings
        List<Rating> ratings = p.getRatings();
        if (ratings != null && !ratings.isEmpty()) {
            dto.setAvgRating(ratings.stream().mapToInt(Rating::getRate).average().orElse(0));
            dto.setRatingCount((long) ratings.size());
        } else {
            dto.setAvgRating(0.0);
            dto.setRatingCount(0L);
        }

        dto.setCommentCount(p.getComments() != null ? (long) p.getComments().size() : 0L);
        dto.setFavoriteCount(p.getFavorites() != null ? (long) p.getFavorites().size() : 0L);

        // Steps
        if (p.getCookingSteps() != null) {
            List<CookingStepDTO> steps = p.getCookingSteps().stream()
                    .sorted(Comparator.comparingInt(CookingSteps::getStepNumber))
                    .map(s -> {
                        CookingStepDTO sdto = new CookingStepDTO();
                        sdto.setStepID(s.getStepID());
                        sdto.setStepNumber(s.getStepNumber());
                        sdto.setContent(s.getContent());
                        sdto.setImage(s.getImage());
                        sdto.setVideo(s.getVideo());
                        return sdto;
                    }).collect(Collectors.toList());
            dto.setSteps(steps);
        }

        // Comments
        if (p.getComments() != null) {
            List<CommentDTO> comments = p.getComments().stream().map(c -> {
                CommentDTO cdto = new CommentDTO();
                cdto.setCommentID(c.getCommentID());
                cdto.setPostID(p.getPostID());
                cdto.setContent(c.getContent());
                if (c.getAccount() != null) {
                    cdto.setAccountID(c.getAccount().getAccountID());
                    cdto.setAuthorName(c.getAccount().getUsername());
                    cdto.setAuthorAvatar(c.getAccount().getAvatar());
                    // Try to find their rating for this post
                    ratingDAO.findByAccount_AccountIDAndPost_PostID(c.getAccount().getAccountID(), p.getPostID())
                            .ifPresent(r -> cdto.setRating(r.getRate()));
                }
                return cdto;
            }).collect(Collectors.toList());
            dto.setComments(comments);
        }

        return dto;
    }

    // ─── My posts (all statuses — owner only) ─────────────────────────
    @GetMapping("/me")
    public ResponseEntity<?> getMyPosts(
            @RequestParam(required = false) PostStatus status) {
        Integer callerId = resolveCallerAccountId();
        if (callerId == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        return ResponseEntity.ok(moderationService.getMyPosts(callerId, status));
    }

    // ─── Create post ────────────────────────────────────────────────────
    @PostMapping
    public ResponseEntity<?> createPost(@RequestBody Map<String, Object> body,
                                        jakarta.servlet.http.HttpServletRequest request) {
        try {
            Integer accountID  = (Integer) body.get("accountID");
            Integer categoryID = (Integer) body.get("categoryID");
            String  title       = (String) body.getOrDefault("title", "");
            String  description = (String) body.getOrDefault("description", "");
            String  ingredients = (String) body.getOrDefault("ingredients", "");
            String  media       = (String) body.getOrDefault("media", "");
            Integer level = body.get("level") instanceof Integer ? (Integer) body.get("level")
                         : body.get("level") != null ? Integer.parseInt(body.get("level").toString()) : 2;
            Integer cookingTime = body.get("cookingTime") instanceof Integer ? (Integer) body.get("cookingTime")
                               : body.get("cookingTime") != null ? Integer.parseInt(body.get("cookingTime").toString()) : 30;

            Account account = accountDAO.findById(accountID).orElse(null);
            if (account == null) return ResponseEntity.badRequest().body(Map.of("code", "INVALID_ACCOUNT", "message", "Invalid accountID"));
            Category category = categoryDAO.findById(categoryID).orElse(null);
            if (category == null) return ResponseEntity.badRequest().body(Map.of("code", "INVALID_CATEGORY", "message", "Invalid categoryID"));

            // ── Anti-spam gate ──
            PostAntiSpamService.SpamCheckResult spamResult =
                    postAntiSpamService.checkAndRecord(account, extractIp(request), title, description, ingredients,
                            (List<Map<String,Object>>) body.get("steps"));
            if (spamResult.blocked()) {
                return ResponseEntity.status(429).body(Map.of(
                        "code", "RATE_LIMITED",
                        "message", spamResult.blockReason()));
            }

            PostStatus initialStatus = PostStatus.PENDING_REVIEW;
            // Extreme spam => auto-hide
            if (spamResult.spamScore() >= 80) {
                initialStatus = PostStatus.HIDDEN;
            }

            Post post = Post.builder()
                    .account(account)
                    .category(category)
                    .title(title)
                    .description(description)
                    .ingredients(ingredients)
                    .media(media)
                    .level(level)
                    .cookingTime(cookingTime)
                    .views(0)
                    .isActive(initialStatus == PostStatus.APPROVED ? 1 : 0)
                    .isApproved(initialStatus == PostStatus.APPROVED ? 1 : 0)
                    .status(initialStatus)
                    .spamScore(spamResult.spamScore())
                    .spamReasons(spamResult.reasonsJson())
                    .createdAt(LocalDate.now())
                    .updatedAt(Instant.now())
                    .build();
            post = postDAO.save(post);

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> steps = (List<Map<String, Object>>) body.get("steps");
            if (steps != null) {
                final Post savedPost = post;
                for (int i = 0; i < steps.size(); i++) {
                    Map<String, Object> s = steps.get(i);
                    CookingSteps step = new CookingSteps();
                    step.setPost(savedPost);
                    step.setStepNumber(i + 1);
                    step.setContent((String) s.getOrDefault("desc", ""));
                    step.setImage((String) s.getOrDefault("image", null));
                    cookingStepsDAO.save(step);
                }
            }

            return ResponseEntity.ok(Map.of(
                    "postID",   post.getPostID(),
                    "status",   post.getStatus().name(),
                    "message",  initialStatus == PostStatus.HIDDEN
                            ? "Your post was flagged and is under review."
                            : "Post submitted and is pending review."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("code", "ERROR", "message", e.getMessage()));
        }
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────

    private Integer resolveCallerAccountId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) return null;
        Object principal = auth.getPrincipal();
        return (principal instanceof Integer) ? (Integer) principal : null;
    }

    private String extractIp(jakarta.servlet.http.HttpServletRequest request) {
        if (request == null) return "0.0.0.0";
        String xff = request.getHeader("X-Forwarded-For");
        return (xff != null && !xff.isBlank()) ? xff.split(",")[0].trim() : request.getRemoteAddr();
    }
}