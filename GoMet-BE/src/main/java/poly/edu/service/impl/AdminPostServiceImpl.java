package poly.edu.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import poly.edu.dao.AccountDAO;
import poly.edu.dao.PostDAO;
import poly.edu.dto.AdminPostDTO;
import poly.edu.entity.Account;
import poly.edu.entity.Post;
import poly.edu.service.AdminPostService;
import poly.edu.service.NotificationService;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminPostServiceImpl implements AdminPostService {

    private final PostDAO postDAO;
    private final AccountDAO accountDAO;
    private final NotificationService notificationService;

    // Helper map từ Entity sang DTO cho Admin
    private AdminPostDTO toDTO(Post post) {
        AdminPostDTO dto = new AdminPostDTO();
        dto.setPostID(post.getPostID());
        dto.setTitle(post.getTitle());
        dto.setDescription(post.getDescription());
        dto.setMedia(post.getMedia());
        dto.setLevel(post.getLevel());
        dto.setViews(post.getViews());
        dto.setIsActive(post.getIsActive());
        dto.setIsApproved(post.getIsApproved());

        // 🔥 Đã đồng bộ lấy LocalDateTime từ Entity Post
        dto.setCreatedAt(post.getCreatedAt());

        if (post.getAccount() != null) {
            dto.setUsername(post.getAccount().getUsername());
            dto.setAccountID(post.getAccount().getAccountID());
            dto.setAccountAvatar(post.getAccount().getAvatar());
        }

        if (post.getCategory() != null) {
            dto.setCategoryName(post.getCategory().getCategoryName());
            dto.setCategoryID(post.getCategory().getCategoryID());
        }

        return dto;
    }

    @Override
    public List<AdminPostDTO> findAll() {
        return postDAO.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<AdminPostDTO> findByApproved(Integer isApproved) {
        return postDAO.findByIsApproved(isApproved)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void approvePost(Integer id) {
        Post post = postDAO.findById(id).orElseThrow(() -> new RuntimeException("Không tìm thấy bài viết!"));
        post.setIsApproved(1);
        postDAO.save(post);

        // Gửi thông báo cho chủ bài viết
        notificationService.notifyPostApproved(id);
    }

    @Override
    @Transactional
    public void rejectPost(Integer id, String reason) {
        Post post = postDAO.findById(id).orElseThrow(() -> new RuntimeException("Không tìm thấy bài viết!"));
        post.setIsApproved(-1); // Từ chối phê duyệt
        postDAO.save(post);

        // Gửi thông báo kèm lý do cho chủ bài viết
        notificationService.notifyPostRejected(id, reason);
    }

    @Override
    @Transactional
    public void deactivePost(Integer id) {
        Post post = postDAO.findById(id).orElseThrow(() -> new RuntimeException("Không tìm thấy bài viết!"));
        post.setIsActive(0);
        postDAO.save(post);

        // Thông báo cho người dùng bài viết bị ẩn
        notificationService.notifyPostDisabled(id);
    }

    @Override
    @Transactional
    public void deletePost(Integer id) {
        Post post = postDAO.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bài viết số " + id));

        // 🔥 Xử lý "Trảm mềm" (Soft Delete) để giữ lại data đối soát
        post.setIsActive(0);
        post.setIsApproved(-1);
        postDAO.save(post);
    }

    @Override
    @Transactional
    public void banAuthorByPostId(Integer postId) {
        Post post = postDAO.findById(postId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bài viết!"));
        Account author = post.getAccount();

        if (author == null)
            throw new RuntimeException("Tác giả không tồn tại!");

        // 1. Trảm tài khoản
        author.setIsActive(0);

        // 2. Đá văng khỏi app bằng cách xóa Token
        author.setToken(null);
        accountDAO.save(author);

        // 3. Quét sạch: Ẩn toàn bộ bài viết của tài khoản này
        postDAO.deactivateAllPostsByAccountId(author.getAccountID());
    }
}