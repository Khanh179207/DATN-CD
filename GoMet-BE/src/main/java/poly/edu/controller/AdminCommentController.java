package poly.edu.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import poly.edu.dto.AdminCommentDTO;
import poly.edu.service.AdminCommentService;
import java.util.List;

@RestController
@RequestMapping("/api/admin/comments")
@RequiredArgsConstructor
@CrossOrigin
public class AdminCommentController {

    private final AdminCommentService adminCommentService;

    @GetMapping
    public List<AdminCommentDTO> getAll() {
        return adminCommentService.findAll();
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        adminCommentService.delete(id);
    }
}
