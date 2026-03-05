package poly.edu.dao;

import poly.edu.entity.ShoppingList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ShoppingListDAO extends JpaRepository<ShoppingList, Integer> {

    // Interface hứng dữ liệu gộp từ bảng ShoppingList và bảng Post
    public interface CartItemDTO {
        Integer getShoppingId();
        String getIngredientName();
        Integer getIsBought();
        String getPostTitle(); // Lấy tên món ăn từ bảng Post
    }

    // Câu SQL lấy Giỏ hàng + Tên món ăn (Luôn lấy tên mới nhất nếu Admin đổi)
    @Query(value = "SELECT s.ShoppingID as shoppingId, s.IngredientName as ingredientName, " +
            "s.IsBought as isBought, p.Title as postTitle " +
            "FROM ShoppingList s LEFT JOIN Post p ON s.PostID = p.PostID " +
            "WHERE s.AccountID = :accountId ORDER BY s.CreatedAt DESC",
            nativeQuery = true)
    List<CartItemDTO> getCartWithPostTitle(@Param("accountId") Integer accountId);

    Optional<ShoppingList> findByAccountIdAndIngredientName(Integer accountId, String ingredientName);
    void deleteByAccountIdAndIsBought(Integer accountId, Integer isBought);
    void deleteByAccountId(Integer accountId);
}