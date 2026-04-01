package poly.edu.dao;

import poly.edu.entity.InteractionLog;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface InteractionLogDAOCustom {

    void saveCustom(InteractionLog log);

    List<Map<String, Object>> findTopTrending(LocalDateTime startDate, int limit);
}
