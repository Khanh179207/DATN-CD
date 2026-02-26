package poly.edu.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import poly.edu.entity.SearchHistory;
import java.util.List;
import java.util.Optional;

public interface SearchHistoryDAO extends JpaRepository<SearchHistory, Integer> {

    // Lấy 5 từ khóa gần nhất (thay vì 10)
    List<SearchHistory> findTop5ByAccount_AccountIDOrderBySearchedAtDesc(Integer accountId);

    // Tìm từ khóa cụ thể của một tài khoản
    Optional<SearchHistory> findByAccount_AccountIDAndKeyword(Integer accountId, String keyword);

    // Xóa toàn bộ lịch sử của một tài khoản
    void deleteByAccount_AccountID(Integer accountId);

    // Đếm số lượng bản ghi của một tài khoản
    long countByAccount_AccountID(Integer accountId);

    // Xóa bản ghi cũ nhất của một tài khoản (dùng để duy trì giới hạn 5)
    @Modifying
    @Query("DELETE FROM SearchHistory s WHERE s.searchId = " +
            "(SELECT s2.searchId FROM SearchHistory s2 WHERE s2.account.accountID = :accountId " +
            "ORDER BY s2.searchedAt ASC LIMIT 1)")
    void deleteOldestByAccountId(@Param("accountId") Integer accountId);
}