package poly.edu.scheduled;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import poly.edu.dao.EventDAO;
import poly.edu.entity.Event;
import poly.edu.service.EventRewardService;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class EventRewardScheduler {

    private final EventDAO eventDAO;
    private final EventRewardService eventRewardService;

    /**
     * Chạy mỗi 1 phút
     * Kiểm tra những event nào đã hết giờ bình chọn (voteEndAt <= now)
     * Và chưa được thưởng (winner = NULL) thì tự động thưởng
     */
    @Scheduled(fixedDelay = 60000) // 60 giây
    public void autoRewardCompletedEvents() {
        try {
            log.debug("🔍 Checking for completed events that need rewards...");
            
            LocalDateTime now = LocalDateTime.now();
            
            // Lấy tất cả sự kiện chưa được thưởng (winner IS NULL)
            List<Event> incompleteEvents = eventDAO.findByWinnerIsNull();
            
            for (Event event : incompleteEvents) {
                // Kiểm tra xem sự kiện đã kết thúc chưa
                LocalDateTime endTime = event.getVoteEndAt() != null ? 
                        event.getVoteEndAt() : event.getEndAt();
                
                if (endTime != null && now.isAfter(endTime)) {
                    try {
                        log.info("⏰ Event {} has ended at {}, triggering auto reward", 
                                event.getEventID(), endTime);
                        eventRewardService.rewardTopUsersForEvent(event.getEventID());
                        log.info("✨ Auto reward completed for event {}", event.getEventID());
                    } catch (Exception e) {
                        log.warn("⚠️ Failed to auto-reward event {}: {}", 
                                event.getEventID(), e.getMessage());
                        // Không throw để scheduler tiếp tục kiểm tra event khác
                    }
                }
            }
            
        } catch (Exception e) {
            log.error("❌ Error in auto reward scheduler: {}", e.getMessage(), e);
        }
    }
}
