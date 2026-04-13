package poly.edu.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import poly.edu.entity.Event;
import java.time.LocalDateTime;
import java.util.List;

public interface EventDAO extends JpaRepository<Event, Integer> {

    // Tìm các sự kiện mà thời gian hiện tại nằm trong khoảng Start và End
    @Query("SELECT e FROM Event e WHERE :now BETWEEN e.startAt AND e.endAt")
    List<Event> findActiveEvents(@Param("now") LocalDateTime now);
}