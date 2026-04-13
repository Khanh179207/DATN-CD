package poly.edu.controller;

import poly.edu.service.NoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/notes")
@CrossOrigin("*")
public class NoteController {

    @Autowired
    private NoteService noteService;

    @GetMapping
    public ResponseEntity<String> getNote(@RequestParam Integer accountId, @RequestParam Integer postId) {
        return ResponseEntity.ok(noteService.getNoteContent(accountId, postId));
    }

    @PostMapping("/save")
    public ResponseEntity<String> save(@RequestBody Map<String, Object> payload) {
        Integer accId = (Integer) payload.get("accountId");
        Integer pId = (Integer) payload.get("postId");
        String content = (String) payload.get("content");

        noteService.saveOrUpdateNote(accId, pId, content);
        return ResponseEntity.ok("Saved!");
    }
}