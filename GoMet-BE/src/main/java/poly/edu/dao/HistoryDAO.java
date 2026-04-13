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

    /** Check if a record already exists for account+post. */
    Optional<History> findByAccount_AccountIDAndPost_PostID(Integer accountID, Integer postID);

    /** Clear all history for an account. */
    @Modifying
    @Transactional
    @Query("DELETE FROM History h WHERE h.account.accountID = :accountID")
    void deleteAllByAccountId(@Param("accountID") Integer accountID);

    /** Count total records in a user's history. */
    long countByAccount_AccountID(Integer accountID);
}

