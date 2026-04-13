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

    // 🔥 INJECT THÊM MÁY NGHE LÉN ĐỂ GHI LOG
    private final ModerationLogService moderationLogService;

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

    // 🔥 LOGIC DUYỆT BÀI (ĐÃ THÊM LƯU LOG & XÓA LÝ DO CŨ)
    @Override
    @Transactional
    public void approvePost(Integer id, Integer adminId, String adminName) {
        Post post = postDAO.findById(id).orElseThrow(() -> new RuntimeException("Không tìm thấy bài viết!"));

        // 🔥 LOGIC TÁCH BIỆT LOG:
        // Nếu bài đang bị ẩn (isActive = 0) thì ghi là RESTORE
        // Nếu bài đang chờ duyệt bình thường thì ghi là APPROVE
        String logAction = (post.getIsActive() != null && post.getIsActive() == 0) ? "RESTORE" : "APPROVE";
        String logNote = logAction.equals("RESTORE") ? "Khôi phục bài viết bị gỡ" : "Duyệt bài viết hiển thị";

        post.setIsApproved(1);
        post.setIsActive(1);

        // Rửa sạch lý do cũ
        post.setRejectReason(null);
        post.setRejectedAt(null);
        postDAO.save(post);

        // Ghi sổ Nam Tào với hành động chuẩn xác
        moderationLogService.logAction(id, "POST", logAction, adminId, adminName, logNote);

        notificationService.notifyPostApproved(id);
    }

    // 🔥 LOGIC TỪ CHỐI / ẨN BÀI (ĐÃ FIX TÁCH BIỆT LOG REJECT VÀ HIDE)
    @Override
    @Transactional
    public void rejectPost(Integer id, Integer adminId, String adminName, String reason) {
        Post post = postDAO.findById(id).orElseThrow(() -> new RuntimeException("Không tìm thấy bài viết!"));

        // KIỂM TRA: Nếu bài đã được duyệt trước đó thì gọi là "HIDE" (GỠ BÀI), nếu chưa duyệt thì gọi là "REJECT" (TỪ CHỐI)
        String logAction = (post.getIsApproved() != null && post.getIsApproved() == 1) ? "HIDE" : "REJECT";

        post.setIsApproved(-1); // Đánh dấu từ chối/gỡ
        post.setIsActive(0);    // Ẩn luôn khỏi public

        // Lưu lý do để user đọc
        post.setRejectReason(reason);
        post.setRejectedAt(LocalDateTime.now());
        postDAO.save(post);

        // Ghi sổ Nam Tào với Action đã phân biệt rõ ràng ở trên
        moderationLogService.logAction(id, "POST", logAction, adminId, adminName, reason);

        notificationService.notifyPostRejected(id, reason);
    }

    @Override
    @Transactional
    public void deactivePost(Integer id) {
        Post post = postDAO.findById(id).orElseThrow(() -> new RuntimeException("Không tìm thấy bài viết!"));
        post.setIsActive(0);
        postDAO.save(post);
        notificationService.notifyPostDisabled(id);
    }

    @Override
    @Transactional
    public void deletePost(Integer id) {
        Post post = postDAO.findById(id).orElseThrow(() -> new RuntimeException("Không tìm thấy bài viết!"));
        post.setIsActive(0);
        post.setIsApproved(-1);
        postDAO.save(post);
    }

    @Override
    @Transactional
    public void banAuthorByPostId(Integer postId) {
        Post post = postDAO.findById(postId).orElseThrow(() -> new RuntimeException("Không tìm thấy bài viết!"));
        Account author = post.getAccount();
        if (author == null) throw new RuntimeException("Tác giả không tồn tại!");
        author.setIsActive(0);
        author.setToken(null);
        accountDAO.save(author);
        postDAO.deactivateAllPostsByAccountId(author.getAccountID());
    }
}