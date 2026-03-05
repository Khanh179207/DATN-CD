package poly.edu.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import poly.edu.dao.AccountDAO;
import poly.edu.dao.CategoryDAO;
import poly.edu.dao.CookingStepsDAO;
import poly.edu.dao.FollowDAO;
import poly.edu.dao.PostDAO;
import poly.edu.dao.RatingDAO;
import poly.edu.dto.*;
import poly.edu.entity.*;
import poly.edu.service.ModerationService;
import poly.edu.service.PostAntiSpamService;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostDAO postDAO;
    private final RatingDAO ratingDAO;
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
        int safePage = Math.max(0, page - 1);
        int safeSize = Math.min(100, Math.max(1, size));
        Pageable pageable = PageRequest.of(safePage, safeSize);
        Page<Post> paged = postDAO.findByIsApprovedAndIsActiveOrderByCreatedAtDesc(1, 1, pageable);
        return ResponseEntity.ok(paged.getContent().stream().map(this::toPublicDTO).collect(Collectors.toList()));
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
            @RequestParam(defaultValue = "newest") String sort,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        int safePage = Math.max(0, page - 1);
        int safeSize = Math.min(100, Math.max(1, size));
        Pageable pageable = PageRequest.of(safePage, safeSize);

        Page<Post> posts;
        if (keyword.isBlank() && categoryID != null) {
            posts = postDAO.findByCategory_CategoryIDAndIsApprovedAndIsActive(categoryID, 1, 1, pageable);
        } else if (!keyword.isBlank() && categoryID != null) {
            posts = postDAO.searchByKeywordAndCategory(keyword, categoryID, pageable);
        } else if (!keyword.isBlank()) {
            posts = postDAO.searchByKeyword(keyword, pageable);
        } else {
            posts = postDAO.findByIsApprovedAndIsActiveOrderByCreatedAtDesc(1, 1, pageable);
        }

        List<PublicPostDTO> result = posts.getContent().stream().map(this::toPublicDTO).collect(Collectors.toList());
        switch (sort) {
            case "views" -> result.sort(java.util.Comparator.comparingInt((PublicPostDTO p) -> {
                Integer views = p.getViews();
                return views != null ? views : 0;
            }).reversed());
            case "rating" -> result.sort(java.util.Comparator.comparingDouble((PublicPostDTO p) -> {
                Double rating = p.getAvgRating();
                return rating != null ? rating : 0.0;
            }).reversed());
            default -> {
            }
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
                    Double avgRating = p.getAvgRating();
                    double rating = avgRating != null ? avgRating : 0;
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
        public ResponseEntity<?> createPost(@RequestBody @Valid CreatePostRequestDTO body,
                                        jakarta.servlet.http.HttpServletRequest request) {
        Integer accountID = body.getAccountID();
        Integer categoryID = body.getCategoryID();
        String title = body.getTitle();
        String description = body.getDescription();
        String ingredients = body.getIngredients();
        String media = Objects.toString(body.getMedia(), "");
        Integer level = body.getLevel() == null ? Integer.valueOf(2) : Integer.valueOf(body.getLevel());
        Integer cookingTime = body.getCookingTime() == null ? Integer.valueOf(30) : Integer.valueOf(body.getCookingTime());

        Account account = accountDAO.findById(accountID)
            .orElseThrow(() -> new IllegalArgumentException("Invalid accountID"));
        Category category = categoryDAO.findById(categoryID)
            .orElseThrow(() -> new IllegalArgumentException("Invalid categoryID"));

        List<String> stepDescriptions = body.getSteps().stream()
            .map(s -> s.getDesc() != null ? s.getDesc() : "")
            .collect(Collectors.toList());

        PostAntiSpamService.SpamCheckResult spamResult =
            postAntiSpamService.checkAndRecord(account, extractIp(request), title, description, ingredients, stepDescriptions);
        if (spamResult.blocked()) {
            return ResponseEntity.status(429).body(java.util.Map.of(
                "code", "RATE_LIMITED",
                "message", spamResult.blockReason()));
        }

        PostStatus initialStatus = spamResult.spamScore() >= 80 ? PostStatus.HIDDEN : PostStatus.PENDING_REVIEW;

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

        List<CreatePostStepRequestDTO> steps = body.getSteps();
        final Post savedPost = post;
        for (int i = 0; i < steps.size(); i++) {
            CreatePostStepRequestDTO s = steps.get(i);
            CookingSteps step = new CookingSteps();
            step.setPost(savedPost);
            step.setStepNumber(i + 1);
            step.setContent(s.getDesc() != null ? s.getDesc() : "");
            step.setImage(s.getImage());
            cookingStepsDAO.save(step);
        }

        return ResponseEntity.ok(java.util.Map.of(
            "postID",   post.getPostID(),
            "status",   post.getStatus().name(),
            "message",  initialStatus == PostStatus.HIDDEN
                ? "Your post was flagged and is under review."
                : "Post submitted and is pending review."));
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