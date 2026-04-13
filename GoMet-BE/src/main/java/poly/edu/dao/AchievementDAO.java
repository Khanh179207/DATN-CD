package poly.edu.dao;

import poly.edu.entity.Achievement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AchievementDAO extends JpaRepository<Achievement, Integer> {
}
