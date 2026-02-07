package poly.edu.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import poly.edu.dto.EventDTO;
import poly.edu.dto.EventPostDTO;
import poly.edu.service.AdminEventService;
import java.util.List;

@RestController
@RequestMapping("/api/admin/events")
@RequiredArgsConstructor
@CrossOrigin
public class AdminEventController {

    private final AdminEventService adminEventService;

    // CRUD Event
    @GetMapping
    public List<EventDTO> getAll() {
        return adminEventService.findAllEvents();
    }

    @GetMapping("/{id}")
    public EventDTO getOne(@PathVariable Integer id) {
        return adminEventService.findEventById(id);
    }

    @PostMapping
    public EventDTO create(@RequestBody EventDTO dto) {
        return adminEventService.saveEvent(dto);
    }

    @PutMapping("/{id}")
    public EventDTO update(@PathVariable Integer id,
                           @RequestBody EventDTO dto) {
        dto.setEventID(id);
        return adminEventService.saveEvent(dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        adminEventService.deleteEvent(id);
    }

    // ===== Event Detail =====
    @GetMapping("/{id}/posts")
    public List<EventPostDTO> getPosts(@PathVariable Integer id) {
        return adminEventService.getPostsOfEvent(id);
    }

    @DeleteMapping("/posts/{eventPostID}")
    public void removePost(@PathVariable Integer eventPostID) {
        adminEventService.removePostFromEvent(eventPostID);
    }
}

