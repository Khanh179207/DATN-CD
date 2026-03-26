package poly.edu.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import poly.edu.dao.ModerationLogDAO;
import poly.edu.entity.ModerationLog;

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
}