package poly.edu.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import poly.edu.dao.EventDAO;
import poly.edu.dao.EventPostsDAO;
import poly.edu.dto.EventDTO;
import poly.edu.dto.EventPostDTO;
import poly.edu.entity.Event;
import poly.edu.service.AdminEventService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminEventServiceImpl implements AdminEventService {

    private final EventDAO eventDAO;
    private final EventPostsDAO eventPostsDAO;

    // ===== Mapping =====
    private EventDTO toDTO(Event e) {
        EventDTO dto = new EventDTO();
        dto.setEventID(e.getEventID());
        dto.setEventName(e.getEventName());
        dto.setWinner(e.getWinner());
        dto.setStartAt(e.getStartAt());
        dto.setEndAt(e.getEndAt());
        return dto;
    }

    private Event toEntity(EventDTO dto) {
        return Event.builder()
                .eventID(dto.getEventID())
                .eventName(dto.getEventName())
                .winner(dto.getWinner())
                .startAt(dto.getStartAt())
                .endAt(dto.getEndAt())
                .build();
    }

    // ===== CRUD Event =====
    @Override
    public List<EventDTO> findAllEvents() {
        return eventDAO.findAll().stream().map(this::toDTO).toList();
    }

    @Override
    public EventDTO findEventById(Integer id) {
        return toDTO(eventDAO.findById(id).orElseThrow());
    }

    @Override
    public EventDTO saveEvent(EventDTO dto) {
        return toDTO(eventDAO.save(toEntity(dto)));
    }

    @Override
    public void deleteEvent(Integer id) {
        eventDAO.deleteById(id);
    }

    // ===== Event Detail =====
    @Override
    public List<EventPostDTO> getPostsOfEvent(Integer eventID) {
        return eventPostsDAO.findByEvent_EventID(eventID)
                .stream()
                .map(ep -> {
                    EventPostDTO dto = new EventPostDTO();
                    dto.setEventPostID(ep.getEventPostID());
                    dto.setPostID(ep.getPost().getPostID());
                    dto.setPostTitle(ep.getPost().getTitle());
                    dto.setUsername(ep.getPost().getAccount().getUsername());
                    return dto;
                })
                .toList();
    }

    @Override
    public void removePostFromEvent(Integer eventPostID) {
        eventPostsDAO.deleteById(eventPostID);
    }
}

