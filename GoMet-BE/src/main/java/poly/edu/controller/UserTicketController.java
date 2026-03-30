package poly.edu.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import poly.edu.dto.TicketDTO; // Giả sử sếp có DTO này
import poly.edu.service.TicketService;

import java.util.Map;

@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor

public class UserTicketController {

    private final TicketService ticketService;

    /**
     * 🔥 ĐỒNG BỘ LUỒNG CHUẨN GOMET:
     * Frontend đã up ảnh lấy link xong, giờ chỉ gửi JSON chứa data + link ảnh về đây.
     */
    @PostMapping("/create")
    public ResponseEntity<?> createTicket(@RequestBody TicketDTO ticketDTO) {
        try {
            // Service sẽ lo việc chuyển DTO thành Entity và gán Account/Post
            return ResponseEntity.ok(ticketService.saveTicket(ticketDTO));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "error",
                    "message", "Lỗi tạo yêu cầu: " + e.getMessage()
            ));
        }
    }
}