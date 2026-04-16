package poly.edu.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
import poly.edu.dao.MessageDAO;
import poly.edu.dao.MessageReactionDAO;
import poly.edu.dao.AccountDAO;
import poly.edu.dao.ConversationDAO;
import poly.edu.dao.FollowDAO;
import poly.edu.entity.Message;
import poly.edu.entity.MessageReaction;
import poly.edu.entity.Account;
import poly.edu.entity.Conversation;
import poly.edu.service.MessageService;

@Service
public class MessageServiceImpl implements MessageService {

    @Autowired private MessageDAO messageDAO;
    @Autowired private MessageReactionDAO reactionDAO;
    @Autowired private AccountDAO accountDAO;
    @Autowired private ConversationDAO conversationDAO;
    @Autowired private FollowDAO followDAO;

    @Override
    @Transactional
    public MessageReaction saveReaction(Integer msgId, Integer userId, String emoji) {
        Message msg = messageDAO.findById(msgId).orElse(null);
        Account user = accountDAO.findById(userId).orElse(null);

        if (msg != null && user != null) {
            MessageReaction reaction = reactionDAO.findByMessageAndUser(msg, user)
                    .orElse(new MessageReaction());
            reaction.setMessage(msg);
            reaction.setUser(user);
            reaction.setEmoji(emoji);
            return reactionDAO.save(reaction);
        }
        return null;
    }

    // 🚀 LOGIC GỠ TIN NHẮN: Đổi nội dung thành [UNSENT]
    @Override
    @Transactional
    public void unsendMessage(Integer msgId) {
        messageDAO.findById(msgId).ifPresent(msg -> {
            msg.setContent("[UNSENT]");
            // Nếu muốn triệt để, xóa luôn các reaction của tin nhắn này
            if (msg.getReactions() != null) {
                reactionDAO.deleteAll(msg.getReactions());
                msg.getReactions().clear();
            }
            messageDAO.save(msg);
        });
    }

    // 🚀 LOGIC KIỂM TRA ĐIỀU KIỆN GỌI VIDEO
    @Override
    public boolean checkMutualFollowForCall(Integer conversationId, Integer senderId) {
        Conversation conv = conversationDAO.findById(conversationId).orElse(null);
        if (conv == null) return false;

        Integer receiverId = conv.getUserOne().getAccountID().equals(senderId)
                ? conv.getUserTwo().getAccountID()
                : conv.getUserOne().getAccountID();

        boolean iFollowThem = followDAO.existsByFollower_AccountIDAndFollowee_AccountIDAndStatus(senderId, receiverId, 1);
        boolean theyFollowMe = followDAO.existsByFollower_AccountIDAndFollowee_AccountIDAndStatus(receiverId, senderId, 1);

        return iFollowThem && theyFollowMe;
    }
}