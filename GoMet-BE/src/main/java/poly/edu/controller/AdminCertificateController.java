package poly.edu.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import poly.edu.dto.WeeklyCertificateDTO;
import poly.edu.dto.WeeklyLeaderboardEntryDTO;
import poly.edu.service.WeeklyCertificateService;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminCertificateController {

    private final WeeklyCertificateService weeklyCertificateService;

    @GetMapping("/leaderboard/weekly")
    public ResponseEntity<List<WeeklyLeaderboardEntryDTO>> getWeeklyLeaderboard(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate weekStart,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate weekEnd,
            @RequestParam(defaultValue = "10") int top) {
        return ResponseEntity.ok(weeklyCertificateService.getWeeklyLeaderboard(weekStart, weekEnd, top));
    }

    @GetMapping("/certificates")
    public ResponseEntity<List<WeeklyCertificateDTO>> getCertificates(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate weekStart,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate weekEnd) {
        return ResponseEntity.ok(weeklyCertificateService.getAdminCertificates(weekStart, weekEnd));
    }

    @PostMapping("/certificates/generate")
    public ResponseEntity<List<WeeklyCertificateDTO>> generateCertificates(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate weekStart,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate weekEnd,
            @RequestParam(defaultValue = "3") int top) {
        return ResponseEntity.ok(weeklyCertificateService.generateCertificates(weekStart, weekEnd, top));
    }
}
