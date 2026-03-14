package poly.edu.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import poly.edu.dto.AdminTicketDTO;
import poly.edu.entity.Ticket;
import poly.edu.service.TicketService;

import java.util.List;

@RestController
@RequestMapping("/api/admin/tickets")
@RequiredArgsConstructor
@CrossOrigin
public class AdminTicketController {

    private final TicketService ticketService;

    // Lấy danh sách Ticket ĐANG CHỜ (Status = 0)
    @GetMapping("/pending")
    public ResponseEntity<List<AdminTicketDTO>> getPendingTickets() {
        List<AdminTicketDTO> tickets = ticketService.getTicketsByStatus(0);
        return ResponseEntity.ok(tickets);
    }

    // Lấy danh sách Ticket ĐÃ HOÀN THÀNH (Status = 2)
    @GetMapping("/resolved")
    public ResponseEntity<List<AdminTicketDTO>> getResolvedTickets() {
        List<AdminTicketDTO> tickets = ticketService.getTicketsByStatus(2);
        return ResponseEntity.ok(tickets);
    }

    // Admin cập nhật trạng thái (Ví dụ truyền ?status=1 hoặc ?status=2)
    @PutMapping("/{id}/status")
    public ResponseEntity<Ticket> updateTicketStatus(
            @PathVariable Integer id,
            @RequestParam Integer status) {
        try {
            Ticket ticket = ticketService.updateTicketStatus(id, status);
            return ResponseEntity.ok(ticket);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}