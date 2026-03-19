package poly.edu.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import poly.edu.dao.AccountDAO;
import poly.edu.dao.FavoriteDAO;
import poly.edu.dao.PostDAO;
import poly.edu.dto.FavoriteDTO;
import poly.edu.entity.Account;
import poly.edu.entity.Favorite;
import poly.edu.entity.Post;
import poly.edu.service.FavoriteService;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FavoriteServiceImpl implements FavoriteService {

    @Autowired
    private FavoriteDAO favoriteDAO;

    @Autowired
    private AccountDAO accountDAO;

    @Autowired
    private PostDAO postDAO;

    @Override
    public void saveFavorite(Integer accountID, Integer postID) {

        if (favoriteDAO.existsByAccount_AccountIDAndPost_PostID(accountID, postID)) {
            return;
        }

        Account account = accountDAO.findById(accountID).orElse(null);
        Post post = postDAO.findById(postID).orElse(null);

        if (account == null || post == null) {
            throw new RuntimeException("Account hoặc Post không tồn tại");
        }

        Favorite favorite = Favorite.builder()
                .account(account)
                .post(post)
                .build();

        favoriteDAO.save(favorite);
    }

    @Override
    public List<FavoriteDTO> getFavorites(Integer accountID) {

        List<Favorite> favorites = favoriteDAO.findByAccount_AccountID(accountID);

        return favorites.stream().map(f -> {

            Post p = f.getPost();

            FavoriteDTO dto = new FavoriteDTO();
            dto.setFavoriteID(f.getFavoriteID());
            dto.setPostID(p.getPostID());
            dto.setTitle(p.getTitle());
            dto.setMedia(p.getMedia());
            dto.setCookingTime(p.getCookingTime());
            dto.setLevel(p.getLevel());
            dto.setCategoryName(p.getCategory().getCategoryName());

            // thêm dữ liệu mới
            double avgRating = 0;

            if (p.getRatings() != null && !p.getRatings().isEmpty()) {
                avgRating = p.getRatings()
                        .stream()
                        .mapToDouble(r -> r.getRate())   // hoặc r.getRating()
                        .average()
                        .orElse(0);
            }
            dto.setRating(avgRating);
            dto.setViews(p.getViews());
            dto.setUserName(p.getAccount().getUsername());
            dto.setAvatar(p.getAccount().getAvatar());

            return dto;

        }).collect(Collectors.toList());
    }

    @Override
    public void removeFavorite(Integer accountID, Integer postID) {

        favoriteDAO.findByAccount_AccountIDAndPost_PostID(accountID, postID)
                .ifPresent(favoriteDAO::delete);
    }

    @Override
    public boolean checkFavorite(Integer accountID, Integer postID) {

        return favoriteDAO.existsByAccount_AccountIDAndPost_PostID(accountID, postID);
    }
}
