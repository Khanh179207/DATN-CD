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

    @GetMapping
    public ResponseEntity<List<AdminTicketDTO>> getAllUnreadTickets() {
        List<AdminTicketDTO> tickets = ticketService.getAllUnreadTickets();
        return ResponseEntity.ok(tickets);
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<Ticket> markTicketAsRead(@PathVariable Integer id) {
        try {
            Ticket ticket = ticketService.markTicketAsRead(id);
            return ResponseEntity.ok(ticket);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}