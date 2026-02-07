package poly.edu.service;

import poly.edu.dto.EventDTO;
import poly.edu.dto.EventPostDTO;
import java.util.List;

public interface AdminEventService {
    // CRUD Event
    List<EventDTO> findAllEvents();
    EventDTO findEventById(Integer id);
    EventDTO saveEvent(EventDTO dto);
    void deleteEvent(Integer id);

    // Chi tiết event
    List<EventPostDTO> getPostsOfEvent(Integer eventID);
    void removePostFromEvent(Integer eventPostID);
}
