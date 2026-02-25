package poly.edu.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import poly.edu.dao.AccountDAO;
import poly.edu.dao.FavoriteDAO;
import poly.edu.dao.PostDAO;
import poly.edu.dto.PublicPostDTO;
import poly.edu.entity.Account;
import poly.edu.entity.Favorite;
import poly.edu.entity.Post;
import poly.edu.entity.Rating;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/favorites")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteDAO favoriteDAO;
    private final PostDAO postDAO;
    private final AccountDAO accountDAO;
    private final PostController postController;

    @GetMapping("/{accountID}")
    public ResponseEntity<List<PublicPostDTO>> getFavorites(@PathVariable Integer accountID) {
        List<PublicPostDTO> result = favoriteDAO.findByAccount_AccountID(accountID).stream()
                .filter(f -> f.getPost() != null
                        && f.getPost().getIsApproved() == 1
                        && f.getPost().getIsActive() == 1)
                .map(f -> postController.toPublicDTO(f.getPost()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    @PostMapping
    public ResponseEntity<?> addFavorite(
            @RequestParam Integer accountID,
            @RequestParam Integer postID) {
        if (favoriteDAO.existsByAccount_AccountIDAndPost_PostID(accountID, postID)) {
            return ResponseEntity.ok(Map.of("message", "Đã lưu trước đó"));
        }
        Post post = postDAO.findById(postID).orElse(null);
        Account account = accountDAO.findById(accountID).orElse(null);
        if (post == null || account == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Không tìm thấy"));
        }
        Favorite fav = Favorite.builder().account(account).post(post).build();
        favoriteDAO.save(fav);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "Đã lưu bài viết"));
    }

    @DeleteMapping
    public ResponseEntity<?> removeFavorite(
            @RequestParam Integer accountID,
            @RequestParam Integer postID) {
        favoriteDAO.findByAccount_AccountIDAndPost_PostID(accountID, postID).ifPresent(favoriteDAO::delete);
        return ResponseEntity.ok(Map.of("message", "Đã bỏ lưu bài viết"));
    }

    @GetMapping("/check")
    public ResponseEntity<?> checkFavorite(
            @RequestParam Integer accountID,
            @RequestParam Integer postID) {
        boolean isFav = favoriteDAO.existsByAccount_AccountIDAndPost_PostID(accountID, postID);
        return ResponseEntity.ok(Map.of("isFavorite", isFav));
    }
}
