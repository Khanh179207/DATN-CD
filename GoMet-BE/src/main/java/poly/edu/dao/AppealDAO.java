package poly.edu.dao;

import poly.edu.entity.Appeal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AppealDAO extends JpaRepository<Appeal, Integer> {

    Optional<Appeal> findByEmail(String email);

    List<Appeal> findByStatus(String status);

    @Query("SELECT a FROM Appeal a WHERE a.email LIKE %:email% ORDER BY a.createdAt DESC")
    List<Appeal> searchByEmail(@Param("email") String email);

    @Query("SELECT a FROM Appeal a ORDER BY a.createdAt DESC")
    List<Appeal> findAllOrderByCreatedDesc();

    @Query("SELECT COUNT(a) FROM Appeal a WHERE a.email = :email AND a.createdAt > CURRENT_TIMESTAMP - 1 DAY")
    long countRecentAppeals(@Param("email") String email);
}
