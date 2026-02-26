package poly.edu.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ShoppingList")
public class ShoppingList {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ShoppingID")
    private Integer shoppingId;

    @Column(name = "AccountID")
    private Integer accountId;

    @Column(name = "PostID") // Đã đổi thành PostID
    private Integer postId;

    @Column(name = "IngredientName")
    private String ingredientName;

    @Column(name = "IsBought")
    private Integer isBought;

    @Column(name = "CreatedAt")
    private LocalDate createdAt;
}