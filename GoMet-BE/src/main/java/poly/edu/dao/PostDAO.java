package poly.edu.dao;

import poly.edu.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostDAO extends JpaRepository<Post, Integer> {

    List<Post> findByIsApproved(Integer isApproved);

    List<Post> findByIsApprovedAndIsActive(Integer isApproved, Integer isActive);

    List<Post> findByAccount_AccountID(Integer accountID);

    List<Post> findByAccount_AccountIDAndIsApprovedAndIsActive(Integer accountID, Integer isApproved, Integer isActive);

    List<Post> findByCategory_CategoryID(Integer categoryID);

    List<Post> findByCategory_CategoryIDAndIsApprovedAndIsActive(Integer categoryID, Integer isApproved, Integer isActive);

    @Query("SELECT p FROM Post p WHERE p.isApproved = 1 AND p.isActive = 1 " +
           "AND (LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(p.ingredients) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<Post> searchByKeyword(@Param("keyword") String keyword);

    @Query("SELECT p FROM Post p WHERE p.isApproved = 1 AND p.isActive = 1 " +
           "AND p.category.categoryID = :categoryID " +
           "AND (LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<Post> searchByKeywordAndCategory(@Param("keyword") String keyword, @Param("categoryID") Integer categoryID);

    @Query("SELECT p FROM Post p WHERE p.isApproved = 1 AND p.isActive = 1 ORDER BY p.createdAt DESC")
    List<Post> findLatest();

    @Query("SELECT p FROM Post p WHERE p.isApproved = 1 AND p.isActive = 1 ORDER BY p.views DESC")
    List<Post> findMostViewed();

    @Query("SELECT p FROM Post p WHERE p.isApproved = 1 AND p.isActive = 1 " +
           "AND p.postID <> :excludeId AND p.category.categoryID = :categoryID")
    List<Post> findRelated(@Param("categoryID") Integer categoryID, @Param("excludeId") Integer excludeId);

    @Query("SELECT COUNT(p) FROM Post p WHERE p.account.accountID = :accountID AND p.isApproved = 1 AND p.isActive = 1")
    long countByAccountId(@Param("accountID") Integer accountID);
}
