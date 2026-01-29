package poly.edu.dao;

import poly.edu.entity.History;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface HistoryDAO extends JpaRepository<History, Integer> {


}
