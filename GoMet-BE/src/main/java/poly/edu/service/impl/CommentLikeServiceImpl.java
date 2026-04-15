package poly.edu.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import poly.edu.dao.CommentLikeDAO;
import poly.edu.dao.CommentDAO;
import poly.edu.dao.AccountDAO;
import poly.edu.entity.Account;
import poly.edu.entity.Comment;
import poly.edu.entity.CommentLike;
import poly.edu.service.CommentLikeService;
import poly.edu.service.NotificationService;

import java.util.Optional; // QUAN TRỌNG: Phải có import này

@Service
@RequiredArgsConstructor
public class CommentLikeServiceImpl implements CommentLikeService {

    private final CommentLikeDAO commentLikeDAO;
    private final CommentDAO commentDAO;
    private final AccountDAO accountDAO;
    private final NotificationService notificationService;

    @Override
    @Transactional
    public boolean toggleLike(Integer accountId, Integer commentId) {
        // 1. Tìm comment
        Comment comment = commentDAO.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bình luận"));

        // 2. Check xem đã Like chưa (Dùng đúng hàm đã khai báo ở CommentLikeDAO)
        Optional<CommentLike> existingLike = commentLikeDAO.findByAccountAndComment(accountId, commentId);

        if (existingLike.isPresent()) {
            // --- TRƯỜNG HỢP: HỦY LIKE ---
            commentLikeDAO.delete(existingLike.get());

            // Giảm Like (Check null để an toàn)
            int currentLikes = (comment.getLikes() != null) ? comment.getLikes() : 1;
            comment.setLikes(Math.max(0, currentLikes - 1));

            commentDAO.save(comment);
            return false;
        } else {
            // --- TRƯỜNG HỢP: THÊM LIKE ---
            Account account = accountDAO.findById(accountId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản"));

            // Dùng Builder sếp đã tạo trong Entity
            CommentLike newLike = CommentLike.builder()
                    .account(account)
                    .comment(comment)
                    .build();
            commentLikeDAO.save(newLike);

            // Tăng Like
            int currentLikes = (comment.getLikes() != null) ? comment.getLikes() : 0;
            comment.setLikes(currentLikes + 1);

            commentDAO.save(comment);

            if (comment.getAccount() != null && !account.getAccountID().equals(comment.getAccount().getAccountID())) {
                Integer postId = comment.getPost() != null ? comment.getPost().getPostID() : null;
                notificationService.notifyCommentLike(account.getUsername(), comment.getAccount().getAccountID(),
                        postId, commentId);
            }

            return true;
        }
    }
}