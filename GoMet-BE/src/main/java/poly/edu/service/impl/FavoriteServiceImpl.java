package poly.edu.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import poly.edu.dao.AccountDAO;
import poly.edu.dao.FavoriteDAO;
import poly.edu.dao.PostDAO;
import poly.edu.dao.LikesDAO; // 🔥 PHẢI IMPORT THÊM DAO NÀY
import poly.edu.dto.FavoriteDTO;
import poly.edu.entity.Account;
import poly.edu.entity.Comment;
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

    @Autowired
    private LikesDAO likesDAO; // 🔥 TIÊM VÀO ĐỂ FIX LỖI LIKE

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
            dto.setViews(p.getViews());
            dto.setUserName(p.getAccount().getUsername());
            dto.setAvatar(p.getAccount().getAvatar());

            // 🔥 1. FIX LỖI THỜI GIAN "MỚI ĐÂY"
            // Lấy chính xác LocalDateTime từ Post để RecipeCard tính "5 phút trước"
            dto.setCreatedAt(p.getCreatedAt());

            // 🔥 Lấy trực tiếp từ field LikeCount do SQL Trigger đã tự động tính toán
            dto.setFavoriteCount(p.getLikeCount() != null ? p.getLikeCount() : 0);

            // 🔥 3. FIX LỖI MẤT TIM ĐỎ (TRẠNG THÁI LIKE)
            // Kiểm tra xem user đang xem (accountID) đã từng nhấn Like bài này chưa
            boolean checkIsLiked = likesDAO.existsByAccount_AccountIDAndPost_PostID(accountID, p.getPostID());
            dto.setIsLiked(checkIsLiked);

            // --- Logic tính Rating từ Comment (Giữ nguyên của sếp) ---
            double avgRating = 0;
            if (p.getComments() != null && !p.getComments().isEmpty()) {
                avgRating = p.getComments()
                        .stream()
                        .filter(c -> c.getRating() != null && c.getRating() > 0)
                        .mapToDouble(Comment::getRating)
                        .average()
                        .orElse(0.0);
            }
            dto.setRating(avgRating);

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