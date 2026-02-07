package poly.edu.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import poly.edu.entity.EventPosts;
import java.util.List;

public interface EventPostsDAO extends JpaRepository<EventPosts, Integer> {
    List<EventPosts> findByEvent_EventID(Integer eventID);
}
