package poly.edu.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.jdbc.core.JdbcTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class DatabaseCleanupTask {

    private final JdbcTemplate jdbcTemplate;
    @Scheduled(cron = "0/10 * * * * ?")
    public void runAutoCleanup() {
        try {
            log.info("==== BẮT ĐẦU TIẾN TRÌNH DỌN DẸP DATABASE ====");

            // Gọi Stored Procedure sếp đã tạo trong SQL
            jdbcTemplate.execute("EXEC sp_CleanupOldInteractionLogs");

            log.info("==== DỌN DẸP LOG CŨ (VIEW/LIKE > 1 NĂM) HOÀN TẤT ====");
        } catch (Exception e) {
            log.error("Lỗi khi thực thi Stored Procedure dọn dẹp: {}", e.getMessage());
        }
    }
}
