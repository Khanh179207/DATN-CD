package poly.edu.service;

import poly.edu.dto.AdminEventDTO;
import poly.edu.dto.AdminEventPostDTO;
import java.util.List;

public interface AdminEventService {
    // CRUD Event
    List<AdminEventDTO> findAllEvents();
    AdminEventDTO findEventById(Integer id);
    AdminEventDTO saveEvent(AdminEventDTO dto);
    void deleteEvent(Integer id);

    // Chi tiết event
    List<AdminEventPostDTO> getPostsOfEvent(Integer eventID);
    void removePostFromEvent(Integer eventPostID);
    void restoreEvent(Integer eventID);
    
    // 🎁 REWARD
    void forceEndEventWithReward(Integer eventID);
}
