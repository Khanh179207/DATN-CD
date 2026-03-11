package poly.edu.service;

import poly.edu.dto.EventDTO;
import java.util.List;

public interface EventService {
    List<EventDTO> getAllEvents();
    EventDTO getEventById(Integer id); // 👈 Tên hàm phải như này
    List<EventDTO> getActiveEvents();
}