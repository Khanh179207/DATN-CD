package poly.edu.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import poly.edu.dto.AdminPostDTO;
import poly.edu.service.AdminPostService;

import java.util.List;

@RestController
@RequestMapping("/api/admin/posts")
@RequiredArgsConstructor
@CrossOrigin
public class AdminPostController {

    private final AdminPostService adminPostService;

    // Tab Tất cả
    @GetMapping
    public List<AdminPostDTO> getAll() {
        return adminPostService.findAll();
    }

    // Tab Đã duyệt / Chờ duyệt
    @GetMapping("/approved/{status}")
    public List<AdminPostDTO> getByApproved(@PathVariable Integer status) {
        return adminPostService.findByApproved(status);
    }

    // Duyệt bài
    @PutMapping("/approve/{id}")
    public void approve(@PathVariable Integer id) {
        adminPostService.approvePost(id);
    }

    // Deactive bài
    @PutMapping("/deactive/{id}")
    public void deactive(@PathVariable Integer id) {
        adminPostService.deactivePost(id);
    }
}
