package poly.edu.dao;

import poly.edu.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface NotificationDAO extends JpaRepository<Notification, Integer> {

    List<Notification> findByAccount_AccountID(Integer accountID);

    // Optimized query: return notifications for a user ordered by createdAt desc
    List<Notification> findByAccount_AccountIDOrderByCreatedAtDesc(Integer accountID);

    // Fetch actor and parent to avoid N+1 when rendering notification lists
    @Query("SELECT n FROM Notification n LEFT JOIN FETCH n.actor a LEFT JOIN FETCH n.parentNotification p WHERE n.account.accountID = ?1 ORDER BY n.createdAt DESC")
    List<Notification> findByAccount_AccountIDWithActorAndParentOrderByCreatedAtDesc(Integer accountID);

    @Query("SELECT n FROM Notification n WHERE n.type = ?1 ORDER BY n.createdAt DESC")
    List<Notification> findByTypeOrderByCreatedAtDesc(String type);
}
