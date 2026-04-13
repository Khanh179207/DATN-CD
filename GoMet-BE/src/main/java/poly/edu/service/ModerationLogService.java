package poly.edu.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import poly.edu.dao.ModerationLogDAO;
import poly.edu.entity.ModerationLog;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ModerationLogService {

    private final ModerationLogDAO moderationLogDAO;

    // Hàm gọi phát ăn luôn, bắn data vào DB
    public void logAction(Integer targetID, String targetType, String action, Integer adminID, String adminName, String reason) {
        ModerationLog log = ModerationLog.builder()
                .targetID(targetID)
                .targetType(targetType)
                .action(action)
                .adminID(adminID)
                .adminName(adminName)
                .reason(reason)
                .build();
        moderationLogDAO.save(log);
    }
    // Thêm hàm này để lấy toàn bộ Log (Sắp xếp mới nhất lên đầu)
    public List<ModerationLog> getAllLogs() {
        return moderationLogDAO.findAll(org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "createdAt"));
    }
}