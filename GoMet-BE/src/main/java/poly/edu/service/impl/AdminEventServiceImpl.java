package poly.edu.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import poly.edu.dao.EventDAO;
import poly.edu.dao.EventPostsDAO;
import poly.edu.dto.AdminEventDTO;
import poly.edu.dto.AdminEventPostDTO;
import poly.edu.entity.Event;
import poly.edu.service.AdminEventService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminEventServiceImpl implements AdminEventService {

    private final EventDAO eventDAO;
    private final EventPostsDAO eventPostsDAO;

    // Bộ định dạng chuẩn cho input datetime-local của trình duyệt
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

    private AdminEventDTO toDTO(Event e) {
        AdminEventDTO dto = new AdminEventDTO();
        dto.setEventID(e.getEventID());
        dto.setEventName(e.getEventName());
        dto.setWinner(e.getWinner());

        // Đổi từ LocalDateTime sang String để gửi lên Vue
        dto.setStartAt(e.getStartAt() != null ? e.getStartAt().toString() : null);
        dto.setEndAt(e.getEndAt() != null ? e.getEndAt().toString() : null);
        dto.setVoteStartAt(e.getVoteStartAt() != null ? e.getVoteStartAt().toString() : null);
        dto.setVoteEndAt(e.getVoteEndAt() != null ? e.getVoteEndAt().toString() : null);

        dto.setBannerImage(e.getBannerImage());
        dto.setDescription(e.getDescription());
        dto.setRules(e.getRules());
        dto.setReward(e.getReward());
        dto.setMaxVotes(e.getMaxVotes() != null ? e.getMaxVotes() : 3);

        if (e.getEventPosts() != null) {
            dto.setPostCount(e.getEventPosts().size());
            int total = e.getEventPosts().stream()
                    .mapToInt(ep -> ep.getVoteCount() != null ? ep.getVoteCount() : 0)
                    .sum();
            dto.setTotalVotes(total);
        }
        return dto;
    }

    @Override
    @Transactional
    public AdminEventDTO saveEvent(AdminEventDTO dto) {
        Event event;
        if (dto.getEventID() != null) {
            event = eventDAO.findById(dto.getEventID())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy sự kiện"));
        } else {
            event = new Event();
        }

        // Cập nhật thông tin cơ bản
        event.setEventName(dto.getEventName());
        event.setBannerImage(dto.getBannerImage());
        event.setDescription(dto.getDescription());
        event.setRules(dto.getRules());
        event.setReward(dto.getReward());
        event.setMaxVotes(dto.getMaxVotes() != null ? dto.getMaxVotes() : 3);
        event.setWinner(dto.getWinner());

        // 🔥 XỬ LÝ NGÀY THÁNG: Chuyển từ String (DTO) sang LocalDateTime (Entity)
        // Cách này cực kỳ an toàn, không bao giờ lo bị NULL nếu Vue có gửi dữ liệu
        try {
            if (dto.getStartAt() != null && !dto.getStartAt().isEmpty())
                event.setStartAt(LocalDateTime.parse(dto.getStartAt(), formatter));

            if (dto.getEndAt() != null && !dto.getEndAt().isEmpty())
                event.setEndAt(LocalDateTime.parse(dto.getEndAt(), formatter));

            if (dto.getVoteStartAt() != null && !dto.getVoteStartAt().isEmpty())
                event.setVoteStartAt(LocalDateTime.parse(dto.getVoteStartAt(), formatter));

            if (dto.getVoteEndAt() != null && !dto.getVoteEndAt().isEmpty())
                event.setVoteEndAt(LocalDateTime.parse(dto.getVoteEndAt(), formatter));
        } catch (Exception ex) {
            System.err.println("Lỗi ép kiểu ngày tháng: " + ex.getMessage());
        }

        return toDTO(eventDAO.save(event));
    }

    // Các hàm khác giữ nguyên...
    @Override
    public List<AdminEventDTO> findAllEvents() {
        return eventDAO.findAll().stream().map(this::toDTO).toList();
    }

    @Override
    public AdminEventDTO findEventById(Integer id) {
        return eventDAO.findById(id).map(this::toDTO).orElse(null);
    }

    @Override
    @Transactional
    public void deleteEvent(Integer id) {
        eventDAO.deleteById(id);
    }

    @Override
    public List<AdminEventPostDTO> getPostsOfEvent(Integer eventID) {
        return eventPostsDAO.findByEvent_EventID(eventID).stream().map(ep -> {
            AdminEventPostDTO d = new AdminEventPostDTO();
            d.setEventPostID(ep.getEventPostID());
            d.setPostID(ep.getPost().getPostID());
            d.setPostTitle(ep.getPost().getTitle());
            d.setPostImage(ep.getPost().getMedia());
            d.setVoteCount(ep.getVoteCount() != null ? ep.getVoteCount() : 0);
            d.setUsername(ep.getPost().getAccount().getUsername());
            return d;
        }).toList();
    }

    @Override
    @Transactional
    public void removePostFromEvent(Integer eventPostID) {
        eventPostsDAO.deleteById(eventPostID);
    }
}