package poly.edu.service;

import poly.edu.dto.EventDTO;
import poly.edu.dto.RewardedUserDTO;
import java.util.List;

public interface EventService {
    List<EventDTO> getAllEvents();
    EventDTO getEventById(Integer id); // 👈 Tên hàm phải như này
    List<EventDTO> getActiveEvents();
    
    // 🔥 NEW: Reward methods (uses event.winner as flag)
    List<RewardedUserDTO> rewardTopUsersForEvent(Integer eventId);
    boolean isEventEligibleForReward(Integer eventId);
}