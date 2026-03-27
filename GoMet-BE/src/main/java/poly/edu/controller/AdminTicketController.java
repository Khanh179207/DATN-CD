package poly.edu.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import poly.edu.dao.AccountDAO;
import poly.edu.dto.AdminTicketDTO;
import poly.edu.entity.Account;
import poly.edu.entity.Ticket;
import poly.edu.service.TicketService;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin/tickets")
@RequiredArgsConstructor
@CrossOrigin
public class AdminTicketController {

    private final TicketService ticketService;
    private final AccountDAO accountDAO; // 🔥 Thêm DAO để tìm Admin

    @GetMapping
    public ResponseEntity<List<AdminTicketDTO>> getAllTickets() {
        return ResponseEntity.ok(ticketService.getAllTickets());
    }

    @GetMapping("/pending")
    public ResponseEntity<List<AdminTicketDTO>> getPendingTickets() {
        return ResponseEntity.ok(ticketService.getTicketsByStatus(0));
    }

    @GetMapping("/resolved")
    public ResponseEntity<List<AdminTicketDTO>> getResolvedTickets() {
        return ResponseEntity.ok(ticketService.getTicketsByStatus(2));
    }

    /**
     * Admin cập nhật trạng thái Ticket (Duyệt/Hoàn thành/Từ chối)
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateTicketStatus(
            @PathVariable Integer id,
            @RequestParam Integer status,
            @RequestBody(required = false) Map<String, String> body, // Để nhận AdminNote từ FE
            HttpServletRequest request) { // 🔥 Lấy request để bóc Token
        try {
            // 1. "Tóm" lấy Admin đang login từ Token trong Database
            Account admin = getAdminFromToken(request);

            // 2. Lấy ghi chú nếu có gửi kèm từ Frontend
            String adminNote = (body != null) ? body.get("note") : null;

            // 3. Gọi Service xử lý (Nhớ bảo Hưng update tham số trong Service nhé)
            Ticket updatedTicket = ticketService.updateTicketStatus(
                    id,
                    status,
                    admin.getAccountID(),
                    admin.getUsername(),
                    adminNote
            );

            return ResponseEntity.ok(updatedTicket);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    /**
     * 🛠️ HELPER: Bóc Token tìm Admin thực tế (Copy-paste từ AppealController)
     */
    private Account getAdminFromToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Lỗi Xác thực: Không tìm thấy Token!");
        }
        String token = authHeader.substring(7);

        // Logic bốc User theo Token (Đồng bộ với AuthController)
        return accountDAO.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Token không hợp lệ hoặc đã hết hạn!"));
    }
}