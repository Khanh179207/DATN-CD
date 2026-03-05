package poly.edu.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import poly.edu.entity.Post;
import poly.edu.entity.PostStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface PostDAO extends JpaRepository<Post, Integer> {

    // ─── Legacy (backward-compat) ─────────────────────────────────────────────

    List<Post> findByIsApproved(Integer isApproved);

    List<Post> findByIsApprovedAndIsActive(Integer isApproved, Integer isActive);

    List<Post> findByAccount_AccountID(Integer accountID);

    List<Post> findByAccount_AccountIDAndIsApprovedAndIsActive(Integer accountID, Integer isApproved, Integer isActive);

    List<Post> findByCategory_CategoryID(Integer categoryID);

    List<Post> findByCategory_CategoryIDAndIsApprovedAndIsActive(Integer categoryID, Integer isApproved, Integer isActive);

    // ─── Public feed (APPROVED only) ──────────────────────────────────────────

    @Query("SELECT p FROM Post p WHERE p.status = 'APPROVED' " +
           "AND (LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(p.ingredients) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<Post> searchByKeyword(@Param("keyword") String keyword);

    @Query("SELECT p FROM Post p WHERE p.status = 'APPROVED' " +
           "AND p.category.categoryID = :categoryID " +
           "AND (LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<Post> searchByKeywordAndCategory(@Param("keyword") String keyword, @Param("categoryID") Integer categoryID);

    @Query("SELECT p FROM Post p WHERE p.status = 'APPROVED' ORDER BY p.createdAt DESC")
    List<Post> findLatest();

    @Query("SELECT p FROM Post p WHERE p.status = 'APPROVED' ORDER BY p.views DESC")
    List<Post> findMostViewed();

    @Query("SELECT p FROM Post p WHERE p.status = 'APPROVED' " +
           "AND p.postID <> :excludeId AND p.category.categoryID = :categoryID")
    List<Post> findRelated(@Param("categoryID") Integer categoryID, @Param("excludeId") Integer excludeId);

    @Query("SELECT COUNT(p) FROM Post p WHERE p.account.accountID = :accountID AND p.status = 'APPROVED'")
    long countByAccountId(@Param("accountID") Integer accountID);

    // ─── Status-based queries ─────────────────────────────────────────────────

    List<Post> findByStatus(PostStatus status);

    Page<Post> findByStatus(PostStatus status, Pageable pageable);

    /** Admin inbox: pending with optional keyword search, newest first. */
    @Query("SELECT p FROM Post p WHERE p.status = :status " +
           "AND (:keyword IS NULL OR :keyword = '' " +
           "    OR LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "    OR LOWER(p.account.username) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "ORDER BY p.createdAt DESC")
    Page<Post> findByStatusAndKeyword(@Param("status") PostStatus status,
                                       @Param("keyword") String keyword,
                                       Pageable pageable);

    /** Owner's own posts across all statuses. */
    List<Post> findByAccount_AccountIDOrderByCreatedAtDesc(Integer accountID);

    /** Owner's own posts filtered by status. */
    List<Post> findByAccount_AccountIDAndStatusOrderByCreatedAtDesc(Integer accountID, PostStatus status);

    /** Duplicate detection: same author, same title+content hash, within window. */
    @Query("SELECT p FROM Post p WHERE p.account.accountID = :accountId " +
           "AND p.title = :title AND p.createdAt >= :since")
    List<Post> findDuplicateCandidates(@Param("accountId") Integer accountId,
                                        @Param("title") String title,
                                        @Param("since") LocalDate since);

    /** Count how many approved posts an account has (used for trusted-user check). */
    @Query("SELECT COUNT(p) FROM Post p WHERE p.account.accountID = :accountId AND p.status = 'APPROVED'")
    long countApprovedByAccount(@Param("accountId") Integer accountId);
}

