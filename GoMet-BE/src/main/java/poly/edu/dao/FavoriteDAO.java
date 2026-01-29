package poly.edu.dao;

import poly.edu.entity.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FavoriteDAO extends JpaRepository<Favorite, Integer> {

    List<Favorite> findByAccount_AccountID(Integer accountID);
}
