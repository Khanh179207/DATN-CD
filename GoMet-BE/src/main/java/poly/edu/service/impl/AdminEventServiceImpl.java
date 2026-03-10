package poly.edu.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import poly.edu.dao.EventDAO;
import poly.edu.dao.EventPostsDAO;
import poly.edu.dto.AdminEventDTO;
import poly.edu.dto.AdminEventPostDTO;
import poly.edu.entity.Event;
import poly.edu.entity.EventPosts;
import poly.edu.service.AdminEventService;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminEventServiceImpl implements AdminEventService {

    private final EventDAO eventDAO;
    private final EventPostsDAO eventPostsDAO;

    // ===== Mapping (Dữ liệu thật 100% từ Database) =====
    private AdminEventDTO toDTO(Event e) {
        AdminEventDTO dto = new AdminEventDTO();
        dto.setEventID(e.getEventID());
        dto.setEventName(e.getEventName());
        dto.setWinner(e.getWinner());
        dto.setStartAt(e.getStartAt());
        dto.setEndAt(e.getEndAt());
        dto.setVoteStartAt(e.getVoteStartAt());
        dto.setVoteEndAt(e.getVoteEndAt());
        dto.setBannerImage(e.getBannerImage());
        dto.setDescription(e.getDescription());
        dto.setRules(e.getRules());
        dto.setReward(e.getReward());

        // 🔥 FIX LỖI: Lấy maxVotes từ DB gửi lên cho Vue hiển thị (Nếu null thì mặc định 3)
        dto.setMaxVotes(e.getMaxVotes() != null ? e.getMaxVotes() : 3);

        // 🔥 Logic tính toán thống kê cho Admin
        if (e.getEventPosts() != null) {
            dto.setPostCount(e.getEventPosts().size());
            int total = e.getEventPosts().stream()
                    .mapToInt(ep -> ep.getVoteCount() != null ? ep.getVoteCount() : 0)
                    .sum();
            dto.setTotalVotes(total);
        } else {
            dto.setPostCount(0);
            dto.setTotalVotes(0);
        }

        return dto;
    }

    private Event toEntity(AdminEventDTO dto) {
        return Event.builder()
                .eventID(dto.getEventID())
                .eventName(dto.getEventName())
                .winner(dto.getWinner())
                .startAt(dto.getStartAt())
                .endAt(dto.getEndAt())
                .voteStartAt(dto.getVoteStartAt())
                .voteEndAt(dto.getVoteEndAt())
                .bannerImage(dto.getBannerImage())
                .description(dto.getDescription())
                .rules(dto.getRules())
                .reward(dto.getReward())
                // 🔥 FIX LỖI: Nhận maxVotes từ Vue gửi xuống để lưu vào Database
                .maxVotes(dto.getMaxVotes() != null ? dto.getMaxVotes() : 3)
                .build();
    }

    @Override
    public List<AdminEventDTO> findAllEvents() {
        return eventDAO.findAll().stream().map(this::toDTO).toList();
    }

    @Override
    public AdminEventDTO findEventById(Integer id) {
        return eventDAO.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sự kiện ID: " + id));
    }

    @Override
    @Transactional
    public AdminEventDTO saveEvent(AdminEventDTO dto) {
        Event event = toEntity(dto);
        return toDTO(eventDAO.save(event));
    }

    @Override
    @Transactional
    public void deleteEvent(Integer id) {
        eventDAO.deleteById(id);
    }

    @Override
    public List<AdminEventPostDTO> getPostsOfEvent(Integer eventID) {
        return eventPostsDAO.findByEvent_EventID(eventID)
                .stream()
                .map(ep -> {
                    AdminEventPostDTO dto = new AdminEventPostDTO();
                    dto.setEventPostID(ep.getEventPostID());
                    dto.setPostID(ep.getPost().getPostID());
                    dto.setPostTitle(ep.getPost().getTitle());
                    dto.setPostImage(ep.getPost().getMedia());
                    dto.setVoteCount(ep.getVoteCount() != null ? ep.getVoteCount() : 0);
                    dto.setUsername(ep.getPost().getAccount().getUsername());
                    return dto;
                })
                .toList();
    }

    @Override
    @Transactional
    public void removePostFromEvent(Integer eventPostID) {
        eventPostsDAO.deleteById(eventPostID);
    }
}