package poly.edu.controller;

import poly.edu.dao.ShoppingListDAO;
import poly.edu.service.ShoppingListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; // 🔥 IMPORT THẺ BẢO VỆ
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/shopping")
@PreAuthorize("isAuthenticated()") // 🔥 CHỐT CHẶN VÀNG: Bắt buộc đăng nhập cho TẤT CẢ các thao tác đi chợ
public class ShoppingListController {

    @Autowired
    private ShoppingListService shoppingListService;

    // 🟡 USER: Xem giỏ hàng đi chợ của mình
    @GetMapping("/{accountId}")
    public ResponseEntity<List<ShoppingListDAO.CartItemDTO>> getCart(@PathVariable Integer accountId) {
        return ResponseEntity.ok(shoppingListService.getCartByUser(accountId));
    }

    // 🟡 USER: Thêm nguyên liệu từ bài viết vào giỏ hàng
    @PostMapping("/add")
    public ResponseEntity<?> addItems(@RequestBody Map<String, Object> payload) {
        Integer accountId = Integer.valueOf(payload.get("accountId").toString());
        Integer postId = payload.get("postId") != null ? Integer.valueOf(payload.get("postId").toString()) : null;
        List<String> ingredients = (List<String>) payload.get("ingredients");

        shoppingListService.addIngredients(accountId, postId, ingredients);
        return ResponseEntity.ok("Thành công!");
    }

    // 🟡 USER: Đánh dấu đã mua hoặc chưa mua cho một món
    @PutMapping("/toggle/{shoppingId}")
    public ResponseEntity<?> toggleStatus(@PathVariable Integer shoppingId) {
        shoppingListService.toggleItemStatus(shoppingId);
        return ResponseEntity.ok("Cập nhật thành công!");
    }

    // 🟡 USER: Dọn dẹp những món đã bỏ vào túi (đã mua)
    @DeleteMapping("/remove-bought/{accountId}")
    public ResponseEntity<?> removeBought(@PathVariable Integer accountId) {
        shoppingListService.removeBoughtItems(accountId);
        return ResponseEntity.ok("Xóa món đã mua!");
    }

    // 🟡 USER: Xóa trắng toàn bộ giỏ hàng
    @DeleteMapping("/clear-all/{accountId}")
    public ResponseEntity<?> clearAll(@PathVariable Integer accountId) {
        shoppingListService.clearAllItems(accountId);
        return ResponseEntity.ok("Làm sạch giỏ hàng!");
    }
}