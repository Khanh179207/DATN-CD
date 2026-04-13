package poly.edu.dao;

import poly.edu.entity.UserAchievement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserAchievementDAO extends JpaRepository<UserAchievement, Integer> {

    /** All achievements earned by a specific account. */
    List<UserAchievement> findByAccount_AccountIDOrderByReceivedAtDesc(Integer accountID);

    /** Check if an account already has a specific achievement. */
    boolean existsByAccount_AccountIDAndAchievement_AchievementID(Integer accountID, Integer achievementID);
}

