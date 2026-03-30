package poly.edu.controller;

import poly.edu.dao.MessageDAO;
import poly.edu.dao.ConversationDAO;
import poly.edu.dao.AccountDAO; // <-- THÊM MỚI: Import AccountDAO
import poly.edu.entity.Message;
import poly.edu.entity.Conversation;
import poly.edu.entity.Account;
import poly.edu.dto.ConversationDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date; // <-- THÊM MỚI: Import Date để set thời gian
import java.util.List;
import org.springframework.http.ResponseEntity;
// ... các import giữ nguyên

@RestController

public class ChatController {

    @Autowired private SimpMessagingTemplate messagingTemplate;
    @Autowired private MessageDAO messageDAO;
    @Autowired private ConversationDAO conversationDAO;
    @Autowired private AccountDAO accountDAO;

    // 1. API lấy danh sách cuộc trò chuyện (Sidebar)
    @GetMapping("/api/conversations/user/{userId}")
    public List<ConversationDTO> getConversations(@PathVariable Integer userId) {
        // Đảm bảo DAO đã có hàm này (không có dấu gạch dưới _)
        List<Conversation> list = conversationDAO.findByUserOneAccountIDOrUserTwoAccountID(userId, userId);
        List<ConversationDTO> dtos = new ArrayList<>();

        for (Conversation c : list) {
            // Xác định ai là người đang nhắn tin với mình
            Account partner = c.getUserOne().getAccountID().equals(userId) ? c.getUserTwo() : c.getUserOne();

            // Lấy tin nhắn cuối cùng để hiện bản xem trước
            Message lastMsg = messageDAO.findTopByConversation_ConversationIDOrderByCreatedAtDesc(c.getConversationID());

            dtos.add(new ConversationDTO(
                    c.getConversationID(),
                    partner.getUsername(),
                    partner.getAvatar(),
                    lastMsg != null ? lastMsg.getContent() : "Chưa có tin nhắn",
                    lastMsg != null ? "vừa xong" : "", // Sau này có thể dùng Relative Time
                    lastMsg != null ? (lastMsg.getIsRead() == 1) : true,
                    true // partnerOnline - Tạm thời để true, sau này dùng WebSocket check
            ));
        }
        return dtos;
    }

    // 2. API lấy lịch sử chat (MiniChatBox)
    @GetMapping("/api/messages/{conversationId}")
    public ResponseEntity<?> getHistory(@PathVariable Integer conversationId) {
        List<Message> history = messageDAO.findByConversation_ConversationIDOrderByCreatedAtAsc(conversationId);

        // Trả về ResponseEntity để kiểm soát dữ liệu tốt hơn
        // Spring sẽ tự xử lý JSON, nhưng hãy đảm bảo Entity Message đã có @JsonIgnore ở trường conversation
        return ResponseEntity.ok(history);
    }

    // 3. WebSocket xử lý gửi tin nhắn
    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload Message chatMessage) {
        try {
            // Thiết lập các giá trị mặc định
            chatMessage.setCreatedAt(new Date());
            chatMessage.setIsRead(0);

            // Tìm và gắn Object Conversation thực tế
            if (chatMessage.getConversation() != null) {
                Integer convId = chatMessage.getConversation().getConversationID();
                conversationDAO.findById(convId).ifPresent(chatMessage::setConversation);
            }

            // Tìm và gắn Object Sender thực tế
            if (chatMessage.getSender() != null) {
                Integer senderId = chatMessage.getSender().getAccountID();
                accountDAO.findById(senderId).ifPresent(chatMessage::setSender);
            }

            // Lưu vào DB
            Message savedMessage = messageDAO.save(chatMessage);

            // Bắn tin nhắn qua Socket cho cả 2 người trong phòng
            // Quan trọng: Gửi savedMessage đã có đầy đủ ID và thông tin Sender
            messagingTemplate.convertAndSend("/topic/" + savedMessage.getConversation().getConversationID(), savedMessage);

        } catch (Exception e) {
            System.err.println("❌ Lỗi gửi tin nhắn: " + e.getMessage());
        }
    }
}