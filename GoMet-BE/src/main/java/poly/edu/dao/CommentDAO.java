package poly.edu.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import poly.edu.entity.Comment;
import java.util.List;

public interface CommentDAO extends JpaRepository<Comment, Integer> {

    // Lấy toàn bộ bình luận của bài viết
    List<Comment> findByPost_PostID(Integer postID);

    // Thống kê số lượng từng loại sao (trả về list mảng Object [sao, số lượng])
    @Query("SELECT c.rating, COUNT(c) FROM Comment c WHERE c.post.postID = :postID AND c.rating IS NOT NULL GROUP BY c.rating")
    List<Object[]> countRatingByPostID(@Param("postID") Integer postID);
    // Thêm hàm này vào CommentDAO
    @Query("SELECT COUNT(c) FROM Comment c WHERE c.post.postID = :postId AND c.account.accountID = :accountId AND c.rating > 0 AND c.isActive = 1")
    long countRatingsByUserAndPost(@Param("postId") Integer postId, @Param("accountId") Integer accountId);
}