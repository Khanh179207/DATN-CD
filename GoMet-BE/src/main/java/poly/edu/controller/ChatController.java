package poly.edu.controller;

import poly.edu.dao.FollowDAO;
import poly.edu.dao.MessageDAO;
import poly.edu.dao.ConversationDAO;
import poly.edu.dao.AccountDAO;
import poly.edu.entity.Message;
import poly.edu.entity.Conversation;
import poly.edu.entity.Account;
import poly.edu.dto.ConversationDTO;
import poly.edu.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;

@RestController
@PreAuthorize("isAuthenticated()")
public class ChatController {

    @Autowired private SimpMessagingTemplate messagingTemplate;
    @Autowired private MessageDAO messageDAO;
    @Autowired private ConversationDAO conversationDAO;
    @Autowired private AccountDAO accountDAO;
    @Autowired private FollowDAO followDAO;

    // Đã tiêm Service vào để dọn dẹp Controller
    @Autowired private MessageService messageService;

    // Trong ChatController.java
    @GetMapping("/api/conversations/user/{userId}")
    public List<ConversationDTO> getConversations(@PathVariable Integer userId) {
        List<Conversation> list = conversationDAO.findByUserOneAccountIDOrUserTwoAccountID(userId, userId);
        List<ConversationDTO> dtos = new ArrayList<>();

        for (Conversation c : list) {
            Account partner = c.getUserOne().getAccountID().equals(userId) ? c.getUserTwo() : c.getUserOne();
            Message lastMsg = messageDAO.findTopByConversation_ConversationIDOrderByCreatedAtDesc(c.getConversationID());

            boolean iFollowThem = followDAO.existsByFollower_AccountIDAndFollowee_AccountIDAndStatus(userId, partner.getAccountID(), 1);
            boolean theyFollowMe = followDAO.existsByFollower_AccountIDAndFollowee_AccountIDAndStatus(partner.getAccountID(), userId, 1);

            // 🚀 KIỂM TRA ĐÃ ĐỌC:
            // Nếu tin nhắn cuối là CỦA MÌNH gửi -> Auto đánh dấu là đã đọc (không in đậm Sidebar)
            // Nếu tin nhắn cuối của NGƯỜI KHÁC -> Lấy trạng thái isRead từ Database
            boolean isRead = true;
            if (lastMsg != null) {
                if (lastMsg.getSender().getAccountID().equals(userId)) {
                    isRead = true;
                } else {
                    isRead = lastMsg.getIsRead() == 1;
                }
            }

            dtos.add(new ConversationDTO(
                    c.getConversationID(),
                    partner.getAccountID(),
                    partner.getUsername(),
                    partner.getAvatar(),
                    lastMsg != null ? lastMsg.getContent() : "Chưa có tin nhắn",

                    // 🚀 THAY ĐỔI: TRẢ VỀ GIỜ THỰC TẾ DƯỚI DẠNG CHUỖI THAY VÌ CHỮ "VỪA XONG"
                    lastMsg != null && lastMsg.getCreatedAt() != null ? lastMsg.getCreatedAt().toInstant().toString() : "",

                    isRead, // 🚀 TRẠNG THÁI ĐÃ XỬ LÝ LÔGIC Ở TRÊN
                    true, // isOnline (cái này sếp có vẻ đang fake cứng là true)
                    iFollowThem,
                    theyFollowMe
            ));
        }
        return dtos;
    }

