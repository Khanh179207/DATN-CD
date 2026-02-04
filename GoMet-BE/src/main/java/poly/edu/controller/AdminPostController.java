package poly.edu.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import poly.edu.dto.AdminPostDTO;
import poly.edu.service.PostService;

import java.util.List;

@RestController
@RequestMapping("/api/admin/posts")
@RequiredArgsConstructor
@CrossOrigin
public class AdminPostController {

    private final PostService postService;

    // Tab Tất cả
    @GetMapping
    public List<AdminPostDTO> getAll() {
        return postService.findAll();
    }

    // Tab Đã duyệt / Chờ duyệt
    @GetMapping("/approved/{status}")
    public List<AdminPostDTO> getByApproved(@PathVariable Integer status) {
        return postService.findByApproved(status);
    }

    // Duyệt bài
    @PutMapping("/approve/{id}")
    public void approve(@PathVariable Integer id) {
        postService.approvePost(id);
    }

    // Deactive bài
    @PutMapping("/deactive/{id}")
    public void deactive(@PathVariable Integer id) {
        postService.deactivePost(id);
    }
}
