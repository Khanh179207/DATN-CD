package poly.edu.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import poly.edu.entity.Message;
import poly.edu.entity.Account;
import poly.edu.entity.MessageReaction;
import java.util.Optional;

public interface MessageReactionDAO extends JpaRepository<MessageReaction, Integer> {
    // Tìm xem user này đã thả cảm xúc cho tin nhắn này chưa
    Optional<MessageReaction> findByMessageAndUser(Message message, Account user);
}