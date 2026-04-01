package poly.edu.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; // 🔥 IMPORT THẺ BẢO VỆ
import org.springframework.web.bind.annotation.*;
import poly.edu.dao.ConversationDAO;
import poly.edu.dao.AccountDAO; // Giả sử bạn dùng AccountDAO
import poly.edu.entity.Conversation;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/conversations")
@PreAuthorize("isAuthenticated()") // 🔥 CHỐT CHẶN VÀNG: Bắt buộc phải có Token mới được vào tạo phòng chat
public class ConversationController {

    @Autowired ConversationDAO convDao;
    @Autowired AccountDAO accDao;

    @PostMapping("/access")
    public ResponseEntity<?> access(@RequestBody Map<String, Integer> body) {
        Integer u1Id = body.get("user1Id");
        Integer u2Id = body.get("user2Id");

        return convDao.findBetweenUsers(u1Id, u2Id)
                .map(c -> {
                    Map<String, Object> res = new HashMap<>();
                    res.put("conversationID", c.getConversationID());
                    return ResponseEntity.ok(res);
                })
                .orElseGet(() -> {
                    Conversation newC = new Conversation();
                    // Sử dụng đúng tên hàm Setter theo Entity của bạn
                    newC.setUserOne(accDao.findById(u1Id).orElse(null));
                    newC.setUserTwo(accDao.findById(u2Id).orElse(null));
                    newC.setCreatedAt(new Date());

                    Conversation saved = convDao.save(newC);

                    Map<String, Object> res = new HashMap<>();
                    res.put("conversationID", saved.getConversationID());
                    return ResponseEntity.ok(res);
                });
    }
}