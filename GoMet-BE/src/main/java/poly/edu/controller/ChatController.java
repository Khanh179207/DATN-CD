package poly.edu.controller;

import poly.edu.dao.FollowDAO;
import poly.edu.dao.MessageDAO;
import poly.edu.dao.ConversationDAO;
import poly.edu.dao.AccountDAO;
import poly.edu.entity.Message;
import poly.edu.entity.Conversation;
import poly.edu.entity.Account;
import poly.edu.dto.ConversationDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map; // 🔥 Dùng Map để nhận dữ liệu Signaling linh hoạt
import org.springframework.http.ResponseEntity;

@RestController
@PreAuthorize("isAuthenticated()")
public class ChatController {

    @Autowired private SimpMessagingTemplate messagingTemplate;
    @Autowired private MessageDAO messageDAO;
    @Autowired private ConversationDAO conversationDAO;
    @Autowired private AccountDAO accountDAO;
    @Autowired private FollowDAO followDAO;

    @GetMapping("/api/conversations/user/{userId}")
    public List<ConversationDTO> getConversations(@PathVariable Integer userId) {
        List<Conversation> list = conversationDAO.findByUserOneAccountIDOrUserTwoAccountID(userId, userId);
        List<ConversationDTO> dtos = new ArrayList<>();

        for (Conversation c : list) {
            // 1. Xác định partner
            Account partner = c.getUserOne().getAccountID().equals(userId) ? c.getUserTwo() : c.getUserOne();

            // 2. Lấy tin nhắn cuối
            Message lastMsg = messageDAO.findTopByConversation_ConversationIDOrderByCreatedAtDesc(c.getConversationID());

            // 3. Check follow chéo
            boolean iFollowThem = followDAO.existsByFollower_AccountIDAndFollowee_AccountIDAndStatus(userId, partner.getAccountID(), 1);
            boolean theyFollowMe = followDAO.existsByFollower_AccountIDAndFollowee_AccountIDAndStatus(partner.getAccountID(), userId, 1);

            // 4. Đổ đúng 10 tham số vào Constructor
            dtos.add(new ConversationDTO(
                    c.getConversationID(),                             // 1. id (ConversationID)
                    partner.getAccountID(),                            // 2. partnerID (MỚI - Sếp thiếu cái này)
                    partner.getUsername(),                             // 3. name
                    partner.getAvatar(),                               // 4. avatar
                    lastMsg != null ? lastMsg.getContent() : "Chưa có tin nhắn", // 5. lastMessage
                    lastMsg != null ? "vừa xong" : "",                 // 6. time
                    lastMsg != null ? (lastMsg.getIsRead() == 1) : true, // 7. read
                    true,                                              // 8. online
                    iFollowThem,                                       // 9. isFollowing
                    theyFollowMe                                       // 10. isFollowed
            ));
        }
        return dtos;
    }

    // 2. API lấy lịch sử chat
    @GetMapping("/api/messages/{conversationId}")
    public ResponseEntity<?> getHistory(@PathVariable Integer conversationId) {
        List<Message> history = messageDAO.findByConversation_ConversationIDOrderByCreatedAtAsc(conversationId);
        return ResponseEntity.ok(history);
    }

    // 3. WebSocket xử lý gửi tin nhắn (GIỮ NGUYÊN)
    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload Message chatMessage) {
        try {
            chatMessage.setCreatedAt(new Date());
            chatMessage.setIsRead(0);
            if (chatMessage.getConversation() != null) {
                Integer convId = chatMessage.getConversation().getConversationID();
                conversationDAO.findById(convId).ifPresent(chatMessage::setConversation);
            }
            if (chatMessage.getSender() != null) {
                Integer senderId = chatMessage.getSender().getAccountID();
                accountDAO.findById(senderId).ifPresent(chatMessage::setSender);
            }

            Message savedMessage = messageDAO.save(chatMessage);
            messagingTemplate.convertAndSend("/topic/" + savedMessage.getConversation().getConversationID(), savedMessage);
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
                Integer cId = Integer.parseInt(conversationId);

                Conversation conv = conversationDAO.findById(cId).orElse(null);
                if (conv == null) return;

                Integer receiverId = conv.getUserOne().getAccountID().equals(senderId)
                        ? conv.getUserTwo().getAccountID()
                        : conv.getUserOne().getAccountID();

                if ("invite".equals(type)) {
                    boolean iFollowThem = followDAO.existsByFollower_AccountIDAndFollowee_AccountIDAndStatus(senderId, receiverId, 1);
                    boolean theyFollowMe = followDAO.existsByFollower_AccountIDAndFollowee_AccountIDAndStatus(receiverId, senderId, 1);

                    if (!iFollowThem || !theyFollowMe) {
                        System.err.println("❌ Chặn invite: Chưa follow chéo!");
                        return;
                    }

                    // 🔥 SỬA TẠI ĐÂY: Thêm (Object) để hết lỗi Ambiguous
                    messagingTemplate.convertAndSend("/topic/user/" + receiverId, (Object) signal);
                    System.out.println("🔔 Đang rung chuông máy User: " + receiverId);
                }

                // 🔥 Dòng này cũng nên có (Object) cho chắc sếp nhé
                messagingTemplate.convertAndSend("/topic/" + conversationId, (Object) signal);
                System.out.println("🚀 WebRTC Signal [" + type + "] -> Room: " + conversationId);
            }
        } catch (Exception e) {
            System.err.println("❌ Lỗi Signaling: " + e.getMessage());
        }
    }
    // 🚀 [THÊM MỚI]: API hỗ trợ Frontend check trạng thái nút gọi cực nhanh
    @GetMapping("/api/follows/check-mutual/{partnerId}")
    public ResponseEntity<?> checkMutualFollow(@PathVariable Integer partnerId, @RequestParam Integer myId) {
        boolean iFollowThem = followDAO.existsByFollower_AccountIDAndFollowee_AccountIDAndStatus(myId, partnerId, 1);
        boolean theyFollowMe = followDAO.existsByFollower_AccountIDAndFollowee_AccountIDAndStatus(partnerId, myId, 1);

        return ResponseEntity.ok(Map.of(
                "following", iFollowThem, // 🔥 Bỏ chữ 'is'
                "followed", theyFollowMe,  // 🔥 Bỏ chữ 'is'
                "canCall", (iFollowThem && theyFollowMe)
        ));
    }
}