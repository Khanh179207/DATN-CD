package poly.edu.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import poly.edu.dao.*;
import poly.edu.dto.LikesDTO;
import poly.edu.entity.*;
import poly.edu.service.LikesService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LikesServiceImpl implements LikesService {

    private final LikesDAO likesDAO;
    private final PostDAO postDAO;
    private final AccountDAO accountDAO;
    private final NotificationDAO notificationDAO; // Đảm bảo sếp đã có DAO này nhé

    @Override
    @Transactional
    public boolean toggleLike(Integer accountId, Integer postId) {
        Optional<Likes> existingLike = likesDAO.findByAccount_AccountIDAndPost_PostID(accountId, postId);

        if (existingLike.isPresent()) {
            // Đã like -> Xóa (Hủy like). SQL Trigger sẽ tự động trừ LikeCount của Post.
            likesDAO.delete(existingLike.get());
            return false;
        } else {
            // Chưa like -> Thêm mới. SQL Trigger sẽ tự động cộng LikeCount của Post.
            Likes newLike = new Likes();
            newLike.setAccount(accountDAO.findById(accountId).orElseThrow(() -> new RuntimeException("Lỗi: Không tìm thấy Account")));
            newLike.setPost(postDAO.findById(postId).orElseThrow(() -> new RuntimeException("Lỗi: Không tìm thấy Post")));
            likesDAO.save(newLike);

            // Gửi thông báo cho chủ bài viết
//            createNotification(accountId, postId);
            return true;
        }
    }

    @Override
    public List<LikesDTO> getLikesByPostId(Integer postId) {
        return likesDAO.findByPost_PostIDOrderByCreatedAtDesc(postId)
                .stream()
                .map(like -> new LikesDTO(
                        like.getLikeID(),
                        like.getAccount().getAccountID(),
                        like.getAccount().getUsername(),
                        like.getAccount().getAvatar(),
                        like.getPost().getPostID(),
                        like.getCreatedAt()
                ))
                .collect(Collectors.toList());
    }

    @Override
    public boolean checkUserLikedPost(Integer accountId, Integer postId) {
        return likesDAO.existsByAccount_AccountIDAndPost_PostID(accountId, postId);
    }

    // Hàm phụ trợ tạo thông báo
    private void createNotification(Integer likerId, Integer postId) {
        Post post = postDAO.findById(postId).orElseThrow();
        Account liker = accountDAO.findById(likerId).orElseThrow();
        Account author = post.getAccount();

        // Không gửi thông báo nếu mình tự like bài của mình
        if (!likerId.equals(author.getAccountID())) {
            Notification notice = new Notification();
            notice.setTitle("Lượt tương tác mới");
            notice.setContent(liker.getUsername() + " đã thích bài viết: " + post.getTitle());
            notice.setType("LIKE");
            notice.setAccount(author);
            notice.setPost(post);
            notice.setIsRead(0);
            // Sếp nhớ đảm bảo Entity Notification có đủ các trường này
            notificationDAO.save(notice);
        }
    }
}