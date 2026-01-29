package poly.edu.dao;

import poly.edu.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventDAO extends JpaRepository<Event, Integer> {
}
