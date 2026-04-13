package poly.edu.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; // 🔥 IMPORT THẺ BẢO VỆ
import org.springframework.web.bind.annotation.*;
import poly.edu.dto.FavoriteDTO;
import poly.edu.service.FavoriteService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/favorites")
@PreAuthorize("isAuthenticated()") // 🔥 CHỐT CHẶN VÀNG: Bắt buộc đăng nhập cho TẤT CẢ các hàm bên dưới
public class FavoriteController {

    @Autowired
    private FavoriteService favoriteService;

    // 🟡 USER: Lưu bài viết
    @PostMapping("/add")
    public ResponseEntity<?> saveFavorite(@RequestBody Map<String, Integer> body) {

        Integer accountID = body.get("accountID");
        Integer postID = body.get("postID");

        favoriteService.saveFavorite(accountID, postID);

        return ResponseEntity.ok(Map.of(
                "message", "Saved successfully"
        ));
    }

    // 🟡 USER: Lấy danh sách bài viết đã lưu
    @GetMapping("/{accountID}")
    public ResponseEntity<List<FavoriteDTO>> getFavorites(@PathVariable Integer accountID) {

        List<FavoriteDTO> favorites = favoriteService.getFavorites(accountID);

        return ResponseEntity.ok(favorites);
    }

    // 🟡 USER: Bỏ lưu bài viết
    @DeleteMapping("/remove")
    public ResponseEntity<?> removeFavorite(
            @RequestParam Integer accountID,
            @RequestParam Integer postID) {

        favoriteService.removeFavorite(accountID, postID);

        return ResponseEntity.ok(Map.of(
                "message", "Removed successfully"
        ));
    }

    // 🟡 USER: Kiểm tra đã lưu chưa
    @GetMapping("/check")
    public ResponseEntity<?> checkFavorite(
            @RequestParam Integer accountID,
            @RequestParam Integer postID) {

        boolean isFavorite = favoriteService.checkFavorite(accountID, postID);

        return ResponseEntity.ok(Map.of(
                "isFavorite", isFavorite
        ));
    }
}