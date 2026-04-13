package poly.edu.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import poly.edu.dao.EventDAO;
import poly.edu.dto.EventDTO;
import poly.edu.dto.RewardedUserDTO;
import poly.edu.entity.Event;
import poly.edu.service.EventService;
import poly.edu.service.EventRewardService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventDAO eventDAO;
    private final EventRewardService eventRewardService;

    @Override
    public List<EventDTO> getAllEvents() {
        return eventDAO.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    // 🔥 Sửa tên hàm này từ getById thành getEventById
    @Override
    public EventDTO getEventById(Integer id) {
        return eventDAO.findById(id).map(this::toDTO).orElse(null);
    }

    @Override
    public List<EventDTO> getActiveEvents() {
        // 🔥 Đổi từ LocalDate sang LocalDateTime cho đồng bộ DB
        LocalDateTime now = LocalDateTime.now();
        return eventDAO.findActiveEvents(now).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // 🔥 NEW: Reward top 3 users for an event
    @Override
    public List<RewardedUserDTO> rewardTopUsersForEvent(Integer eventId) {
        return eventRewardService.rewardTopUsersForEvent(eventId);
    }

    // 🔥 NEW: Check if event is eligible for reward
    @Override
    public boolean isEventEligibleForReward(Integer eventId) {
        return eventRewardService.isEventEligibleForReward(eventId);
    }

    private EventDTO toDTO(Event e) {
        EventDTO dto = new EventDTO();
        dto.setEventID(e.getEventID());
        dto.setEventName(e.getEventName());
        dto.setStartAt(e.getStartAt());
        dto.setEndAt(e.getEndAt());
        dto.setBannerImage(e.getBannerImage());
        dto.setWinnerPostID(e.getWinner());

        // Mapping các trường Vote mới
        dto.setVoteStartAt(e.getVoteStartAt());
        dto.setVoteEndAt(e.getVoteEndAt());
        dto.setDescription(e.getDescription()); // 🔥 Thiếu dòng này là nó null ngay!
        dto.setReward(e.getReward());
        // Lúc map từ Entity sang DTO, sếp nhớ thêm 2 dòng này:
        dto.setIsActive(e.getIsActive());
        dto.setIsForceEnded(e.getIsForceEnded());
        dto.setMaxVotes(e.getMaxVotes());

        long count = (e.getEventPosts() != null) ? e.getEventPosts().size() : 0;
        dto.setPostCount(count);

        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(e.getStartAt())) dto.setStatus("upcoming");
        else if (now.isAfter(e.getEndAt())) dto.setStatus("ended");
        else dto.setStatus("active");

        return dto;

    }

}