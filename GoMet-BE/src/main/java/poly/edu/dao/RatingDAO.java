package poly.edu.dao;

import poly.edu.entity.Rating;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RatingDAO extends JpaRepository<Rating, Integer> {

    Optional<Rating> findByAccount_AccountIDAndPost_PostID(Integer accountID, Integer postID);

    Page<Rating> findByPost_PostID(Integer postID, Pageable pageable);

    List<Rating> findAllByPost_PostID(Integer postID);

    @Query("SELECT AVG(r.rate) FROM Rating r WHERE r.post.postID = :postId")
    Double findAvgRateByPost(@Param("postId") Integer postId);

    @Query("SELECT r.rate, COUNT(r) FROM Rating r WHERE r.post.postID = :postId GROUP BY r.rate")
    List<Object[]> countByRateGroupBy(@Param("postId") Integer postId);
}
