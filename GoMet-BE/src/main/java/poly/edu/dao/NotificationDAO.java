package poly.edu.dao;

import poly.edu.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationDAO extends JpaRepository<Notification, Integer> {

    List<Notification> findByAccount_AccountID(Integer accountID);
}
