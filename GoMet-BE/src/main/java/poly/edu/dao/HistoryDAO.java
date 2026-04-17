package poly.edu.dao;

import poly.edu.entity.History;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface HistoryDAO extends JpaRepository<History, Integer> {

    /** Recent history for a user, newest first. */
    List<History> findByAccount_AccountIDOrderByLastViewedAtDesc(Integer accountID);

    /** Check if records already exist for account+post (Returns List to avoid NonUniqueResultException) */
    List<History> findByAccount_AccountIDAndPost_PostID(Integer accountID, Integer postID);

    /** Clear all history for an account. */
    @Modifying
    @Transactional
    @Query("DELETE FROM History h WHERE h.account.accountID = :accountID")
    void deleteAllByAccountId(@Param("accountID") Integer accountID);

    /** Count total records in a user's history. */
    long countByAccount_AccountID(Integer accountID);

    /** Count distinct posts viewed today */
    @Query("SELECT COUNT(DISTINCT h.post.postID) FROM History h WHERE h.account.accountID = :accountID AND h.lastViewedAt >= :startOfDay AND h.lastViewedAt <= :endOfDay")
    long countDistinctPostsViewedToday(@Param("accountID") Integer accountID, @Param("startOfDay") java.time.LocalDateTime startOfDay, @Param("endOfDay") java.time.LocalDateTime endOfDay);

    /** Check if a post was already viewed today */
    @Query("SELECT COUNT(h) > 0 FROM History h WHERE h.account.accountID = :accountID AND h.post.postID = :postID AND h.lastViewedAt >= :startOfDay AND h.lastViewedAt <= :endOfDay")
    boolean hasViewedPostToday(@Param("accountID") Integer accountID, @Param("postID") Integer postID, @Param("startOfDay") java.time.LocalDateTime startOfDay, @Param("endOfDay") java.time.LocalDateTime endOfDay);

    /** Delete history for today (Demo purpose) */
    @Modifying
    @Transactional
    @Query("DELETE FROM History h WHERE h.account.accountID = :accountID AND h.lastViewedAt >= :startOfDay AND h.lastViewedAt <= :endOfDay")
    void deleteTodayHistory(@Param("accountID") Integer accountID, @Param("startOfDay") java.time.LocalDateTime startOfDay, @Param("endOfDay") java.time.LocalDateTime endOfDay);
}

