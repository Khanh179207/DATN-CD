package poly.edu.dao;

import org.springframework.data.domain.Pageable;
import poly.edu.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface AccountDAO extends JpaRepository<Account, Integer> {

    Optional<Account> findByUsername(String username);

    Optional<Account> findByUsernameIgnoreCase(String username);

    Optional<Account> findByEmail(String email);

    List<Account> findByIsAdmin(Integer isAdmin);

    List<Account> findByUsernameContainingIgnoreCase(String keyword);

    List<Account> findByIsActive(Integer isActive);

    List<Account> findByUsernameContainingIgnoreCaseAndIsActive(String keyword, Integer isActive);

    // ==========================================
    // THÊM CÁC HÀM CHO ADMIN STATS CONTROLLER
    // ==========================================
// Đếm user VIP và Banned
    long countByIsPremium(Integer isPremium);
    long countByIsActive(Integer isActive);

    // Chart: Đếm user đăng ký theo tháng
    @Query("SELECT MONTH(a.createdAt), COUNT(a) FROM Account a WHERE YEAR(a.createdAt) = :year GROUP BY MONTH(a.createdAt)")
    List<Object[]> countUsersByMonth(@Param("year") int year);

    // Top Ranking: Top 5 Đầu bếp chăm chỉ nhất (Dùng TOP 5 cho SQL Server)
    @Query(value = "SELECT TOP 5 a.username, COUNT(p.postID) as totalPosts, a.avatar " +
            "FROM Account a JOIN Post p ON a.accountID = p.accountID " +
            "WHERE p.isActive = 1 AND p.isApproved = 1 " +
            "GROUP BY a.accountID, a.username, a.avatar " +
            "ORDER BY totalPosts DESC", nativeQuery = true)
    List<Object[]> findTopCreators();

    // Đếm số lượng tài khoản bị Khóa/Ban (Giả sử isActive = 0 hoặc -1 là bị khóa)
    @Query("SELECT COUNT(a) FROM Account a WHERE a.isActive = 0") // Tùy sếp quy định số 0 hay -1 nhé
    long countBannedAccounts();

    @Query(value = """
        SELECT 
            a.AccountID as id, 
            a.Username as name, 
            a.Avatar as avatar,
            ISNULL(a.isPremium, 0) as isPremium,
            ISNULL(p.postCount, 0) as postCount,
            ISNULL(f.followerCount, 0) as followers,
            
            -- CÔNG THỨC TÍNH ĐIỂM
            (ISNULL(f.followerCount, 0) * 100 + ISNULL(p.postCount, 0)) as totalPts
            
        FROM Account a
        
        -- Đếm bài viết
        LEFT JOIN (
            SELECT AccountID, COUNT(PostID) as postCount 
            FROM Post 
            WHERE CreatedAt >= :startDate 
              AND isActive = 1 AND isApproved = 1 
            GROUP BY AccountID
        ) p ON a.AccountID = p.AccountID
        
        -- Đếm lượt theo dõi
        LEFT JOIN (
            SELECT FolloweeID, COUNT(FollowID) as followerCount 
            FROM Follow 
            WHERE Status = 1 AND FollowedAt >= :startDate 
            GROUP BY FolloweeID
        ) f ON a.AccountID = f.FolloweeID
        
        WHERE a.isActive = 1 
          AND (ISNULL(f.followerCount, 0) * 100 + ISNULL(p.postCount, 0)) > 0
          
        ORDER BY totalPts DESC
        """, nativeQuery = true)
    List<Map<String, Object>> findTopChefs(
            @Param("startDate") LocalDateTime startDate,
            Pageable pageable // 🔥 Dùng Pageable thay cho query.setMaxResults(limit)
    );

}