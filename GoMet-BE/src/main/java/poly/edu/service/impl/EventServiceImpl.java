package poly.edu.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import poly.edu.dao.EventDAO;
import poly.edu.dao.EventPostsDAO;
import poly.edu.dto.EventDTO;
import poly.edu.entity.Event;
import poly.edu.service.EventService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventDAO eventDAO;
    private final EventPostsDAO eventPostsDAO;

    @Override
    public List<EventDTO> getAllEvents() {
        return eventDAO.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public EventDTO getEventById(Integer id) {
        return eventDAO.findById(id).map(this::toDTO).orElse(null);
    }

    @Override
    public List<EventDTO> getActiveEvents() {
        LocalDateTime now = LocalDateTime.now();
        return eventDAO.findActiveEvents(now).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    private EventDTO toDTO(Event event) {
        EventDTO dto = new EventDTO();
        dto.setEventID(event.getEventID());
        dto.setEventName(event.getEventName());
        dto.setStartAt(event.getStartAt());
        dto.setEndAt(event.getEndAt());
        dto.setVoteStartAt(event.getVoteStartAt());
        dto.setVoteEndAt(event.getVoteEndAt());
        dto.setBannerImage(event.getBannerImage());
        dto.setWinnerPostID(event.getWinner());
        dto.setDescription(event.getDescription());
        dto.setRules(event.getRules());
        dto.setReward(event.getReward());

        long participantCount = event.getEventID() != null
            ? eventPostsDAO.countByEvent_EventID(event.getEventID())
            : 0L;
        dto.setParticipantCount(participantCount);
        dto.setPostCount(participantCount);

        LocalDateTime now = LocalDateTime.now();
        if (event.getStartAt() != null && now.isBefore(event.getStartAt())) {
            dto.setStatus("upcoming");
        } else if (event.getEndAt() != null && now.isAfter(event.getEndAt())) {
            dto.setStatus("ended");
        } else {
            dto.setStatus("active");
        }

        return dto;
    }
}