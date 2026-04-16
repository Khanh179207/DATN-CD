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
import poly.edu.service.ModerationLogService;
import poly.edu.service.NotificationService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminPostServiceImpl implements AdminPostService {

    private final PostDAO postDAO;
    private final AccountDAO accountDAO;
    private final NotificationService notificationService;
    private final ModerationLogService moderationLogService;

    private AdminPostDTO toDTO(Post post) {
        AdminPostDTO dto = new AdminPostDTO();
        dto.setPostID(post.getPostID());
        dto.setTitle(post.getTitle());
        dto.setDescription(post.getDescription());
        dto.setMedia(post.getMedia());
        dto.setLevel(post.getLevel());
        dto.setViews(post.getViews());

        // 🔥 Đẩy cả 2 trạng thái này lên cho Frontend phân tích ma trận
        dto.setIsActive(post.getIsActive());
        dto.setIsApproved(post.getIsApproved());

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
        return postDAO.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public List<AdminPostDTO> findByApproved(Integer isApproved) {
        return postDAO.findByIsApproved(isApproved).stream().map(this::toDTO).collect(Collectors.toList());
    }

    // =========================================================
    // ADMIN DUYỆT BÀI / KHÔI PHỤC BÀI
    // =========================================================
    @Override
    @Transactional
    public void approvePost(Integer id, Integer adminId, String adminName) {
        Post post = postDAO.findById(id).orElseThrow(() -> new RuntimeException("Không tìm thấy bài viết!"));

        // Nếu bài đang bị Admin gỡ (isActive = -1), khôi phục nó lên thành 1
        String logAction = (post.getIsActive() != null && post.getIsActive() == -1) ? "RESTORE" : "APPROVE";
        String logNote = logAction.equals("RESTORE") ? "Khôi phục bài viết bị gỡ" : "Duyệt bài viết hiển thị";

        // 🔥 MA TRẬN: Nếu bài đang là -1 thì lên 1. Nếu đang là 0 (User ẩn) thì giữ nguyên 0.
        if (post.getIsActive() != null && post.getIsActive() == -1) {
            post.setIsActive(1);
        }

        boolean isFirstTimeApproval = (post.getIsApproved() == null || post.getIsApproved() == 0);

        post.setIsApproved(1);
        post.setRejectReason(null);

        postDAO.save(post);

        // Cộng 1 GoMetCoin cho tác giả nếu đây là lần duyệt đầu tiên (từ 0/null lên 1)
        if (isFirstTimeApproval) {
            Account author = post.getAccount();
            if (author != null) {
                author.setPoint((author.getPoint() == null ? 0 : author.getPoint()) + 1);
                accountDAO.save(author);
            }
        }

        moderationLogService.logAction(id, "POST", logAction, adminId, adminName, logNote);
        notificationService.notifyPostApproved(id);
    }

    // =========================================================
    // ADMIN TỪ CHỐI / GỠ BÀI (REJECT/BANNED)
    // =========================================================
    @Override
    @Transactional
    public void rejectPost(Integer id, Integer adminId, String adminName, String reason) {
        Post post = postDAO.findById(id).orElseThrow(() -> new RuntimeException("Không tìm thấy bài viết!"));

        String logAction = (post.getIsApproved() != null && post.getIsApproved() == 1) ? "HIDE" : "REJECT";

        // 🔥 MA TRẬN: Chỉ set isActive = -1. Tuyệt đối KHÔNG SỬA isApproved.
        post.setIsActive(-1);
        post.setRejectReason(reason);

        postDAO.save(post);

        moderationLogService.logAction(id, "POST", logAction, adminId, adminName, reason);
        notificationService.notifyPostRejected(id, reason);
    }

    @Override
    @Transactional
    public void deactivePost(Integer id) {
        // Có thể gọi trực tiếp sang Delete nếu cần, hoặc gỡ bỏ.
        // Tạm thời em map nó sang logic xóa luôn cho gọn gàng.
        deletePost(id);
    }

    @Override
    @Transactional
    public void deletePost(Integer id) {
        Post post = postDAO.findById(id).orElseThrow(() -> new RuntimeException("Không tìm thấy bài viết!"));

        // Đánh dấu Xóa hẳn: set cả isActive và isApproved về -1
        post.setIsActive(-1);
        post.setIsApproved(-1);
        postDAO.save(post);

        // Lưu log (Dùng ID admin mặc định hoặc lấy từ security context nếu Sếp truyền vào)
        moderationLogService.logAction(id, "POST", "DELETE", null, "Hệ Thống", "Admin xóa bài viết");
    }

    @Override
    @Transactional
    public void banAuthorByPostId(Integer postId) {
        Post post = postDAO.findById(postId).orElseThrow(() -> new RuntimeException("Không tìm thấy bài viết!"));
        Account author = post.getAccount();
        if (author == null) throw new RuntimeException("Tác giả không tồn tại!");

        // 🔥 Khóa tài khoản User (Admin Ban là -1)
        author.setIsActive(-1);
        accountDAO.save(author);

        // 🔥 Gọi đúng hàm mới trong PostDAO để "trảm" toàn bộ bài của user này về -1
        postDAO.banAllPostsByAccountId(author.getAccountID());
    }
}