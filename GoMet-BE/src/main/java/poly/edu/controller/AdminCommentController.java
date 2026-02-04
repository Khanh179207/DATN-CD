package poly.edu.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import poly.edu.dto.AdminCommentDTO;
import poly.edu.service.CommentService;
import java.util.List;

@RestController
@RequestMapping("/api/admin/comments")
@RequiredArgsConstructor
@CrossOrigin
public class AdminCommentController {

    private final CommentService commentService;

    @GetMapping
    public List<AdminCommentDTO> getAll() {
        return commentService.findAll();
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        commentService.delete(id);
    }
}
