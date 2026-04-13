package poly.edu.dao;

import poly.edu.entity.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FavoriteDAO extends JpaRepository<Favorite, Integer> {

    List<Favorite> findByAccount_AccountID(Integer accountID);

    Optional<Favorite> findByAccount_AccountIDAndPost_PostID(Integer accountID, Integer postID);

    long countByPost_PostID(Integer postID);

    boolean existsByAccount_AccountIDAndPost_PostID(Integer accountID, Integer postID);
    void deleteByAccount_AccountIDAndPost_PostID(Integer accountID, Integer postID);
}
