package poly.edu.service;

import poly.edu.entity.MessageReaction;
import java.util.Map;

public interface MessageService {
    // 🚀 Hàm xử lý thả cảm xúc (Upsert logic)
    MessageReaction saveReaction(Integer msgId, Integer userId, String emoji);

    // 🚀 THÊM MỚI: Hàm xử lý gỡ tin nhắn
    void unsendMessage(Integer msgId);

    // 🚀 THÊM MỚI: Hàm kiểm tra follow chéo để gọi video
    boolean checkMutualFollowForCall(Integer conversationId, Integer senderId);
}