    @GetMapping("/api/messages/{conversationId}")
    public ResponseEntity<?> getHistory(@PathVariable Integer conversationId) {
        List<Message> history = messageDAO.findByConversation_ConversationIDOrderByCreatedAtAsc(conversationId);
        return ResponseEntity.ok(history);
    }

    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload Message chatMessage) {
        try {
            chatMessage.setCreatedAt(new Date());
            chatMessage.setIsRead(0);

            if (chatMessage.getConversation() != null) {
                conversationDAO.findById(chatMessage.getConversation().getConversationID())
                        .ifPresent(chatMessage::setConversation);
            }
            if (chatMessage.getSender() != null) {
                accountDAO.findById(chatMessage.getSender().getAccountID())
                        .ifPresent(chatMessage::setSender);
            }

            Message savedMessage = messageDAO.save(chatMessage);

            // Bắn vào phòng chat chung (cho MiniChatBox)
            messagingTemplate.convertAndSend("/topic/" + savedMessage.getConversation().getConversationID(), savedMessage);

            Conversation conversation = savedMessage.getConversation();
            if (conversation != null) {
                // 🚀 BẮN CHO CẢ 2 ĐẦU (ĐỂ ĐỒNG BỘ SIDEBAR)
                if (conversation.getUserOne() != null && conversation.getUserOne().getAccountID() != null) {
                    messagingTemplate.convertAndSend(
                            "/topic/chat-user/" + conversation.getUserOne().getAccountID(),
                            savedMessage);
                }

                if (conversation.getUserTwo() != null && conversation.getUserTwo().getAccountID() != null) {
                    messagingTemplate.convertAndSend(
                            "/topic/chat-user/" + conversation.getUserTwo().getAccountID(),
                            savedMessage);
                }
            }

            // 🚀 THÊM MỚI: BẮN TÍN HIỆU "TOÀN CẦU" ĐỂ PHÁT ÂM THANH
            Integer senderId = savedMessage.getSender().getAccountID();
            Integer receiverId = conversation.getUserOne().getAccountID().equals(senderId)
                    ? conversation.getUserTwo().getAccountID()
                    : conversation.getUserOne().getAccountID();

            // Gửi ra kênh riêng của người nhận
            messagingTemplate.convertAndSend("/topic/global-chat/" + receiverId, savedMessage);

        } catch (Exception e) {
            System.err.println("❌ Lỗi gửi tin nhắn: " + e.getMessage());
        }
    }

    @MessageMapping("/call.signaling")
    public void handleSignaling(@Payload Map<String, Object> signal) {
        try {
            String type = String.valueOf(signal.get("type"));
            Object convIdObj = signal.get("conversationId");

            if (convIdObj != null) {
                String conversationId = String.valueOf(convIdObj);
                Integer senderId = Integer.parseInt(signal.get("senderId").toString());

                // 🚀 CASE 1: REACTION
                if ("reaction".equals(type)) {
                    Integer msgId = Integer.parseInt(signal.get("messageId").toString());
                    String emoji = String.valueOf(signal.get("emoji"));
                    messageService.saveReaction(msgId, senderId, emoji);
                    messagingTemplate.convertAndSend("/topic/" + conversationId, (Object) signal);
                    return;
                }

                // 🚀 CASE 2: UN-SEND (GỠ TIN NHẮN) - LƯU VÀO DB RỒI BÁO CHO MỌI NGƯỜI
                if ("unsend".equals(type)) {
                    Integer msgId = Integer.parseInt(signal.get("messageId").toString());
                    messageService.unsendMessage(msgId);
                    messagingTemplate.convertAndSend("/topic/" + conversationId, (Object) signal);
                    return;
                }

                // 🚀 CASE 3: TYPING & READ RECEIPT
                if ("typing".equals(type) || "stop_typing".equals(type) || "read_receipt".equals(type)) {
                    messagingTemplate.convertAndSend("/topic/" + conversationId, (Object) signal);
                    return;
                }

                // 🚀 CASE 4: VIDEO CALL INVITE
                if ("invite".equals(type)) {
                    Integer cId = Integer.parseInt(conversationId);
                    boolean canCall = messageService.checkMutualFollowForCall(cId, senderId);

                    if (!canCall) return; // Không đủ điều kiện gọi thì im lặng

                    // Nếu oke, bắn tín hiệu tới đích danh người nhận
                    Conversation conv = conversationDAO.findById(cId).orElse(null);
                    if (conv != null) {
                        Integer receiverId = conv.getUserOne().getAccountID().equals(senderId) ? conv.getUserTwo().getAccountID() : conv.getUserOne().getAccountID();
                        messagingTemplate.convertAndSend("/topic/user/" + receiverId, (Object) signal);
                    }
                    return;
                }

                // Các tín hiệu WebRTC khác (offer, answer, candidate, hangup)
                messagingTemplate.convertAndSend("/topic/" + conversationId, (Object) signal);
            }
        } catch (Exception e) {
            System.err.println("❌ Lỗi Signaling: " + e.getMessage());
        }
    }

    @GetMapping("/api/follows/check-mutual/{partnerId}")
    public ResponseEntity<?> checkMutualFollow(@PathVariable Integer partnerId, @RequestParam Integer myId) {
        boolean iFollowThem = followDAO.existsByFollower_AccountIDAndFollowee_AccountIDAndStatus(myId, partnerId, 1);
        boolean theyFollowMe = followDAO.existsByFollower_AccountIDAndFollowee_AccountIDAndStatus(partnerId, myId, 1);

        return ResponseEntity.ok(Map.of(
                "following", iFollowThem,
                "followed", theyFollowMe,
                "canCall", (iFollowThem && theyFollowMe)
        ));
    }

    @PutMapping("/api/messages/read/{conversationId}")
    public ResponseEntity<?> markMessagesAsRead(@PathVariable Integer conversationId, @RequestParam Integer myId) {
        try {
            messageDAO.markAsRead(conversationId, myId);
            return ResponseEntity.ok(Map.of("status", "success"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("status", "error"));
        }
    }
}
