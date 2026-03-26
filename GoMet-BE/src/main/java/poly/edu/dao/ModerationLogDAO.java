package poly.edu.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import poly.edu.entity.ModerationLog;

import java.util.List;

public interface ModerationLogDAO extends JpaRepository<ModerationLog, Integer> {

    // Lấy toàn bộ lịch sử tiền án tiền sự của 1 User hoặc 1 Bài viết (Sắp xếp mới nhất lên đầu)
    List<ModerationLog> findByTargetIDAndTargetTypeOrderByCreatedAtDesc(Integer targetID, String targetType);
}