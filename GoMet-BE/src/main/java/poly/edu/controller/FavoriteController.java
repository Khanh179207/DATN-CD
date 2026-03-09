package poly.edu.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import poly.edu.dto.FavoriteDTO;
import poly.edu.service.FavoriteService;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/favorites")
@CrossOrigin("*")
public class FavoriteController {

    @Autowired
    private FavoriteService favoriteService;

    // Lưu bài viết
    @PostMapping("/add")
    public ResponseEntity<?> saveFavorite(@RequestBody Map<String, Integer> body) {

        Integer accountID = body.get("accountID");
        Integer postID = body.get("postID");

        favoriteService.saveFavorite(accountID, postID);

        return ResponseEntity.ok(Map.of(
                "message", "Saved successfully"
        ));
    }

    // Lấy danh sách bài viết đã lưu
    @GetMapping("/{accountID}")
    public ResponseEntity<List<FavoriteDTO>> getFavorites(@PathVariable Integer accountID) {

        List<FavoriteDTO> favorites = favoriteService.getFavorites(accountID);

        return ResponseEntity.ok(favorites);
    }

    // Bỏ lưu bài viết
    @DeleteMapping("/remove")
    public ResponseEntity<?> removeFavorite(
            @RequestParam Integer accountID,
            @RequestParam Integer postID) {

        favoriteService.removeFavorite(accountID, postID);

        return ResponseEntity.ok(Map.of(
                "message", "Removed successfully"
        ));
    }

    // Kiểm tra đã lưu chưa
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
