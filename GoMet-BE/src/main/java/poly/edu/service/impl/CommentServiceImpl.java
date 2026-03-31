package poly.edu.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import poly.edu.dao.*;
import poly.edu.dto.AdminCommentDTO;
import poly.edu.dto.CommentDTO;
import poly.edu.entity.Account;
import poly.edu.entity.Comment;
import poly.edu.entity.InteractionLog;
import poly.edu.entity.Post;
import poly.edu.service.CommentService;
import poly.edu.service.ModerationLogService; // 🔥 IMPORT MỚI
import poly.edu.service.NotificationService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentDAO commentDAO;
    private final PostDAO postDAO;
    private final AccountDAO accountDAO;
    private final CommentLikeDAO commentLikeDAO;
    private final NotificationService notificationService;
    private final InteractionLogDAO interactionLogDAO;

    // 🔥 INJECT THÊM MÁY NGHE LÉN
    private final ModerationLogService moderationLogService;

    @Override
    public List<CommentDTO> getCommentsByPost(Integer postID, Integer currentAccountID) {
        return commentDAO.findByPost_PostID(postID).stream()
                .map(c -> {
                    CommentDTO dto = toDTO(c);

                    // Chỉ check like cho những comment đang hoạt động (isActive = 1)
                    if (currentAccountID != null && Integer.valueOf(1).equals(c.getIsActive())) {
                        boolean liked = commentLikeDAO.findByAccountAndComment(currentAccountID, c.getCommentID()).isPresent();
                        dto.setIsLiked(liked);
                    } else {
                        dto.setIsLiked(false);
                    }

                    return dto;
                })
                .sorted(Comparator.comparing(CommentDTO::getCreatedAt).reversed())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CommentDTO saveNewComment(CommentDTO req) {
        boolean hasContent = req.getContent() != null && !req.getContent().isBlank();
        boolean hasImages = req.getImageUrls() != null && !req.getImageUrls().isEmpty();
        boolean hasRating = req.getRating() != null && req.getRating() > 0;

        if (req.getAccountID() == null || (!hasContent && !hasImages && !hasRating)) {
            throw new RuntimeException("Vui lòng nhập nội dung, hình ảnh hoặc đánh giá sao");
        }

        Account account = accountDAO.findById(req.getAccountID())
                .orElseThrow(() -> new RuntimeException("Tài khoản không tồn tại"));

        Comment parentComment = null;
        Integer finalRating = req.getRating();
        if (req.getCmtid() != null) {
            parentComment = commentDAO.findById(req.getCmtid()).orElse(null);
            finalRating = null;
        }

        Post post = null;
        if (req.getPostID() != null) {
            post = postDAO.findById(req.getPostID()).orElse(null);
        } else if (parentComment != null) {
            post = parentComment.getPost();
        }

        if (post == null) throw new RuntimeException("Không tìm thấy bài viết");

        // 🔥 CHỐT CHẶN BẢO MẬT: CHỐNG SPAM RATING (Mỗi bài chỉ rate 1 lần) 🔥
        if (finalRating != null && finalRating > 0) {
            // Đếm xem user này đã từng rate bài này (với isActive = 1) chưa
            long existingRatings = commentDAO.countRatingsByUserAndPost(post.getPostID(), account.getAccountID());
            if (existingRatings > 0) {
                throw new RuntimeException("Bạn đã đánh giá bài viết này rồi! Mỗi bài viết chỉ được đánh giá 1 lần.");
            }
        }

        Comment comment = Comment.builder()
                .post(post)
                .parentComment(parentComment)
                .account(account)
                .content(req.getContent() != null ? req.getContent() : "")
                .rating(finalRating)
                .attachments(req.getImageUrls())
                .likes(0)
                .isActive(1) // Mặc định 1: Đang hoạt động
                .build();

        Comment saved = commentDAO.save(comment);

        try {
            if (finalRating != null && finalRating > 0) {
                InteractionLog log = new InteractionLog();
                log.setPostID(post.getPostID());
                log.setType("RATING"); // Khớp với SQL 'RATE' của sếp
                log.setValue(finalRating);
                log.setCreatedAt(LocalDateTime.now());

                // 🔥 Gắn CommentID vừa sinh ra vào ReferenceID
                log.setReferenceId(saved.getCommentID());

                interactionLogDAO.save(log);
            }
        } catch (Exception e) {
            // Bọc trong try-catch để lỡ log có lỗi thì user vẫn comment được
            System.err.println("Lỗi lưu InteractionLog: " + e.getMessage());
        }

        if (post.getAccount() != null && !account.getAccountID().equals(post.getAccount().getAccountID())) {
            notificationService.notifyComment(
                    account.getUsername(),
                    post.getAccount().getAccountID(),
                    post.getPostID(),
                    saved.getCommentID()
            );
        }

        return toDTO(saved);
    }

    // 🔥 ADMIN XÓA BÌNH LUẬN (-1) VÀ GHI LOG
    @Override
    @Transactional
    public void delete(Integer id, Integer adminId, String adminName) {
        // 1. Tìm comment để kiểm tra tồn tại
        Comment comment = commentDAO.findById(id)
                .orElseThrow(() -> new RuntimeException("Bình luận không tồn tại"));

        interactionLogDAO.deleteByReference(id, "RATING");

        comment.setIsActive(-1);
        commentDAO.save(comment);

        // 3. Tự động lấy nội dung làm lý do (Giữ nguyên logic của sếp)
        String content = comment.getContent() != null ? comment.getContent() : "[Chỉ có hình ảnh/Đánh giá]";
        if (content.length() > 50) content = content.substring(0, 50) + "...";
        String autoReason = "Xóa bình luận vi phạm: '" + content + "'";

        // 4. Ghi vào Moderation Log (Giữ nguyên logic của sếp)
        moderationLogService.logAction(id, "COMMENT", "DELETE", adminId, adminName, autoReason);
    }

    @Override
    @Transactional
    public void deleteByUser(Integer id, Integer userId) {
        Comment comment = commentDAO.findById(id).orElseThrow(() -> new RuntimeException("Lỗi"));
        if (!comment.getAccount().getAccountID().equals(userId)) {
            throw new RuntimeException("Bạn không có quyền!");
        }
        interactionLogDAO.deleteByReference(id, "RATING");
        comment.setIsActive(0);
        commentDAO.save(comment);
    }

    // 🔥 ADMIN KHÔI PHỤC BÌNH LUẬN (1) VÀ GHI LOG
    @Override
    @Transactional
    public void restore(Integer id, Integer adminId, String adminName) {
        Comment comment = commentDAO.findById(id)
                .orElseThrow(() -> new RuntimeException("Bình luận không tồn tại"));

        comment.setIsActive(1);
        commentDAO.save(comment);

        // Ghi thẳng vào Sổ Nam Tào
        moderationLogService.logAction(id, "COMMENT", "RESTORE", adminId, adminName, "Khôi phục bình luận bị xóa nhầm");
    }

    @Override
    public List<AdminCommentDTO> findAll() {
        return commentDAO.findAll().stream()
                .filter(c -> (c.getContent() != null && !c.getContent().trim().isEmpty())
                        || (c.getAttachments() != null && !c.getAttachments().isEmpty()))
                .sorted(Comparator.comparing(Comment::getCreatedAt, Comparator.nullsLast(Comparator.reverseOrder())))
                .map(c -> {
                    AdminCommentDTO dto = new AdminCommentDTO();
                    dto.setCommentID(c.getCommentID());
                    dto.setContent(c.getContent());
                    dto.setCreatedAt(c.getCreatedAt());
                    dto.setImageUrls(c.getAttachments());

                    // Trả về đúng trạng thái (1, 0, -1)
                    dto.setIsActive(c.getIsActive() != null ? c.getIsActive() : 1);

                    dto.setRating(c.getRating() != null ? c.getRating() : 0);
                    dto.setLikes(c.getLikes() != null ? c.getLikes() : 0);
                    dto.setHasAttachments(c.getAttachments() != null && !c.getAttachments().isEmpty());
                    dto.setParentCommentID(c.getParentComment() != null ? c.getParentComment().getCommentID() : null);

                    if (c.getAccount() != null) {
                        dto.setAuthorID(c.getAccount().getAccountID());
                        dto.setAuthorName(c.getAccount().getUsername());
                        dto.setAuthorAvatar(c.getAccount().getAvatar());
                    } else {
                        dto.setAuthorName("Người dùng ẩn danh");
                    }

                    if (c.getPost() != null) {
                        dto.setPostID(c.getPost().getPostID());
                        dto.setPostTitle(c.getPost().getTitle());
                    }

                    return dto;
                }).collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> getRatingStats(Integer postID) {
        List<Comment> ratedComments = commentDAO.findByPost_PostID(postID).stream()
                .filter(c -> c.getRating() != null && c.getRating() > 0 && Integer.valueOf(1).equals(c.getIsActive()))
                .collect(Collectors.toList());

        long total = ratedComments.size();
        double avg = ratedComments.stream().mapToInt(Comment::getRating).average().orElse(0.0);
        long[] distribution = new long[5];

        for (Comment c : ratedComments) {
            int star = c.getRating();
            if (star >= 1 && star <= 5) distribution[star - 1]++;
        }

        Map<String, Object> stats = new HashMap<>();
        stats.put("avgRating", Math.round(avg * 10.0) / 10.0);
        stats.put("totalRatings", total);
        stats.put("distribution", distribution);
        return stats;
    }

    private CommentDTO toDTO(Comment c) {
        CommentDTO dto = new CommentDTO();
        dto.setCommentID(c.getCommentID());
        dto.setPostID(c.getPost() != null ? c.getPost().getPostID() : null);
        dto.setCmtid(c.getParentComment() != null ? c.getParentComment().getCommentID() : null);

        Integer activeStatus = c.getIsActive() != null ? c.getIsActive() : 1;

        if (activeStatus == -1) {
            dto.setContent("[Bình luận này đã bị ẩn bởi Quản trị viên do vi phạm]");
            dto.setAuthorName("Hệ thống GoMet");
            dto.setAuthorAvatar(null);
            dto.setImageUrls(new ArrayList<>());
            dto.setLikes(0);
            dto.setRating(0);
        } else if (activeStatus == 0) {
            dto.setContent("[Người dùng đã xóa bình luận này]");
            dto.setAuthorName("Ẩn danh");
            dto.setAuthorAvatar(null);
            dto.setImageUrls(new ArrayList<>());
            dto.setLikes(0);
            dto.setRating(0);
        } else {
            dto.setContent(c.getContent());
            dto.setRating(c.getRating() != null ? c.getRating() : 0);
            if (c.getAccount() != null) {
                dto.setAccountID(c.getAccount().getAccountID());
                dto.setAuthorName(c.getAccount().getUsername());
                dto.setAuthorAvatar(c.getAccount().getAvatar());
            }
            dto.setImageUrls(c.getAttachments());
            dto.setLikes(c.getLikes() != null ? c.getLikes() : 0);
        }

        dto.setCreatedAt(c.getCreatedAt());
        return dto;
    }
}