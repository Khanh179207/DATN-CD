package poly.edu.controller;

import poly.edu.service.NoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; // 🔥 IMPORT THẺ BẢO VỆ
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/notes")
@PreAuthorize("isAuthenticated()") // 🔥 CHỐT CHẶN VÀNG: Khóa toàn bộ các API Ghi chú cá nhân
public class NoteController {

    @Autowired
    private NoteService noteService;

    // 🟡 USER: Lấy nội dung ghi chú cá nhân
    @GetMapping
    public ResponseEntity<String> getNote(@RequestParam Integer accountId, @RequestParam Integer postId) {
        return ResponseEntity.ok(noteService.getNoteContent(accountId, postId));
    }

    // 🟡 USER: Lưu hoặc cập nhật ghi chú cá nhân
    @PostMapping("/save")
    public ResponseEntity<String> save(@RequestBody Map<String, Object> payload) {
        Integer accId = (Integer) payload.get("accountId");
        Integer pId = (Integer) payload.get("postId");
        String content = (String) payload.get("content");

        noteService.saveOrUpdateNote(accId, pId, content);
        return ResponseEntity.ok("Saved!");
    }
}