package poly.edu.dao;

import poly.edu.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MessageDAO extends JpaRepository<Message, Integer> {
    // Lấy tin nhắn cuối cùng dựa trên Conversation
    Message findTopByConversation_ConversationIDOrderByCreatedAtDesc(Integer conversationID);

    List<Message> findByConversation_ConversationIDOrderByCreatedAtAsc(Integer conversationID);
}