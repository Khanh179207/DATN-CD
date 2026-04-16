package poly.edu.dao;

import poly.edu.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface NotificationDAO extends JpaRepository<Notification, Integer> {

    List<Notification> findByAccount_AccountID(Integer accountID);

    // Optimized query: return notifications for a user ordered by createdAt desc
    List<Notification> findByAccount_AccountIDOrderByCreatedAtDesc(Integer accountID);

    // Fetch actor and parent to avoid N+1 when rendering notification lists
    @Query("SELECT n FROM Notification n LEFT JOIN FETCH n.actor a LEFT JOIN FETCH n.parentNotification p WHERE n.account.accountID = ?1 ORDER BY n.createdAt DESC")
    List<Notification> findByAccount_AccountIDWithActorAndParentOrderByCreatedAtDesc(Integer accountID);

    @Query("SELECT n FROM Notification n WHERE n.type = ?1 ORDER BY n.createdAt DESC")
    List<Notification> findByTypeOrderByCreatedAtDesc(String type);

    // Admin: fetch notifications of a given type but exclude cloned records
    // (parentNotification IS NULL)
    @Query("SELECT n FROM Notification n LEFT JOIN FETCH n.actor a LEFT JOIN FETCH n.account acc WHERE n.type = ?1 AND n.parentNotification IS NULL ORDER BY n.createdAt DESC")
    List<Notification> findAdminNotificationsByTypeExcludingClones(String type);

    @Query("SELECT n FROM Notification n " +
            "LEFT JOIN FETCH n.account acc " +
            "LEFT JOIN FETCH n.actor actor " +
            "LEFT JOIN FETCH n.post post " +
            "WHERE n.notificationID = ?1")
    Optional<Notification> findByIdWithAccountActorAndPost(Integer notificationID);

    // Find if a clone exists for a given parent notification and account
    java.util.Optional<Notification> findByParentNotification_NotificationIDAndAccount_AccountID(
            Integer parentNotificationID, Integer accountID);

    @Query("SELECT n FROM Notification n " +
            "LEFT JOIN FETCH n.account acc " +
            "WHERE n.parentNotification.notificationID = ?1 AND n.isRead = 1 " +
            "ORDER BY n.readAt DESC, n.createdAt DESC")
    List<Notification> findReadClonesByParentNotificationId(Integer parentNotificationID);

    // Fetch all global notifications
    @Query("SELECT n FROM Notification n WHERE n.isGlobal = true ORDER BY n.createdAt DESC")
    List<Notification> findGlobalNotificationsOrderByCreatedAtDesc();
}
