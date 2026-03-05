package poly.edu.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import poly.edu.dto.ReportSummaryDTO;
import poly.edu.service.AdminReportService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/reports")
@RequiredArgsConstructor
@CrossOrigin
public class AdminReportController {

    private final AdminReportService adminReportService;

    @GetMapping
    public ResponseEntity<List<ReportSummaryDTO>> getAll() {
        return ResponseEntity.ok(adminReportService.findAllSummaries());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteReport(@PathVariable Integer id) {
        adminReportService.deleteReport(id);
        return ResponseEntity.ok(Map.of("message", "Report deleted"));
    }

    @DeleteMapping("/post/{postId}")
    public ResponseEntity<?> deletePost(@PathVariable Integer postId) {
        adminReportService.deletePostAndHandleReport(postId);
        return ResponseEntity.ok(Map.of("message", "Post and reports deleted"));
    }
}
