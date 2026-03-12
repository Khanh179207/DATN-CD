package poly.edu.dao;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import poly.edu.entity.UserAchievement;

import java.util.List;

public interface UserAchievementDAO extends JpaRepository<UserAchievement, Integer> {

    /** All achievements earned by a specific account. */
    List<UserAchievement> findByAccount_AccountIDOrderByReceivedAtDesc(Integer accountID);

    @EntityGraph(attributePaths = {"account", "achievement"})
    List<UserAchievement> findTop5ByOrderByReceivedAtDesc();

    /** Check if an account already has a specific achievement. */
    boolean existsByAccount_AccountIDAndAchievement_AchievementID(Integer accountID, Integer achievementID);
}

