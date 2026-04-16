package poly.edu.service;

import poly.edu.dto.FavoriteDTO;
import java.util.List;

public interface FavoriteService {

    void saveFavorite(Integer accountID, Integer postID);

    List<FavoriteDTO> getFavorites(Integer accountID);;

    void removeFavorite(Integer accountID, Integer postID);

    boolean checkFavorite(Integer accountID, Integer postID);
}
