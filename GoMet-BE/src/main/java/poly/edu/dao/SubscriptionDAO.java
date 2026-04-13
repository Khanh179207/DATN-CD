package poly.edu.dao;

import poly.edu.entity.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubscriptionDAO extends JpaRepository<Subscription, Integer> {
}
