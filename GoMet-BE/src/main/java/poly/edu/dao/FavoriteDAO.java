package poly.edu.dao;

import poly.edu.entity.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FavoriteDAO extends JpaRepository<Favorite, Integer> {

    List<Favorite> findByAccount_AccountID(Integer accountID);

    Optional<Favorite> findByAccount_AccountIDAndPost_PostID(Integer accountID, Integer postID);

    long countByPost_PostID(Integer postID);

    @Query("SELECT f.post.postID, COUNT(f) FROM Favorite f WHERE f.post.postID IN :postIds GROUP BY f.post.postID")
    List<Object[]> countByPostIds(@Param("postIds") List<Integer> postIds);

    boolean existsByAccount_AccountIDAndPost_PostID(Integer accountID, Integer postID);
    void deleteByAccount_AccountIDAndPost_PostID(Integer accountID, Integer postID);
}
