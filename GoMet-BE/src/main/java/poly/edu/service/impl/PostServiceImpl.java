package poly.edu.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import poly.edu.dao.*;
import poly.edu.dto.PostDTO;
import poly.edu.dto.StepRequestDTO;
import poly.edu.entity.Account;
import poly.edu.entity.Category;
import poly.edu.entity.Post;
import poly.edu.entity.CookingSteps;
import poly.edu.service.PostService;
import poly.edu.service.BlacklistService;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import poly.edu.service.NotificationService;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    @Autowired
    private final PostDAO postDAO;

    @Autowired
    private InteractionLogDAO interactionLogDAO;

    private final CategoryDAO categoryDAO;
    private final AccountDAO accountDAO;
    private final CookingStepsDAO cookingStepsDAO;
    private final BlacklistService blacklistService;
    private final NotificationService notificationService;

    @Override
    public List<PostDTO> getPostsByAccountId(Integer accountId) {
        if (accountId == null) return List.of();

        // 🔥 Lấy TOÀN BỘ (Cả isActive = 0) để User có thể quản lý ẩn/hiện trong Profile
        List<Post> posts = postDAO.findByAccount_AccountIDOrderByCreatedAtDesc(accountId);

        // 🔥 MA TRẬN: Trả về bài (1 1), (1 0), (0 1), (0 0). CHỈ CHẶN bài bị Admin gỡ (-1 x)
        return posts.stream()
                .filter(p -> p.getIsActive() != null && p.getIsActive() != -1)
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public PostDTO createPost(PostDTO postDTO) {
        if (postDTO.getAccountID() == null) {
            throw new RuntimeException("Thiếu mã tài khoản người đăng!");
        }

        Account acc = accountDAO.findById(postDTO.getAccountID())
                .orElseThrow(() -> new RuntimeException("Tài khoản không tồn tại!"));

        if (acc.getIsAdmin() != 1) {
            List<Post> lastPosts = postDAO.findByAccount_AccountIDOrderByCreatedAtDesc(acc.getAccountID());
            if (!lastPosts.isEmpty() && lastPosts.get(0).getCreatedAt() != null) {
                long seconds = Duration.between(lastPosts.get(0).getCreatedAt(), LocalDateTime.now()).getSeconds();
                if (seconds < 180) {
                    throw new RuntimeException("Hệ thống chống Spam: Sếp đăng bài quá nhanh! Vui lòng đợi "
                            + (180 - seconds) + " giây nữa.");
                }
            }
        }

        StringBuilder contentToCheck = new StringBuilder();
        contentToCheck.append(postDTO.getTitle()).append(" ")
                .append(postDTO.getDescription()).append(" ")
                .append(postDTO.getIngredients());

        if (postDTO.getSteps() != null) {
            for (StepRequestDTO step : postDTO.getSteps()) {
                contentToCheck.append(" ").append(step.getDesc());
            }
        }

        if (blacklistService.containsBadWord(contentToCheck.toString())) {
            throw new RuntimeException("Vi phạm tiêu chuẩn: Nội dung bài viết chứa từ khóa bị cấm.");
        }

        int autoApprove = (acc.getIsAdmin() == 1 || acc.getIsPremium() == 1) ? 1 : 0;

        Post post = new Post();
        mapDtoToEntity(postDTO, post);

        Category cat = categoryDAO.findById(postDTO.getCategoryID() == null ? 1 : postDTO.getCategoryID())
                .orElseThrow(() -> new RuntimeException("Danh mục không hợp lệ!"));

        post.setCategory(cat);
        post.setAccount(acc);
        post.setViews(0);
        post.setLikeCount(0);

        // 🔥 MA TRẬN: Admin/Premium tạo bài ra (1 1), User tạo bài ra (1 0)
        post.setIsActive(1);
        post.setIsApproved(autoApprove);

        Post savedPost = postDAO.save(post);

        // 🔥 CỘNG 1 GOMETCOIN: Nếu bài được auto-approve (Admin/Premium) thì cộng điểm ngay
        if (autoApprove == 1) {
            acc.setPoint((acc.getPoint() == null ? 0 : acc.getPoint()) + 1);
            accountDAO.save(acc);
        }

        if (postDTO.getSteps() != null) {
            for (int i = 0; i < postDTO.getSteps().size(); i++) {
                StepRequestDTO stepDto = postDTO.getSteps().get(i);
                CookingSteps stepEntity = new CookingSteps();
                stepEntity.setPost(savedPost);
                stepEntity.setStepNumber(i + 1);
                stepEntity.setContent(stepDto.getDesc());
                stepEntity.setImage(stepDto.getImage());
                cookingStepsDAO.save(stepEntity);
            }
        }

        // Notify mentioned users
        extractAndNotifyMentions(contentToCheck.toString(), acc.getUsername(), savedPost.getPostID(), null);

        return convertToDTO(savedPost);
    }

    @Override
    @Transactional
    public PostDTO updatePost(Integer postId, PostDTO dto) {
        Post post = postDAO.findById(postId)
                .orElseThrow(() -> new RuntimeException("Bài viết không tồn tại!"));

        // 🛡️ CHỐT CHẶN AN NINH CỰC CAO (Admin đã gỡ -1)
        if (post.getIsApproved() == -1 || post.getIsActive() == -1) {
            throw new RuntimeException("Bài viết này đã bị Admin khóa/gỡ, không thể chỉnh sửa!");
        }

        mapDtoToEntity(dto, post);

        if (dto.getCategoryID() != null) {
            Category cat = categoryDAO.findById(dto.getCategoryID())
                    .orElseThrow(() -> new RuntimeException("Danh mục không hợp lệ!"));
            post.setCategory(cat);
        }

        Post savedPost = postDAO.save(post);

        StringBuilder contentToCheck = new StringBuilder();
        contentToCheck.append(dto.getTitle() != null ? dto.getTitle() : "").append(" ")
                .append(dto.getDescription() != null ? dto.getDescription() : "").append(" ")
                .append(dto.getIngredients() != null ? dto.getIngredients() : "");
        if (dto.getSteps() != null) {
            for (StepRequestDTO step : dto.getSteps()) {
                contentToCheck.append(" ").append(step.getDesc());
            }
        }
        extractAndNotifyMentions(contentToCheck.toString(), post.getAccount().getUsername(), savedPost.getPostID(), null);

        return convertToDTO(savedPost);
    }

    @Override
    @Transactional
    public void deletePost(Integer postId) {
        Post post = postDAO.findById(postId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bài viết để xóa!"));

        if (post.getIsActive() == -1) {
            throw new RuntimeException("Bài viết này đã bị Admin xử lý, không thể thay đổi trạng thái!");
        }

        // 🔥 MA TRẬN: Đảo trạng thái 1 thành 0, 0 thành 1 (Chức năng User tự ẩn/hiện
        // bài)
        post.setIsActive(post.getIsActive() == 1 ? 0 : 1);
        postDAO.save(post);
    }

    @Override
    @Transactional
    public PostDTO toggleActive(Integer postId) {
        Post post = postDAO.findById(postId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bài viết!"));

        // Đảo trạng thái: 1 -> 0 hoặc 0 -> 1
        post.setIsActive(post.getIsActive() == 1 ? 0 : 1);
        return convertToDTO(postDAO.save(post));
    }

    @Override
    public PostDTO getPostById(Integer postId) {
        Post post = postDAO.findById(postId)
                .orElseThrow(() -> new RuntimeException("Bài viết không tồn tại!"));

        // 🔥 Chặn xem public nếu bài không phải (1 1).
        // Nếu sếp muốn tác giả được xem bài của họ, hãy handle thêm logic ID người gọi
        // API ở đây.
        if (post.getIsActive() != 1 || post.getIsApproved() != 1) {
            throw new RuntimeException("Bài viết này đang chờ duyệt hoặc đã bị gỡ!");
        }

        return convertToDTO(post);
    }

    private void mapDtoToEntity(PostDTO dto, Post entity) {
        entity.setTitle(dto.getTitle());
        entity.setDescription(dto.getDescription());
        entity.setIngredients(dto.getIngredients());
        entity.setMedia(dto.getMedia());
        entity.setVideo(dto.getVideo());
        entity.setLevel(dto.getLevel() != null ? dto.getLevel() : 1);
        entity.setCookingTime(dto.getCookingTime() != null ? dto.getCookingTime() : 30);

        if (entity.getPostID() == null) {
            entity.setCreatedAt(LocalDateTime.now());
        }
    }

    private PostDTO convertToDTO(Post post) {
        PostDTO dto = new PostDTO();
        dto.setPostID(post.getPostID());
        dto.setTitle(post.getTitle());
        dto.setDescription(post.getDescription());
        dto.setIngredients(post.getIngredients());
        dto.setMedia(post.getMedia());
        dto.setVideo(post.getVideo());
        dto.setLevel(post.getLevel());
        dto.setCookingTime(post.getCookingTime());
        dto.setViews(post.getViews());
        dto.setLikeCount(post.getLikeCount());
        dto.setIsApproved(post.getIsApproved());
        dto.setIsActive(post.getIsActive());
        dto.setCreatedAt(post.getCreatedAt());

        if (post.getAccount() != null) {
            dto.setAccountID(post.getAccount().getAccountID());
            dto.setUsername(post.getAccount().getUsername());
            dto.setAuthorAvatar(post.getAccount().getAvatar());
        }
        if (post.getCategory() != null) {
            dto.setCategoryID(post.getCategory().getCategoryID());
            dto.setCategoryName(post.getCategory().getCategoryName());
        }

        return dto;
    }

    @Override
    public List<Map<String, Object>> getLeaderboard(String timeframe, int limit) {
        LocalDateTime startDate;
        LocalDateTime now = LocalDateTime.now();

        if (timeframe == null)
            timeframe = "month";

        switch (timeframe.toLowerCase()) {
            case "day":
                startDate = now.toLocalDate().atStartOfDay();
                break;
            case "month":
                startDate = now.withDayOfMonth(1).toLocalDate().atStartOfDay();
                break;
            case "year":
                startDate = now.withDayOfYear(1).toLocalDate().atStartOfDay();
                break;
            default:
                startDate = now.withDayOfMonth(1).toLocalDate().atStartOfDay();
        }

        return interactionLogDAO.findTopTrending(startDate, limit);
    }

    private void extractAndNotifyMentions(String content, String mentioner, Integer postId, Integer commentId) {
        if (content == null || content.isEmpty()) return;
        Pattern pattern = Pattern.compile("(?<=^|(?<=[^a-zA-Z0-9-_\\.]))@([A-Za-z0-9_]+)");
        Matcher matcher = pattern.matcher(content);
        List<String> mentionedUsernames = new ArrayList<>();
        while (matcher.find()) {
            String username = matcher.group(1);
            if (!mentionedUsernames.contains(username) && !username.equals(mentioner)) {
                mentionedUsernames.add(username);
                accountDAO.findByUsername(username).ifPresent(acc -> {
                    notificationService.notifyMention(mentioner, acc.getAccountID(), postId, commentId);
                });
            }
        }
    }
}