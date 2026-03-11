package poly.edu.service.impl;

import poly.edu.dao.ShoppingListDAO;
import poly.edu.entity.ShoppingList;
import poly.edu.service.ShoppingListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class ShoppingListServiceImpl implements ShoppingListService {

    @Autowired
    private ShoppingListDAO shoppingListDAO;

    @Override
    public List<ShoppingListDAO.CartItemDTO> getCartByUser(Integer accountId) {
        // Lấy giỏ hàng kèm theo TÊN MÓN ĂN (postTitle) luôn được cập nhật mới nhất
        return shoppingListDAO.getCartWithPostTitle(accountId);
    }

    @Override
    @Transactional
    public void addIngredients(Integer accountId, Integer postId, List<String> ingredients) {
        for (String ingredient : ingredients) {
            Optional<ShoppingList> exist = shoppingListDAO.findByAccountIdAndIngredientName(accountId, ingredient);
            if (exist.isEmpty()) {
                ShoppingList item = new ShoppingList();
                item.setAccountId(accountId);
                item.setPostId(postId); // Chỉ lưu PostID vào Database
                item.setIngredientName(ingredient);
                item.setIsBought(0);
                item.setCreatedAt(LocalDate.now());
                shoppingListDAO.save(item);
            }
        }
    }

    @Override
    @Transactional
    public void toggleItemStatus(Integer shoppingId) {
        shoppingListDAO.findById(shoppingId).ifPresent(item -> {
            item.setIsBought(item.getIsBought() == 1 ? 0 : 1);
            shoppingListDAO.save(item);
        });
    }

    @Override
    @Transactional
    public void removeBoughtItems(Integer accountId) {
        shoppingListDAO.deleteByAccountIdAndIsBought(accountId, 1);
    }

    @Override
    @Transactional
    public void clearAllItems(Integer accountId) {
        shoppingListDAO.deleteByAccountId(accountId);
    }
}