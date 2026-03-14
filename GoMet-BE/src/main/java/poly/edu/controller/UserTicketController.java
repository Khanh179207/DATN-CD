package poly.edu.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import poly.edu.dao.AccountDAO;
import poly.edu.entity.Account;
import poly.edu.entity.Ticket;
import poly.edu.service.TicketService;

@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
@CrossOrigin
public class UserTicketController {

    private final TicketService ticketService;
    private final AccountDAO accountDAO;

    @PostMapping
    public ResponseEntity<Ticket> createTicket(
            @RequestParam String title,
            @RequestParam String description,
            @RequestParam(required = false) MultipartFile attachment,
            @RequestParam Integer accountId) {

        Account account = accountDAO.findById(accountId).orElse(null);
        if (account == null) {
            return ResponseEntity.badRequest().build();
        }

        Ticket ticket = Ticket.builder()
                .account(account)
                .title(title)
                .description(description)
                .isRead(false)
                .build();

        // Handle attachment if provided
        if (attachment != null && !attachment.isEmpty()) {
            // You might want to save the file and set the path
            // For now, just set a placeholder
            ticket.setAttachment("uploaded_file_path");
        }

        Ticket savedTicket = ticketService.createTicket(ticket);
        return ResponseEntity.ok(savedTicket);
    }
}