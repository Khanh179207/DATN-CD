package poly.edu.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import poly.edu.dao.AccountDAO;
import poly.edu.dao.CategoryDAO;
import poly.edu.dao.PostDAO;
import poly.edu.dao.CookingStepsDAO; // 🔥 Nhớ Import DAO của CookingSteps
import poly.edu.dto.PostDTO;
import poly.edu.dto.StepRequestDTO;
import poly.edu.entity.Account;
import poly.edu.entity.Category;
import poly.edu.entity.Post;
import poly.edu.entity.CookingSteps; // 🔥 Nhớ Import Entity CookingSteps
import poly.edu.service.PostService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostDAO postDAO;
    private final CategoryDAO categoryDAO;
    private final AccountDAO accountDAO;
    private final CookingStepsDAO cookingStepsDAO; // 🔥 Inject thêm DAO này

    @Override
    public List<PostDTO> getPostsByAccountId(Integer accountId) {
        if (accountId == null) return List.of();
        List<Post> posts = postDAO.findByAccount_AccountIDOrderByCreatedAtDesc(accountId);
        return posts.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public PostDTO createPost(PostDTO postDTO) {
        if (postDTO.getAccountID() == null) {
            throw new RuntimeException("Không thể tạo bài viết vì thiếu ID tài khoản (Người dùng chưa đăng nhập)!");
        }

        Post post = new Post();

        // 1. Map thông tin cơ bản
        mapDtoToEntity(postDTO, post);

        // 2. Logic ID 1
        Integer targetCatID = (postDTO.getCategoryID() == null) ? 1 : postDTO.getCategoryID();
        Category cat = categoryDAO.findById(targetCatID)
                .orElseThrow(() -> new RuntimeException("Danh mục ID " + targetCatID + " không tồn tại!"));
        post.setCategory(cat);

        // 3. Map người dùng
        Account acc = accountDAO.findById(postDTO.getAccountID())
                .orElseThrow(() -> new RuntimeException("Tài khoản ID " + postDTO.getAccountID() + " không tồn tại trên hệ thống!"));
        post.setAccount(acc);

        // 4. Trạng thái mặc định
        post.setIsActive(1);
        post.setIsApproved(0);
        post.setViews(0);
        post.setLikeCount(0);

        // 🔥 5. Lưu Post trước để lấy ID
        Post savedPost = postDAO.save(post);

        // 🔥 6. XỬ LÝ LƯU COOKING STEPS
        if (postDTO.getSteps() != null && !postDTO.getSteps().isEmpty()) {
            for (int i = 0; i < postDTO.getSteps().size(); i++) {
                StepRequestDTO stepDto = postDTO.getSteps().get(i);

                CookingSteps stepEntity = new CookingSteps();
                stepEntity.setPost(savedPost); // Gắn vào bài viết vừa lưu
                stepEntity.setStepNumber(i + 1); // Đánh số tự động: 1, 2, 3...

                // Map từ StepRequestDTO sang Entity (Khớp tên biến của sếp)
                stepEntity.setContent(stepDto.getDesc());
                stepEntity.setImage(stepDto.getImage()); // Sếp xem lại entity có trường này không, nếu là mediaUrl thì đổi lại nhé

                cookingStepsDAO.save(stepEntity);
            }
        }

        return convertToDTO(savedPost);
    }

    @Override
    @Transactional
    public PostDTO updatePost(Integer postId, PostDTO dto) {
        if (postId == null) throw new RuntimeException("ID bài viết không được để trống!");

        Post post = postDAO.findById(postId)
                .orElseThrow(() -> new RuntimeException("Bài viết không tồn tại để sửa!"));

        mapDtoToEntity(dto, post);

        if (dto.getCategoryID() != null) {
            Category cat = categoryDAO.findById(dto.getCategoryID())
                    .orElseThrow(() -> new RuntimeException("Danh mục mới không hợp lệ!"));
            post.setCategory(cat);
        }

        return convertToDTO(postDAO.save(post));
    }

    @Override
    public PostDTO getPostById(Integer postId) {
        if (postId == null) throw new RuntimeException("ID bài viết không hợp lệ!");
        Post post = postDAO.findById(postId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bài viết!"));
        return convertToDTO(post);
    }

    @Override
    @Transactional
    public void deletePost(Integer postId) {
        if (postId == null) return;
        Post post = postDAO.findById(postId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bài để xóa!"));
        post.setIsActive(0);
        postDAO.save(post);
    }

    // --- HELPER METHODS ---

    private void mapDtoToEntity(PostDTO dto, Post entity) {
        entity.setTitle(dto.getTitle());
        entity.setDescription(dto.getDescription());
        entity.setIngredients(dto.getIngredients());
        entity.setMedia(dto.getMedia());
        entity.setVideo(dto.getVideo());
        entity.setLevel(dto.getLevel() != null ? dto.getLevel() : 1);
        entity.setCookingTime(dto.getCookingTime() != null ? dto.getCookingTime() : 30);
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

        if (post.getAccount() != null) dto.setAccountID(post.getAccount().getAccountID());
        if (post.getCategory() != null) {
            dto.setCategoryID(post.getCategory().getCategoryID());
            dto.setCategoryName(post.getCategory().getCategoryName());
        }

        // Sếp có thể thêm code convert list CookingSteps sang DTO ở đây nếu sau này API cần trả về detail nhé

        return dto;
    }
}