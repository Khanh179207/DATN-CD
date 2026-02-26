package poly.edu.service;

import poly.edu.dao.ShoppingListDAO;
import java.util.List;

public interface ShoppingListService {
    List<ShoppingListDAO.CartItemDTO> getCartByUser(Integer accountId);
    void addIngredients(Integer accountId, Integer postId, List<String> ingredients);
    void toggleItemStatus(Integer shoppingId);
    void removeBoughtItems(Integer accountId);
    void clearAllItems(Integer accountId);
}