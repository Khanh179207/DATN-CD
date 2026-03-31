package poly.edu.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import poly.edu.dto.AdminTransactionDTO;
import poly.edu.service.AdminTransactionService;

import java.util.List;

@RestController
@RequestMapping("/api/admin/transactions") // Link API này khớp 100% với Frontend em đưa nãy
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")

public class AdminTransactionController {

    private final AdminTransactionService adminTransactionService;

    @GetMapping
    public ResponseEntity<List<AdminTransactionDTO>> getAll() {
        try {
            List<AdminTransactionDTO> result = adminTransactionService.getAllTransactions();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build(); // Lỗi server 500
        }
    }
}