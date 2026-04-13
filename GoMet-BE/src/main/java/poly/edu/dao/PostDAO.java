package poly.edu.dao;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import poly.edu.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface PostDAO extends JpaRepository<Post, Integer> {

    List<Post> findByIsApproved(Integer isApproved);

    // 🔥 Cập nhật các hàm tìm kiếm cơ bản để check thêm account.isActive
    @Query("SELECT p FROM Post p WHERE p.isApproved = :isApproved AND p.isActive = :isActive AND p.account.isActive = 1")
    List<Post> findByIsApprovedAndIsActive(@Param("isApproved") Integer isApproved, @Param("isActive") Integer isActive);

    List<Post> findByAccount_AccountID(Integer accountID);

    List<Post> findByAccount_AccountIDAndIsApprovedAndIsActive(Integer accountID, Integer isApproved, Integer isActive);

    List<Post> findByCategory_CategoryID(Integer categoryID);

    @Query("SELECT p FROM Post p WHERE p.category.categoryID = :categoryID AND p.isApproved = :isApproved AND p.isActive = :isActive AND p.account.isActive = 1")
    List<Post> findByCategory_CategoryIDAndIsApprovedAndIsActive(@Param("categoryID") Integer categoryID, @Param("isApproved") Integer isApproved, @Param("isActive") Integer isActive);

    List<Post> findByAccount_AccountIDOrderByCreatedAtDesc(Integer accountId);

    @Query("SELECT p FROM Post p WHERE p.isApproved = 1 AND p.isActive = 1 AND p.account.isActive = 1 " +
            "AND (LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(p.ingredients) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<Post> searchByKeyword(@Param("keyword") String keyword);

    @Query("SELECT p FROM Post p WHERE p.isApproved = 1 AND p.isActive = 1 AND p.account.isActive = 1 " +
            "AND p.category.categoryID = :categoryID " +
            "AND (LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<Post> searchByKeywordAndCategory(@Param("keyword") String keyword, @Param("categoryID") Integer categoryID);

    @Query("SELECT p FROM Post p WHERE p.isApproved = 1 AND p.isActive = 1 AND p.account.isActive = 1 ORDER BY p.createdAt DESC")
    List<Post> findLatest();

    @Query("SELECT p FROM Post p WHERE p.isApproved = 1 AND p.isActive = 1 AND p.account.isActive = 1 ORDER BY p.views DESC")
    List<Post> findMostViewed();

    @Query("SELECT p FROM Post p WHERE p.isApproved = 1 AND p.isActive = 1 AND p.account.isActive = 1 " +
            "AND p.postID <> :excludeId AND p.category.categoryID = :categoryID")
    List<Post> findRelated(@Param("categoryID") Integer categoryID, @Param("excludeId") Integer excludeId);

    @Query("SELECT COUNT(p) FROM Post p WHERE p.account.accountID = :accountID AND p.isApproved = 1 AND p.isActive = 1")
    long countByAccountId(@Param("accountID") Integer accountID);

    @Modifying
    @Query("UPDATE Post p SET p.isActive = 0 WHERE p.account.accountID = :accountId")
    void deactivateAllPostsByAccountId(@Param("accountId") Integer accountId);

    // ==========================================
    // THÊM CÁC HÀM CHO ADMIN STATS CONTROLLER
    // ==========================================

    @Query("SELECT COUNT(p) FROM Post p WHERE p.isApproved = :isApproved AND p.isActive = :isActive AND p.account.isActive = 1")
    long countByIsApprovedAndIsActive(@Param("isApproved") Integer isApproved, @Param("isActive") Integer isActive);

    @Query("SELECT SUM(p.views) FROM Post p")
    Long getTotalViews();

    @Query("SELECT MONTH(p.createdAt), COUNT(p) FROM Post p WHERE YEAR(p.createdAt) = :year GROUP BY MONTH(p.createdAt)")
    List<Object[]> countPostsByMonth(@Param("year") int year);

    // Sửa thành @Query để check được account.isActive
    @Query("SELECT p FROM Post p WHERE p.isActive = :isActive AND p.isApproved = :isApproved AND p.account.isActive = 1 ORDER BY p.views DESC")
    List<Post> findTop5ByIsActiveAndIsApprovedOrderByViewsDesc(@Param("isActive") Integer isActive, @Param("isApproved") Integer isApproved);

    @Query(value = "SELECT c.categoryName, COUNT(p.PostID) " +
            "FROM Post p JOIN Category c ON p.CategoryID = c.categoryID " +
            "GROUP BY c.categoryName", nativeQuery = true)
    List<Object[]> countPostsByCategory();

    @Query(value = "SELECT MONTH(p.CreatedAt), SUM(p.LikeCount) " +
            "FROM Post p WHERE YEAR(p.CreatedAt) = :year " +
            "GROUP BY MONTH(p.CreatedAt)", nativeQuery = true)
    List<Object[]> sumLikesByMonth(@Param("year") int year);

    @Query("SELECT SUM(p.likeCount) FROM Post p")
    Long getTotalLikes();

    @Modifying
    @Query("UPDATE Post p SET p.category.categoryID = 1 WHERE p.category.categoryID = :oldCatId")
    int movePostsToDefaultCategory(@Param("oldCatId") Integer oldCatId);

}