package poly.edu.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import poly.edu.entity.Report;
import poly.edu.service.AdminReportService;
import java.util.List;

@RestController
@RequestMapping("/api/admin/reports")
@RequiredArgsConstructor
public class AdminReportController {

    private final AdminReportService adminReportService;

    @GetMapping
    public List<Report> getAll() {
        return adminReportService.findAll();
    }

    @DeleteMapping("/{id}")
    public void deleteReport(@PathVariable Integer id) {
        adminReportService.deleteReport(id);
    }

    @DeleteMapping("/post/{postId}")
    public void deletePost(@PathVariable Integer postId) {
        adminReportService.deletePostAndHandleReport(postId);
    }
}
