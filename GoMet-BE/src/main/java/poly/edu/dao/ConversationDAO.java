package poly.edu.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import poly.edu.entity.Conversation;
import java.util.List;
import java.util.Optional;

public interface ConversationDAO extends JpaRepository<Conversation, Integer> {

    // Phương thức này giúp lấy danh sách chat của một User (dùng cho Sidebar)
    // Tên phải chính xác để Spring Data JPA hiểu được các field lồng nhau
    List<Conversation> findByUserOneAccountIDOrUserTwoAccountID(Integer uid1, Integer uid2);

    // Phương thức cũ để tìm 1 cuộc hội thoại cụ thể giữa 2 người (đã sửa user1 -> userOne)
    @Query("SELECT c FROM Conversation c WHERE " +
            "(c.userOne.accountID = :u1 AND c.userTwo.accountID = :u2) OR " +
            "(c.userOne.accountID = :u2 AND c.userTwo.accountID = :u1)")
    Optional<Conversation> findBetweenUsers(@Param("u1") Integer u1, @Param("u2") Integer u2);
}