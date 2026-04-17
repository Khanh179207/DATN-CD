package poly.edu.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import poly.edu.entity.Comment;
import poly.edu.dto.CommentHistoryDTO; // 🔥 Import DTO mới
import java.util.List;

public interface CommentDAO extends JpaRepository<Comment, Integer> {

    // 🔥 CẬP NHẬT: Thêm c.attachments vào constructor
    @Query("SELECT new poly.edu.dto.CommentHistoryDTO(c.commentID, c.content, c.createdAt, c.post.title, c.post.postID, c.attachments) " +
            "FROM Comment c WHERE c.account.accountID = :accountId AND c.isActive = 1 " +
            "ORDER BY c.createdAt DESC")
    List<CommentHistoryDTO> findHistoryByAccountId(@Param("accountId") Integer accountId);

    // Lấy toàn bộ bình luận của bài viết
    List<Comment> findByPost_PostID(Integer postID);

    // Thống kê số lượng từng loại sao
    @Query("SELECT c.rating, COUNT(c) FROM Comment c WHERE c.post.postID = :postID AND c.rating IS NOT NULL GROUP BY c.rating")
    List<Object[]> countRatingByPostID(@Param("postID") Integer postID);

    // Đếm số lượt đánh giá
    @Query("SELECT COUNT(c) FROM Comment c WHERE c.post.postID = :postId AND c.account.accountID = :accountId AND c.rating > 0 AND c.isActive = 1")
    long countRatingsByUserAndPost(@Param("postId") Integer postId, @Param("accountId") Integer accountId);

    // Đếm bình luận theo tháng
    @Query("SELECT MONTH(c.createdAt), COUNT(c) FROM Comment c WHERE YEAR(c.createdAt) = :year GROUP BY MONTH(c.createdAt)")
    List<Object[]> countCommentsByMonth(@Param("year") int year);

    // Lấy comment nổi bật (Native Query)
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