package poly.edu.dao;

import poly.edu.entity.Appeal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AppealDAO extends JpaRepository<Appeal, Integer> {

    Optional<Appeal> findByEmail(String email);

    List<Appeal> findByStatus(String status);

    @Query("SELECT a FROM Appeal a WHERE a.email LIKE %:email% ORDER BY a.createdAt DESC")
    List<Appeal> searchByEmail(@Param("email") String email);

    @Query("SELECT a FROM Appeal a ORDER BY a.createdAt DESC")
    List<Appeal> findAllOrderByCreatedDesc();

    // 🔥 ĐÃ FIX: Sửa câu query JPQL chuẩn Spring Data và nhận 2 tham số
    @Query("SELECT COUNT(a) FROM Appeal a WHERE a.email = :email AND a.createdAt > :yesterday")
    long countRecentAppeals(@Param("email") String email, @Param("yesterday") LocalDateTime yesterday);

    // Đếm Khiếu nại theo Trạng thái (Dùng cho Biểu đồ Appeals Chart)
    @Query("SELECT a.status, COUNT(a) FROM Appeal a GROUP BY a.status")
    List<Object[]> countAppealsByStatus();
}