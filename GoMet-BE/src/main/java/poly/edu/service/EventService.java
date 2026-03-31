package poly.edu.service;

import poly.edu.dto.EventDTO;
import poly.edu.dto.RewardedUserDTO;
import java.util.List;

public interface EventService {
    List<EventDTO> getAllEvents();
    EventDTO getEventById(Integer id);
    List<EventDTO> getActiveEvents();
    
    List<RewardedUserDTO> rewardTopUsersForEvent(Integer eventId);
    boolean isEventEligibleForReward(Integer eventId);
}