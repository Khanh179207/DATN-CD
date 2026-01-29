package poly.edu.dao;

import poly.edu.entity.Rating;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RatingDAO extends JpaRepository<Rating, Integer> {

    Optional<Rating> findByAccount_AccountIDAndPost_PostID(Integer accountID, Integer postID);
}
