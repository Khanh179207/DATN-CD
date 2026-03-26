package poly.edu.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import poly.edu.entity.ModerationLog;
import poly.edu.service.ModerationLogService; // Hoặc thư mục impl tùy sếp cấu hình

import java.util.List;

@RestController
@RequestMapping("/api/admin/moderation-logs")
@RequiredArgsConstructor
@CrossOrigin("*")
public class ModerationLogController {

    private final ModerationLogService moderationLogService;

    @GetMapping
    public ResponseEntity<List<ModerationLog>> getAll() {
        return ResponseEntity.ok(moderationLogService.getAllLogs());
    }
}