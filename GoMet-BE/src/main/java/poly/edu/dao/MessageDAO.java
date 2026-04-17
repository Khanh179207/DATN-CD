package poly.edu.dao;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import poly.edu.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MessageDAO extends JpaRepository<Message, Integer> {
    // Lấy tin nhắn cuối cùng dựa trên Conversation
    Message findTopByConversation_ConversationIDOrderByCreatedAtDesc(Integer conversationID);

    List<Message> findByConversation_ConversationIDOrderByCreatedAtAsc(Integer conversationID);
    @Modifying
    @Transactional
    // 🚀 THÊM ĐIỀU KIỆN: Chỉ đánh dấu đã đọc các tin nhắn do đối phương gửi (senderID != myId)
    @Query("UPDATE Message m SET m.isRead = 1 WHERE m.conversation.conversationID = :convId AND m.sender.accountID != :myId")
    void markAsRead(@Param("convId") Integer convId, @Param("myId") Integer myId);
}