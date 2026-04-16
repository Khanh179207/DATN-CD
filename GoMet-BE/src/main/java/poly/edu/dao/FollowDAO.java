package poly.edu.dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import poly.edu.entity.Follow;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FollowDAO extends JpaRepository<Follow, Integer> {

    List<Follow> findByFollower_AccountID(Integer accountID);

    List<Follow> findByFollowee_AccountID(Integer followeeID);

    Optional<Follow> findByFollower_AccountIDAndFollowee_AccountIDAndStatus(
            Integer followerID, Integer followeeID, Integer status);

    // 🔥 NEW: Đếm số người follow mình mà nick của họ vẫn đang ACTIVE (isActive = 1)
    @Query("SELECT COUNT(f) FROM Follow f WHERE f.followee.accountID = :followeeID AND f.status = 1 AND f.follower.isActive = 1")
    long countActiveFollowers(@Param("followeeID") Integer followeeID);

    // 🔥 NEW: Đếm số người mình đang follow mà nick của họ vẫn đang ACTIVE (isActive = 1)
    @Query("SELECT COUNT(f) FROM Follow f WHERE f.follower.accountID = :followerID AND f.status = 1 AND f.followee.isActive = 1")
    long countActiveFollowing(@Param("followerID") Integer followerID);

    // Giữ lại các hàm cũ để tránh lỗi compile ở các chỗ khác
    long countByFollowee_AccountIDAndStatus(Integer followeeID, Integer status);
    long countByFollower_AccountIDAndStatus(Integer followerID, Integer status);

    Optional<Follow> findByFollower_AccountIDAndFollowee_AccountID(Integer followerID, Integer followeeID);

    @Query("SELECT f.followee.accountID FROM Follow f WHERE f.follower.accountID = :followerID AND f.status = :status")
    List<Integer> findFolloweeIdsByFollowerIdAndStatus(
            @Param("followerID") Integer followerID,
            @Param("status") Integer status
    );

    boolean existsByFollower_AccountIDAndFollowee_AccountIDAndStatus(Integer followerID, Integer followeeID, Integer status);
}