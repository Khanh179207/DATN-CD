package poly.edu.dao;

import poly.edu.entity.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RatingDAO extends JpaRepository<Rating, Integer> {

    Optional<Rating> findByAccount_AccountIDAndPost_PostID(Integer accountID, Integer postID);

    @Query("SELECT r.post.postID, AVG(r.rate), COUNT(r) FROM Rating r WHERE r.post.postID IN :postIds GROUP BY r.post.postID")
    List<Object[]> summarizeByPostIds(@Param("postIds") List<Integer> postIds);
}
