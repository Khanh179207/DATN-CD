package poly.edu.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import poly.edu.dao.AccountDAO;
import poly.edu.dao.CategoryDAO;
import poly.edu.dao.PostDAO;
import poly.edu.dao.CookingStepsDAO;
import poly.edu.dao.BlacklistWordDAO;
import poly.edu.dto.PostDTO;
import poly.edu.dto.StepRequestDTO;
import poly.edu.entity.Account;
import poly.edu.entity.Category;
import poly.edu.entity.Post;
import poly.edu.entity.CookingSteps;
import poly.edu.entity.BlacklistWord;
import poly.edu.service.PostService;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostDAO postDAO;
    private final CategoryDAO categoryDAO;
    private final AccountDAO accountDAO;
    private final CookingStepsDAO cookingStepsDAO;
    private final BlacklistWordDAO blacklistWordDAO;

    @Override
    public List<PostDTO> getPostsByAccountId(Integer accountId) {
        if (accountId == null) return List.of();
        // 🔥 CHỈ LẤY BÀI CHƯA XÓA (isActive = 1)
        List<Post> posts = postDAO.findByAccount_AccountIDOrderByCreatedAtDesc(accountId);
        return posts.stream()
                .filter(p -> p.getIsActive() == 1)
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

        // 🛡️ TẦNG 1: RATE LIMITING (CHỐNG SPAM)
        // Quy tắc: Chỉ chặn User thường và Premium. Admin được đăng liên tục.
        if (acc.getIsAdmin() != 1) {
            List<Post> lastPosts = postDAO.findByAccount_AccountIDOrderByCreatedAtDesc(acc.getAccountID());
            if (!lastPosts.isEmpty() && lastPosts.get(0).getCreatedAt() != null) {
                long minutes = Duration.between(lastPosts.get(0).getCreatedAt(), LocalDateTime.now()).toMinutes();
                if (minutes < 3) {
                    throw new RuntimeException("Hệ thống chống Spam: Sếp đăng bài quá nhanh! Vui lòng đợi " + (3 - minutes) + " phút nữa.");
                }
            }
        }

        // 🛡️ TẦNG 2: BỘ LỌC TỪ KHÓA (BLACKLIST)
        String contentToCheck = (postDTO.getTitle() + " " + postDTO.getDescription() + " " + postDTO.getIngredients()).toLowerCase();
        List<BlacklistWord> badWords = blacklistWordDAO.findAll();
        for (BlacklistWord bw : badWords) {
            if (contentToCheck.contains(bw.getWord().toLowerCase())) {
                throw new RuntimeException("Vi phạm tiêu chuẩn: Nội dung chứa từ khóa bị cấm (" + bw.getWord() + ").");
            }
        }

        // 🛡️ TẦNG 3: ĐẶC QUYỀN DUYỆT BÀI
        // Quy tắc: Admin và Premium được duyệt thẳng.
        int autoApprove = (acc.getIsAdmin() == 1 || acc.getIsPremium() == 1) ? 1 : 0;

        // BẮT ĐẦU LƯU
        Post post = new Post();
        mapDtoToEntity(postDTO, post);

        Category cat = categoryDAO.findById(postDTO.getCategoryID() == null ? 1 : postDTO.getCategoryID())
                .orElseThrow(() -> new RuntimeException("Danh mục không hợp lệ!"));

        post.setCategory(cat);
        post.setAccount(acc);
        post.setIsActive(1);    // Luôn là 1 khi mới tạo
        post.setIsApproved(autoApprove);
        post.setViews(0);
        post.setLikeCount(0);

        Post savedPost = postDAO.save(post);

        // Lưu các bước nấu ăn
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

        return convertToDTO(savedPost);
    }

    @Override
    @Transactional
    public PostDTO updatePost(Integer postId, PostDTO dto) {
        Post post = postDAO.findById(postId)
                .orElseThrow(() -> new RuntimeException("Bài viết không tồn tại!"));

        // 🛡️ CHỐT CHẶN AN NINH CỰC CAO
        if (post.getIsApproved() == -1) {
            throw new RuntimeException("Bài viết này đã bị Admin khóa vĩnh viễn, không thể chỉnh sửa!");
        }
        if (post.getIsActive() == 0) {
            throw new RuntimeException("Bài viết này không còn tồn tại trên hệ thống!");
        }

        mapDtoToEntity(dto, post);

        if (dto.getCategoryID() != null) {
            Category cat = categoryDAO.findById(dto.getCategoryID())
                    .orElseThrow(() -> new RuntimeException("Danh mục không hợp lệ!"));
            post.setCategory(cat);
        }

        return convertToDTO(postDAO.save(post));
    }

    @Override
    @Transactional
    public void deletePost(Integer postId) {
        Post post = postDAO.findById(postId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bài viết để xóa!"));

        // 🔥 SOFT DELETE: Chỉ ẩn đi, không xóa khỏi DB để lưu vết
        post.setIsActive(0);
        postDAO.save(post);
    }

    @Override
    public PostDTO getPostById(Integer postId) {
        Post post = postDAO.findById(postId)
                .orElseThrow(() -> new RuntimeException("Bài viết không tồn tại!"));

        // Nếu bài đã xóa mà vẫn cố truy cập bằng link trực tiếp
        if (post.getIsActive() == 0) {
            throw new RuntimeException("Bài viết này đã được gỡ bỏ!");
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
}