package poly.edu.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import poly.edu.entity.Comment;
import java.util.List;
import java.util.Optional;

public interface CommentDAO extends JpaRepository<Comment, Integer> {

    // Lấy toàn bộ bình luận của bài viết
    List<Comment> findByPost_PostID(Integer postID);

    // Thống kê số lượng từng loại sao (trả về list mảng Object [sao, số lượng])
    @Query("SELECT c.rating, COUNT(c) FROM Comment c WHERE c.post.postID = :postID AND c.rating IS NOT NULL GROUP BY c.rating")
    List<Object[]> countRatingByPostID(@Param("postID") Integer postID);
    // Thêm hàm này vào CommentDAO
    @Query("SELECT COUNT(c) FROM Comment c WHERE c.post.postID = :postId AND c.account.accountID = :accountId AND c.rating > 0 AND c.isActive = 1")
    long countRatingsByUserAndPost(@Param("postId") Integer postId, @Param("accountId") Integer accountId);
    // Đếm bình luận theo tháng
    @Query(value = "SELECT MONTH(c.CreatedAt), COUNT(c.CommentID) " +
            "FROM Comment c WHERE YEAR(c.CreatedAt) = :year " +
            "GROUP BY MONTH(c.CreatedAt)", nativeQuery = true)
    List<Object[]> countCommentsByMonth(@Param("year") int year);

    @Query(value = "WITH RankedComments AS (" +
            "  SELECT c.*, " +
            "         ROW_NUMBER() OVER(PARTITION BY c.PostID " +
            "                           ORDER BY (ISNULL(c.Likes, 0) * 2 + ISNULL(c.Rating, 0)) DESC, c.CommentID DESC) as rn " +
            "  FROM Comment c " +
            "  WHERE c.cmtid IS NULL AND c.Content IS NOT NULL AND c.Content <> '' " +
            "    AND c.PostID IN (:postIds) " +
            "    AND (c.IsActive IS NULL OR c.IsActive = 1) " +
            ") " +
            "SELECT * FROM RankedComments WHERE rn = 1",
            nativeQuery = true)
    List<Comment> findTopCommentsByPostIds(@Param("postIds") List<Integer> postIds);
}