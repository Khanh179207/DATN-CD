package poly.edu.dao;

import poly.edu.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AccountDAO extends JpaRepository<Account, Integer> {

    Optional<Account> findByUsername(String username);

    Optional<Account> findByUsernameIgnoreCase(String username);

    Optional<Account> findByEmail(String email);

    Optional<Account> findByToken(String token);

    List<Account> findByIsAdmin(Integer isAdmin);

    List<Account> findByUsernameContainingIgnoreCase(String keyword);

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

}