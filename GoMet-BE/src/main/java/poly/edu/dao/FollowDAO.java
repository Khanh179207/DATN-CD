package poly.edu.dao;

import poly.edu.entity.Follow;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FollowDAO extends JpaRepository<Follow, Integer> {

    List<Follow> findByFollower_AccountID(Integer accountID);
}
