package poly.edu.dao;

import poly.edu.entity.UserAchievement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAchievementDAO extends JpaRepository<UserAchievement, Integer> {
}
