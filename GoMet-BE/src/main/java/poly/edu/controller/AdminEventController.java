package poly.edu.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import poly.edu.dto.AdminEventDTO;
import poly.edu.dto.AdminEventPostDTO;
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
    public List<AdminEventDTO> getAll() {
        return adminEventService.findAllEvents();
    }

    @GetMapping("/{id}")
    public AdminEventDTO getOne(@PathVariable Integer id) {
        return adminEventService.findEventById(id);
    }

    @PostMapping
    public AdminEventDTO create(@RequestBody AdminEventDTO dto) {
        return adminEventService.saveEvent(dto);
    }

    @PutMapping("/{id}")
    public AdminEventDTO update(@PathVariable Integer id,
                                @RequestBody AdminEventDTO dto) {
        dto.setEventID(id);
        return adminEventService.saveEvent(dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        adminEventService.deleteEvent(id);
    }

    // ===== Event Detail =====
    @GetMapping("/{id}/posts")
    public List<AdminEventPostDTO> getPosts(@PathVariable Integer id) {
        return adminEventService.getPostsOfEvent(id);
    }

    @DeleteMapping("/posts/{eventPostID}")
    public void removePost(@PathVariable Integer eventPostID) {
        adminEventService.removePostFromEvent(eventPostID);
    }
}

