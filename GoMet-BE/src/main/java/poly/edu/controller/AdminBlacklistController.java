package poly.edu.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import poly.edu.dao.BlacklistWordDAO;
import poly.edu.entity.BlacklistWord;

import java.util.List;
import java.util.Map;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/admin/blacklist")
public class AdminBlacklistController {

    @Autowired
    private BlacklistWordDAO blacklistWordDAO;

    @GetMapping
    public ResponseEntity<List<BlacklistWord>> getAllWords() {
        return ResponseEntity.ok(blacklistWordDAO.findAll());
    }

    @PostMapping
    public ResponseEntity<?> addWord(@RequestBody Map<String, String> payload) {
        String word = payload.get("word");
        if (word == null || word.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Từ khóa không hợp lệ"));
        }
        BlacklistWord entity = new BlacklistWord();
        entity.setWord(word.trim());
        try {
            return ResponseEntity.ok(blacklistWordDAO.save(entity));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", "Từ khóa đã tồn tại"));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteWord(@PathVariable Integer id) {
        if (blacklistWordDAO.existsById(id)) {
            blacklistWordDAO.deleteById(id);
            return ResponseEntity.ok(Map.of("message", "Đã xóa"));
        }
        return ResponseEntity.notFound().build();
    }
}