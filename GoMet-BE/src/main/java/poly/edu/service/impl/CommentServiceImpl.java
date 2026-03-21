package poly.edu.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import poly.edu.dao.AccountDAO;
import poly.edu.dao.CommentDAO;
import poly.edu.dao.PostDAO;
import poly.edu.dao.CommentLikeDAO;
import poly.edu.dto.AdminCommentDTO;
import poly.edu.dto.CommentDTO;
import poly.edu.entity.Account;
import poly.edu.entity.Comment;
import poly.edu.entity.Post;
import poly.edu.service.CommentService;
import poly.edu.service.NotificationService;

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

    @Override
    public List<CommentDTO> getCommentsByPost(Integer postID, Integer currentAccountID) {
        return commentDAO.findByPost_PostID(postID).stream()
                .map(c -> {
                    CommentDTO dto = toDTO(c); // Gọi mapper cũ của sếp

                    // --- ĐÂY LÀ CHỖ FIX ---
                    if (currentAccountID != null) {
                        // Check xem user này đã like comment này chưa
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
        // 1. Validation cơ bản
        boolean hasContent = req.getContent() != null && !req.getContent().isBlank();
        boolean hasImages = req.getImageUrls() != null && !req.getImageUrls().isEmpty();
        boolean hasRating = req.getRating() != null && req.getRating() > 0;

        if (req.getAccountID() == null || (!hasContent && !hasImages && !hasRating)) {
            throw new RuntimeException("Vui lòng nhập nội dung, hình ảnh hoặc đánh giá sao");
        }

        // 2. Tìm Account
        Account account = accountDAO.findById(req.getAccountID())
                .orElseThrow(() -> new RuntimeException("Tài khoản không tồn tại"));

        // 3. Xử lý Reply và Rating logic
        Comment parentComment = null;
        Integer finalRating = req.getRating();
        if (req.getCmtid() != null) {
            parentComment = commentDAO.findById(req.getCmtid()).orElse(null);
            finalRating = null; // Reply thì không có sao
        }

        // 4. Xác định Post
        Post post = null;
        if (req.getPostID() != null) {
            post = postDAO.findById(req.getPostID()).orElse(null);
        } else if (parentComment != null) {
            post = parentComment.getPost();
        }

        if (post == null) {
            throw new RuntimeException("Không tìm thấy bài viết");
        }

        // 5. Build và Save
        Comment comment = Comment.builder()
                .post(post)
                .parentComment(parentComment)
                .account(account)
                .content(req.getContent() != null ? req.getContent() : "")
                .rating(finalRating)
                .attachments(req.getImageUrls())
                .likes(0) // Mặc định 0 like
                .build();

        Comment saved = commentDAO.save(comment);

        // 6. Gửi thông báo cho chủ bài viết
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

    @Override
    public void delete(Integer id) {
        if (!commentDAO.existsById(id)) throw new RuntimeException("Bình luận không tồn tại");
        commentDAO.deleteById(id);
    }

    @Override
    public List<AdminCommentDTO> findAll() {
        return commentDAO.findAll().stream().map(c -> {
            AdminCommentDTO dto = new AdminCommentDTO();
            dto.setCommentID(c.getCommentID());
            dto.setContent(c.getContent());
            if (c.getAccount() != null) dto.setUsername(c.getAccount().getUsername());
            if (c.getPost() != null) dto.setPostTitle(c.getPost().getTitle());
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> getRatingStats(Integer postID) {
        List<Comment> ratedComments = commentDAO.findByPost_PostID(postID).stream()
                .filter(c -> c.getRating() != null && c.getRating() > 0)
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

    // Helper Mapper
    private CommentDTO toDTO(Comment c) {
        CommentDTO dto = new CommentDTO();
        dto.setCommentID(c.getCommentID());
        dto.setPostID(c.getPost() != null ? c.getPost().getPostID() : null);
        dto.setCmtid(c.getParentComment() != null ? c.getParentComment().getCommentID() : null);
        dto.setContent(c.getContent());
        dto.setRating(c.getRating() != null ? c.getRating() : 0);

        if (c.getAccount() != null) {
            dto.setAccountID(c.getAccount().getAccountID());
            dto.setAuthorName(c.getAccount().getUsername());
            dto.setAuthorAvatar(c.getAccount().getAvatar());
        }

        dto.setImageUrls(c.getAttachments());
        dto.setLikes(c.getLikes() != null ? c.getLikes() : 0);
        dto.setCreatedAt(c.getCreatedAt());
        return dto;
    }
}