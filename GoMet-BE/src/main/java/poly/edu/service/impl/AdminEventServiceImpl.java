package poly.edu.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import poly.edu.dao.EventDAO;
import poly.edu.dao.EventPostsDAO;
import poly.edu.dto.AdminEventDTO;
import poly.edu.dto.AdminEventPostDTO;
import poly.edu.entity.Event;
import poly.edu.service.AdminEventService;
import poly.edu.service.EventRewardService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminEventServiceImpl implements AdminEventService {

    private final EventDAO eventDAO;
    private final EventPostsDAO eventPostsDAO;
    private final EventRewardService eventRewardService;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

    private AdminEventDTO toDTO(Event e) {
        AdminEventDTO dto = new AdminEventDTO();
        dto.setEventID(e.getEventID());
        dto.setEventName(e.getEventName());
        dto.setWinner(e.getWinner());

        dto.setStartAt(e.getStartAt() != null ? e.getStartAt().toString() : null);
        dto.setEndAt(e.getEndAt() != null ? e.getEndAt().toString() : null);
        dto.setVoteStartAt(e.getVoteStartAt() != null ? e.getVoteStartAt().toString() : null);
        dto.setVoteEndAt(e.getVoteEndAt() != null ? e.getVoteEndAt().toString() : null);

        dto.setBannerImage(e.getBannerImage());
        dto.setDescription(e.getDescription());
        dto.setReward(e.getReward());
        dto.setMaxVotes(e.getMaxVotes() != null ? e.getMaxVotes() : 3);

        dto.setIsActive(e.getIsActive());
        dto.setIsForceEnded(e.getIsForceEnded());

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
            event.setIsActive(1);
            event.setIsForceEnded(0);
        }

        event.setEventName(dto.getEventName());
        event.setBannerImage(dto.getBannerImage());
        event.setDescription(dto.getDescription());
        event.setReward(dto.getReward());
        event.setMaxVotes(dto.getMaxVotes() != null ? dto.getMaxVotes() : 3);
        event.setWinner(dto.getWinner());

        if (dto.getIsForceEnded() != null) {
            event.setIsForceEnded(dto.getIsForceEnded());
        }

        try {
            String startStr = dto.getStartAt() != null && dto.getStartAt().length() > 16 ? dto.getStartAt().substring(0, 16) : dto.getStartAt();
            String endStr = dto.getEndAt() != null && dto.getEndAt().length() > 16 ? dto.getEndAt().substring(0, 16) : dto.getEndAt();
            String voteStartStr = dto.getVoteStartAt() != null && dto.getVoteStartAt().length() > 16 ? dto.getVoteStartAt().substring(0, 16) : dto.getVoteStartAt();
            String voteEndStr = dto.getVoteEndAt() != null && dto.getVoteEndAt().length() > 16 ? dto.getVoteEndAt().substring(0, 16) : dto.getVoteEndAt();

            if (startStr != null && !startStr.isEmpty()) event.setStartAt(LocalDateTime.parse(startStr, formatter));
            if (endStr != null && !endStr.isEmpty()) event.setEndAt(LocalDateTime.parse(endStr, formatter));
            if (voteStartStr != null && !voteStartStr.isEmpty()) event.setVoteStartAt(LocalDateTime.parse(voteStartStr, formatter));
            if (voteEndStr != null && !voteEndStr.isEmpty()) event.setVoteEndAt(LocalDateTime.parse(voteEndStr, formatter));
        } catch (Exception ex) {
            System.err.println("Lỗi parse ngày tháng: " + ex.getMessage());
        }

        return toDTO(eventDAO.save(event));
    }

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
        Event event = eventDAO.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sự kiện"));
        event.setIsActive(0);
        eventDAO.save(event);
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

            if (ep.getPost().getAccount() != null) {
                d.setUsername(ep.getPost().getAccount().getUsername());
                d.setAccountID(ep.getPost().getAccount().getAccountID());
            }
            return d;
        }).toList();
    }

    // ===== THÊM ĐOẠN NÀY ĐỂ FIX LỖI BUILD MAVEN =====
    @Override
    @Transactional
    public void restoreEvent(Integer eventID) {
        Event event = eventDAO.findById(eventID)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sự kiện"));

        event.setIsActive(1);
        eventDAO.save(event);
    }

    @Override
    @Transactional
    public void removePostFromEvent(Integer eventPostID) {
        eventPostsDAO.deleteById(eventPostID);
    }

    // ===== 🎁 FORCE END EVENT + AUTO REWARD =====
    @Override
    @Transactional
    public void forceEndEventWithReward(Integer eventID) {
        log.info("🚀 Force ending event {} with auto reward", eventID);
        
        Event event = eventDAO.findById(eventID)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sự kiện"));
        
        // Set ngày kết thúc về hiện tại
        LocalDateTime now = LocalDateTime.now();
        event.setEndAt(now);
        event.setVoteEndAt(now);
        event.setIsForceEnded(1);
        eventDAO.save(event);
        
        log.info("✅ Event {} force ended at {}", eventID, now);
        
        // 🎁 Auto trigger reward
        try {
            eventRewardService.rewardTopUsersForEvent(eventID);
            log.info("🎉 Reward distributed for event {}", eventID);
        } catch (Exception e) {
            log.warn("⚠️ Failed to reward event {}: {}", eventID, e.getMessage());
            // Không throw exception để event vẫn kết thúc, chỉ log warning
        }
    }
}