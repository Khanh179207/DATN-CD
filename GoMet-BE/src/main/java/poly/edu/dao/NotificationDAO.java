package poly.edu.dao;

import poly.edu.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface NotificationDAO extends JpaRepository<Notification, Integer> {

    List<Notification> findByAccount_AccountID(Integer accountID);

    @Query("SELECT n FROM Notification n WHERE n.type = ?1 ORDER BY n.createdAt DESC")
    List<Notification> findByTypeOrderByCreatedAtDesc(String type);
}
