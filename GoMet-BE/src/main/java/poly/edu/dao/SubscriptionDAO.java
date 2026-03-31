package poly.edu.dao;

import poly.edu.entity.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface SubscriptionDAO extends JpaRepository<Subscription, Integer> {
    // Find active subscriptions for an account
    List<Subscription> findByAccount_AccountIDAndIsActive(Integer accountID, Integer isActive);
    
    // Find subscriptions that are ending soon
    @Query("SELECT s FROM Subscription s WHERE s.isActive = 1 AND s.endAt BETWEEN :start AND :end")
    List<Subscription> findEndingSoon(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}
