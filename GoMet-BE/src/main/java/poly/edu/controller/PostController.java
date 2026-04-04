package poly.edu.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import poly.edu.dao.*;
import poly.edu.dto.*;
import poly.edu.entity.*;
import poly.edu.service.NotificationService;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime; // 🔥 Đã đổi sang LocalDateTime
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor

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
    private final NotificationService notificationService;
    private final poly.edu.service.PostService postService;
    @Autowired
    private InteractionLogDAO interactionLogDAO;

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
            // 🛡️ BẢO VỆ RIÊNG TƯ: Nếu tác giả đã xóa tài khoản thì bài viết cũng phải ẩn
            if (post.getAccount() != null && post.getAccount().getIsActive() != 1) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Bài viết không tồn tại hoặc đã bị ẩn"));
            }
            PostDetailDTO dto = toDetailDTO(post, accountId);
            return ResponseEntity.ok(dto);
        }).orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Bài viết không tồn tại")));
    }

    @PostMapping("/{id}/view")
    public ResponseEntity<?> recordView(@PathVariable Integer id) {
        try {
            interactionLogDAO.save(new InteractionLog(id, "VIEW", 1));
            return ResponseEntity.ok().body("Đã ghi nhận lượt xem");
        } catch (Exception e) {
            System.err.println("Lỗi lưu InteractionLog VIEW: " + e.getMessage());
            return ResponseEntity.badRequest().body("Lỗi hệ thống");
        }
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
            result.sort(Comparator.comparingInt((PublicPostDTO p) -> p.getViews() != null ? p.getViews() : 0).reversed());
        } else if ("rating".equals(sort)) {
            result.sort(Comparator.comparingDouble((PublicPostDTO p) -> p.getAvgRating() != null ? p.getAvgRating() : 0).reversed());
        } else {
            // 🔥 Sắp xếp theo LocalDateTime (Mới nhất lên đầu)
            result.sort((p1, p2) -> {
                if (p1.getCreatedAt() == null || p2.getCreatedAt() == null) return 0;
                return p2.getCreatedAt().compareTo(p1.getCreatedAt());
            });
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
        // 🔥 TRẢ VỀ TẤT CẢ: Để Frontend tự lọc (Sếp yêu cầu bài ẩn vẫn phải hiện cho chủ sở hữu)
        List<Post> posts = postDAO.findByAccount_AccountIDOrderByCreatedAtDesc(accountID);
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

    public PublicPostDTO toPublicDTO(Post p, Integer accountId) {
        PublicPostDTO dto = new PublicPostDTO();
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
        dto.setIsActive(p.getIsActive());   // 🔥 TRẠNG THÁI ẨN/HIỆN
        dto.setIsApproved(p.getIsApproved()); // 🔥 TRẠNG THÁI DUYỆT

        if (p.getAccount() != null) {
            dto.setAuthorID(p.getAccount().getAccountID());
            dto.setAuthorName(p.getAccount().getUsername());
            dto.setAuthorAvatar(p.getAccount().getAvatar());
            dto.setIsPremium(p.getAccount().getIsPremium() == 1);
        }
        if (p.getCategory() != null) {
            dto.setCategoryID(p.getCategory().getCategoryID());
            dto.setCategoryName(p.getCategory().getCategoryName());
        }

        // --- Rating Logic ---
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

        dto.setFavoriteCount(likesDAO.countByPost_PostID(p.getPostID()));

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
        dto.setCreatedAt(p.getCreatedAt()); // 🔥 Sẽ tự map LocalDateTime

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

        // --- Rating Logic ---
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

        if (p.getComments() != null) {
            List<CommentDTO> comments = p.getComments().stream().map(c -> {
                CommentDTO cdto = new CommentDTO();
                cdto.setCommentID(c.getCommentID());
                cdto.setPostID(p.getPostID());
                cdto.setContent(c.getContent());
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

        return dto;
    }
    @PreAuthorize("isAuthenticated()")
    @PostMapping
    public ResponseEntity<?> createPost(@RequestBody PostDTO postDTO) {
        try {
            // 🔥 BƯỚC 1: Gọi tầng Service xử lý (Lọc Từ Cấm, Chống Spam, Auto Approve)
            PostDTO createdPost = postService.createPost(postDTO);

            // 🔥 BƯỚC 2: Nếu bài viết rơi vào diện Chờ Duyệt (isApproved = 0), bắn thông báo Admin
            if (createdPost.getIsApproved() != null && createdPost.getIsApproved() == 0) {
                try {
                    // Lấy thẳng username từ Database cho chắc chắn
                    Account account = accountDAO.findById(postDTO.getAccountID()).orElse(null);
                    String userUsername = (account != null) ? account.getUsername() : "Unknown User";
                    notificationService.notifyAdminPostPendingApproval(userUsername, createdPost.getPostID());
                } catch (Exception e) {
                    System.err.println("Gửi thông báo Admin thất bại: " + e.getMessage());
                }
            }

            // 🔥 BƯỚC 3: Trả kết quả về Frontend
            return ResponseEntity.ok(createdPost);

        } catch (RuntimeException e) {
            // 🛡️ BẮT LỖI TỪ CẤM / SPAM Ở ĐÂY
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("message", "Lỗi server: " + e.getMessage()));
        }
    }

    // === API BẢNG XẾP HẠNG MÓN ĂN (TOP DISHES) ===
    @GetMapping("/trending")
    public ResponseEntity<?> getTrending(
            @RequestParam(defaultValue = "month") String timeframe,
            @RequestParam(defaultValue = "10") int limit) {
        try {
            // Gọi Service - lúc này Service đã trả về Map chứa đầy đủ: id, title, image, pts...
            List<Map<String, Object>> result = postService.getLeaderboard(timeframe, limit);

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi: " + e.getMessage());
        }
    }

    @GetMapping("/top-comments-batch")
    public ResponseEntity<?> getTopCommentsBatch(@RequestParam List<Integer> postIds) {
        // Nếu không truyền ID nào thì trả về mảng rỗng luôn cho an toàn
        if (postIds == null || postIds.isEmpty()) {
            return ResponseEntity.ok(Collections.emptyMap());
        }

        List<Comment> topComments = commentDAO.findTopCommentsByPostIds(postIds);

        // Tạo một Map để map PostID -> Dữ liệu Top Comment
        Map<Integer, Object> resultMap = new HashMap<>();

        for (Comment cmt : topComments) {
            Map<String, Object> dto = new HashMap<>();

            // Xử lý cắt chuỗi
            String content = cmt.getContent();
            if (content.length() > 75) {
                content = content.substring(0, 72) + "...";
            }
            dto.put("content", content);

            // Xử lý Tác giả
            String author = cmt.getAccount().getUsername();
            dto.put("author", author);

            // Xử lý Avatar
            String avatar = cmt.getAccount().getAvatar();
            if (avatar == null || avatar.isEmpty()) {
                avatar = "https://ui-avatars.com/api/?name=" + URLEncoder.encode(author, StandardCharsets.UTF_8) + "&background=EA580C&color=fff";
            }
            dto.put("avatar", avatar);

            // Gắn vào Map với Key là PostID (Sửa getPost() thành cách gọi tương ứng trong Entity của sếp nếu cần)
            resultMap.put(cmt.getPost().getPostID(), dto);
        }

        return ResponseEntity.ok(resultMap);
    }

    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> updatePost(@PathVariable Integer id, @RequestBody PostDTO postDTO) {
        try {
            return ResponseEntity.ok(postService.updatePost(id, postDTO));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PatchMapping("/{id}/toggle-active")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> toggleActive(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(postService.toggleActive(id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

}