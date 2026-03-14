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
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminPostServiceImpl implements AdminPostService {

    private final PostDAO postDAO;
    private final AccountDAO accountDAO;

    private AdminPostDTO toDTO(Post post) {
        AdminPostDTO dto = new AdminPostDTO();
        dto.setPostID(post.getPostID());
        dto.setTitle(post.getTitle());
        dto.setDescription(post.getDescription());
        dto.setMedia(post.getMedia());
        dto.setLevel(post.getLevel());
        dto.setCookingTime(post.getCookingTime());
        dto.setViews(post.getViews());
        dto.setIsActive(post.getIsActive());
        dto.setIsApproved(post.getIsApproved());
        dto.setCreatedAt(post.getCreatedAt());

        dto.setUsername(post.getAccount().getUsername());
        dto.setAccountID(post.getAccount().getAccountID());
        dto.setAccountAvatar(post.getAccount().getAvatar());
        dto.setCategoryName(post.getCategory().getCategoryName());
        dto.setCategoryID(post.getCategory().getCategoryID());

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
    public void approvePost(Integer id) {
        Post post = postDAO.findById(id).orElseThrow();
        post.setIsApproved(1);
        postDAO.save(post);
    }

    @Override
    public void deactivePost(Integer id) {
        Post post = postDAO.findById(id).orElseThrow();
        post.setIsActive(0);
        postDAO.save(post);
    }

    // Sửa hàm này, TUYỆT ĐỐI KHÔNG dùng postDAO.deleteById(id) nữa sếp nhé!
    @Override
    public void deletePost(Integer id) {
        Post post = postDAO.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bài viết số " + id));

        // 🔥 Trảm mềm: Chuyển trạng thái hoạt động về 0 (Bị ẩn)
        post.setIsActive(0);

        // (Tùy chọn) Sếp có thể tước luôn tích xanh phê duyệt của nó
        post.setIsApproved(-1);

        postDAO.save(post);
    }
    // Tiêm thêm DAO này vào

    @Override
    @Transactional // Nhớ thêm Transactional để đảm bảo nếu lỗi thì không ban cũng không ẩn bài
    public void banAuthorByPostId(Integer postId) {
        Post post = postDAO.findById(postId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bài viết!"));
        Account author = post.getAccount();

        if (author == null) throw new RuntimeException("Tác giả không tồn tại!");

        // 1. Trảm tài khoản: Đổi trạng thái thành 0 (BANNED)
        author.setIsActive(0);

        // 🔥 2. ĐÁ VĂNG KHỎI APP: Xóa trắng cái Token hiện tại của họ
        author.setToken(null);
        accountDAO.save(author);

        // 🚀 3. QUÉT SẠCH DẤU VẾT: Ẩn toàn bộ bài viết của thằng này
        // Giả sử sếp dùng cột 'is_active' hoặc 'status' trong bảng Post để ẩn bài
        postDAO.deactivateAllPostsByAccountId(author.getAccountID());
    }
}
