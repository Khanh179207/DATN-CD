package poly.edu.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import poly.edu.dao.EventDAO;
import poly.edu.dao.EventPostsDAO;
import poly.edu.dto.EventDTO;
import poly.edu.entity.Event;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {

    private final EventDAO eventDAO;
    private final EventPostsDAO eventPostsDAO;

    @GetMapping
    public ResponseEntity<List<EventDTO>> getAll() {
        List<EventDTO> result = eventDAO.findAll().stream().map(this::toDTO).collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventDTO> getById(@PathVariable Integer id) {
        return eventDAO.findById(id)
                .map(e -> ResponseEntity.ok(toDTO(e)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/active")
    public ResponseEntity<List<EventDTO>> getActive() {
        LocalDate now = LocalDate.now();
        List<EventDTO> result = eventDAO.findAll().stream()
                .filter(e -> !now.isBefore(e.getStartAt()) && !now.isAfter(e.getEndAt()))
                .map(this::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    private EventDTO toDTO(Event e) {
        EventDTO dto = new EventDTO();
        dto.setEventID(e.getEventID());
        dto.setEventName(e.getEventName());
        dto.setStartAt(e.getStartAt());
        dto.setEndAt(e.getEndAt());
        dto.setWinnerPostID(e.getWinner());

        long count = e.getEventPosts() != null ? e.getEventPosts().size() : 0;
        dto.setParticipantCount(count);

        LocalDate now = LocalDate.now();
        if (now.isBefore(e.getStartAt())) dto.setStatus("upcoming");
        else if (now.isAfter(e.getEndAt())) dto.setStatus("ended");
        else dto.setStatus("active");

        return dto;
    }
}
