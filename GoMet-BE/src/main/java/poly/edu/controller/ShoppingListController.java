package poly.edu.controller;

import poly.edu.dao.ShoppingListDAO;
import poly.edu.service.ShoppingListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/shopping")
@CrossOrigin("*")
public class ShoppingListController {

    @Autowired
    private ShoppingListService shoppingListService;

    // Trả về danh sách có chứa `postTitle`
    @GetMapping("/{accountId}")
    public ResponseEntity<List<ShoppingListDAO.CartItemDTO>> getCart(@PathVariable Integer accountId) {
        return ResponseEntity.ok(shoppingListService.getCartByUser(accountId));
    }

    @PostMapping("/add")
    public ResponseEntity<?> addItems(@RequestBody Map<String, Object> payload) {
        Integer accountId = Integer.valueOf(payload.get("accountId").toString());
        Integer postId = payload.get("postId") != null ? Integer.valueOf(payload.get("postId").toString()) : null;
        List<String> ingredients = (List<String>) payload.get("ingredients");

        shoppingListService.addIngredients(accountId, postId, ingredients);
        return ResponseEntity.ok("Thành công!");
    }

    @PutMapping("/toggle/{shoppingId}")
    public ResponseEntity<?> toggleStatus(@PathVariable Integer shoppingId) {
        shoppingListService.toggleItemStatus(shoppingId);
        return ResponseEntity.ok("Cập nhật thành công!");
    }

    @DeleteMapping("/remove-bought/{accountId}")
    public ResponseEntity<?> removeBought(@PathVariable Integer accountId) {
        shoppingListService.removeBoughtItems(accountId);
        return ResponseEntity.ok("Xóa món đã mua!");
    }

    @DeleteMapping("/clear-all/{accountId}")
    public ResponseEntity<?> clearAll(@PathVariable Integer accountId) {
        shoppingListService.clearAllItems(accountId);
        return ResponseEntity.ok("Làm sạch giỏ hàng!");
    }
}