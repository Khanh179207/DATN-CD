package poly.edu.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import poly.edu.dao.*;
import poly.edu.dto.*;
import poly.edu.entity.*;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
@CrossOrigin("*")
public class PostController {

    private final PostDAO postDAO;
    private final FavoriteDAO favoriteDAO;
    private final CommentDAO commentDAO;
    private final FollowDAO followDAO;
    private final AccountDAO accountDAO;
    private final CategoryDAO categoryDAO;
    private final CookingStepsDAO cookingStepsDAO;
    private final SimpMessagingTemplate messagingTemplate;
    private final LikesDAO likesDAO;

    @GetMapping("/search-mini")
    public ResponseEntity<List<Map<String, Object>>> searchMini(@RequestParam String q) {
        List<Post> posts = postDAO.searchByKeyword(q).stream()
                .filter(p -> p.getIsApproved() == 1 && p.getIsActive() == 1)
                .limit(10)
                .collect(Collectors.toList());

        List<Map<String, Object>> result = posts.stream().map(p -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", p.getPostID());
            map.put("title", p.getTitle());
            return map;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }

    @GetMapping("/account/{accountId}")
    public ResponseEntity<List<PublicPostDTO>> getPostsByAccountAPI(
            @PathVariable Integer accountId,
            @RequestParam(required = false) Integer currentUserId) {
        List<Post> posts = postDAO.findByAccount_AccountIDOrderByCreatedAtDesc(accountId);
        List<PublicPostDTO> result = posts.stream()
                .map(p -> toPublicDTO(p, currentUserId))
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/latest")
    public ResponseEntity<List<PublicPostDTO>> getLatest(
            @RequestParam(defaultValue = "8") int limit,
            @RequestParam(required = false) Integer accountId) {
        List<Post> posts = postDAO.findLatest().stream()
                .limit(limit)
                .collect(Collectors.toList());
        return ResponseEntity.ok(posts.stream().map(p -> toPublicDTO(p, accountId)).collect(Collectors.toList()));
    }

    @GetMapping
    public ResponseEntity<List<PublicPostDTO>> getAll(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(required = false) Integer accountId) {
        List<Post> all = postDAO.findByIsApprovedAndIsActive(1, 1);
        int from = Math.min((page - 1) * size, all.size());
        int to = Math.min(from + size, all.size());
        List<Post> paged = all.subList(from, to);
        return ResponseEntity.ok(paged.stream().map(p -> toPublicDTO(p, accountId)).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getDetail(
            @PathVariable Integer id,
            @RequestParam(required = false) Integer accountId) {
        return postDAO.findById(id).map(post -> {
            post.setViews(post.getViews() + 1);
            postDAO.save(post);
            PostDetailDTO dto = toDetailDTO(post, accountId);
            return ResponseEntity.ok(dto);
        }).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public ResponseEntity<List<PublicPostDTO>> search(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(required = false) Integer categoryID,
            @RequestParam(defaultValue = "newest") String sort,
            @RequestParam(required = false) Integer accountId) {
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

        List<PublicPostDTO> result = posts.stream().map(p -> toPublicDTO(p, accountId)).collect(Collectors.toList());
        if ("views".equals(sort)) {
            result.sort(
                    Comparator.comparingInt((PublicPostDTO p) -> p.getViews() != null ? p.getViews() : 0).reversed());
        } else if ("rating".equals(sort)) {
            result.sort(Comparator.comparingDouble((PublicPostDTO p) -> p.getAvgRating() != null ? p.getAvgRating() : 0)
                    .reversed());
        } else {
            result.sort(Comparator
                    .comparing((PublicPostDTO p) -> p.getCreatedAt() != null ? p.getCreatedAt().toString() : "")
                    .reversed());
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}/related")
    public ResponseEntity<List<PublicPostDTO>> getRelated(
            @PathVariable Integer id,
            @RequestParam(defaultValue = "4") int limit,
            @RequestParam(required = false) Integer accountId) {
        return postDAO.findById(id).map(post -> {
            List<Post> related = postDAO.findRelated(post.getCategory().getCategoryID(), id);
            List<PublicPostDTO> result = related.stream()
                    .limit(limit)
                    .map(p -> toPublicDTO(p, accountId))
                    .collect(Collectors.toList());
            return ResponseEntity.ok(result);
        }).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/by-user/{accountID}")
    public ResponseEntity<List<PublicPostDTO>> getByUser(
            @PathVariable Integer accountID,
            @RequestParam(required = false) Integer currentUserId) {
        List<Post> posts = postDAO.findByAccount_AccountIDAndIsApprovedAndIsActive(accountID, 1, 1);
        return ResponseEntity.ok(posts.stream().map(p -> toPublicDTO(p, currentUserId)).collect(Collectors.toList()));
    }

    @GetMapping("/suggest")
    public ResponseEntity<List<PublicPostDTO>> suggest(
            @RequestParam(required = false) Integer categoryID,
            @RequestParam(required = false) Integer difficulty,
            @RequestParam(required = false) Integer maxTime,
            @RequestParam(defaultValue = "0") Integer excludeId,
            @RequestParam(required = false) Integer accountId) {
        List<Post> all = postDAO.findByIsApprovedAndIsActive(1, 1);
        List<Post> filtered = all.stream()
                .filter(p -> categoryID == null || categoryID.equals(p.getCategory().getCategoryID()))
                .filter(p -> difficulty == null || difficulty.equals(p.getLevel()))
                .filter(p -> maxTime == null || p.getCookingTime() <= maxTime)
                .filter(p -> !p.getPostID().equals(excludeId))
                .collect(Collectors.toList());
        return ResponseEntity.ok(filtered.stream()
                .limit(20)
                .map(p -> toPublicDTO(p, accountId))
                .collect(Collectors.toList()));
    }

    @GetMapping("/trending")
    public ResponseEntity<List<PublicPostDTO>> getTrending(
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(required = false) Integer accountId) {
        List<Post> posts = postDAO.findByIsApprovedAndIsActive(1, 1);
        List<PublicPostDTO> result = posts.stream().map(p -> toPublicDTO(p, accountId))
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

    public PublicPostDTO toPublicDTO(Post p, Integer accountId) {
        PublicPostDTO dto = new PublicPostDTO();
        dto.setPostID(p.getPostID());
        dto.setTitle(p.getTitle());
        dto.setDescription(p.getDescription());
        dto.setMedia(p.getMedia());
        dto.setVideo(p.getVideo());
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

        // ================= FIX Ở ĐÂY: Tính Rating từ bảng Comment mới =================
        List<Comment> ratedComments = p.getComments() != null ?
                p.getComments().stream()
                        .filter(c -> c.getRating() != null && c.getRating() > 0)
                        .collect(Collectors.toList()) : List.of();

        if (!ratedComments.isEmpty()) {
            dto.setAvgRating(ratedComments.stream().mapToInt(Comment::getRating).average().orElse(0.0));
            dto.setRatingCount((long) ratedComments.size());
        } else {
            dto.setAvgRating(0.0);
            dto.setRatingCount(0L);
        }

        long commentCount = p.getComments() != null ?
                p.getComments().stream()
                        .filter(c -> c.getRating() == null || c.getRating() == 0)
                        .count() : 0L;
        dto.setCommentCount(commentCount);
        // ============================================================================

        long realLikes = likesDAO.countByPost_PostID(p.getPostID());
        dto.setFavoriteCount(realLikes);

        boolean isLiked = false;
        if (accountId != null) {
            isLiked = likesDAO.existsByAccount_AccountIDAndPost_PostID(accountId, p.getPostID());
        }
        dto.setIsLiked(isLiked);

        return dto;
    }

    public PostDetailDTO toDetailDTO(Post p, Integer accountId) {
        PostDetailDTO dto = new PostDetailDTO();
        dto.setPostID(p.getPostID());
        dto.setTitle(p.getTitle());
        dto.setDescription(p.getDescription());
        dto.setIngredients(p.getIngredients());
        dto.setMedia(p.getMedia());
        dto.setVideo(p.getVideo());
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
            dto.setAuthorBio(author.getBio());
        }
        if (p.getCategory() != null) {
            dto.setCategoryID(p.getCategory().getCategoryID());
            dto.setCategoryName(p.getCategory().getCategoryName());
        }
        if (p.getEvent() != null) {
            dto.setEventID(p.getEvent().getEventID());
            dto.setEventName(p.getEvent().getEventName());
        }

        // ================= FIX Ở ĐÂY: Tính Rating từ bảng Comment mới =================
        List<Comment> ratedComments = p.getComments() != null ?
                p.getComments().stream()
                        .filter(c -> c.getRating() != null && c.getRating() > 0)
                        .collect(Collectors.toList()) : List.of();

        if (!ratedComments.isEmpty()) {
            dto.setAvgRating(ratedComments.stream().mapToInt(Comment::getRating).average().orElse(0.0));
            dto.setRatingCount((long) ratedComments.size());
        } else {
            dto.setAvgRating(0.0);
            dto.setRatingCount(0L);
        }

        long commentCount = p.getComments() != null ?
                p.getComments().stream()
                        .filter(c -> c.getRating() == null || c.getRating() == 0)
                        .count() : 0L;
        dto.setCommentCount(commentCount);
        // ============================================================================

        dto.setFavoriteCount(likesDAO.countByPost_PostID(p.getPostID()));

        if (accountId != null) {
            dto.setIsLiked(likesDAO.existsByAccount_AccountIDAndPost_PostID(accountId, p.getPostID()));
        }

        if (p.getCookingSteps() != null) {
            List<CookingStepDTO> steps = p.getCookingSteps().stream()
                    .sorted(Comparator.comparingInt(CookingSteps::getStepNumber))
                    .map(s -> {
                        CookingStepDTO sdto = new CookingStepDTO();
                        sdto.setStepID(s.getStepID());
                        sdto.setStepNumber(s.getStepNumber());
                        sdto.setContent(s.getContent());
                        sdto.setImage(s.getImage());
                        return sdto;
                    }).collect(Collectors.toList());
            dto.setSteps(steps);
        }

        // ================= FIX Ở ĐÂY: Gỡ bỏ RatingDAO khỏi map Comment =================
        if (p.getComments() != null) {
            List<CommentDTO> comments = p.getComments().stream().map(c -> {
                CommentDTO cdto = new CommentDTO();
                cdto.setCommentID(c.getCommentID());
                cdto.setPostID(p.getPostID());
                cdto.setContent(c.getContent());

                // Lấy số sao trực tiếp từ Entity Comment
                cdto.setRating(c.getRating() != null ? c.getRating() : 0);

                if (c.getAccount() != null) {
                    cdto.setAccountID(c.getAccount().getAccountID());
                    cdto.setAuthorName(c.getAccount().getUsername());
                    cdto.setAuthorAvatar(c.getAccount().getAvatar());
                }
                return cdto;
            }).collect(Collectors.toList());
            dto.setComments(comments);
        }
        // ============================================================================

        return dto;
    }

    @PostMapping
    public ResponseEntity<?> createPost(@RequestBody PostDTO dto) {
        try {
            Account account = accountDAO.findById(dto.getAccountID()).orElse(null);
            if (account == null)
                return ResponseEntity.badRequest().body("Invalid accountID");
            Category category = categoryDAO.findById(dto.getCategoryID()).orElse(null);
            if (category == null)
                return ResponseEntity.badRequest().body("Invalid categoryID");

            Post post = Post.builder()
                    .account(account)
                    .category(category)
                    .title(dto.getTitle() != null ? dto.getTitle() : "")
                    .description(dto.getDescription() != null ? dto.getDescription() : "")
                    .ingredients(dto.getIngredients() != null ? dto.getIngredients() : "")
                    .media(dto.getMedia() != null ? dto.getMedia() : "")
                    .video(dto.getVideo())
                    .level(dto.getLevel() != null ? dto.getLevel() : 2)
                    .cookingTime(dto.getCookingTime() != null ? dto.getCookingTime() : 30)
                    .views(0)
                    .isActive(1)
                    .isApproved(0)
                    .createdAt(LocalDate.now())
                    .build();
            post = postDAO.save(post);

            sendAdminAlert(post);

            if (dto.getSteps() != null) {
                int stepNum = 1;
                for (StepRequestDTO s : dto.getSteps()) {
                    CookingSteps step = new CookingSteps();
                    step.setPost(post);
                    step.setStepNumber(stepNum++);
                    step.setContent(s.getDesc() != null ? s.getDesc() : "");
                    step.setImage(s.getImage());
                    cookingStepsDAO.save(step);
                }
            }

            return ResponseEntity
                    .ok(Map.of("postID", post.getPostID(), "message", "Post created and pending approval"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    private void sendAdminAlert(Post post) {
        try {
            Map<String, Object> alert = Map.of(
                    "id", "post-" + post.getPostID(),
                    "type", "POST_PENDING",
                    "title", post.getTitle(),
                    "message",
                    "New post waiting for approval: \"" + post.getTitle() + "\" by " + post.getAccount().getUsername(),
                    "createdAt", post.getCreatedAt().toString());
            messagingTemplate.convertAndSend("/topic/admin-alerts", (Object) alert);
        } catch (Exception e) {
            System.err.println("Failed to send admin alert: " + e.getMessage());
        }
    }
}