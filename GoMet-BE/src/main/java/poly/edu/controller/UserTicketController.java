package poly.edu.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import poly.edu.dao.AccountDAO;
// Nhớ đảm bảo sếp có class PostDAO nhé
import poly.edu.dao.PostDAO;
import poly.edu.entity.Account;
import poly.edu.entity.Post;
import poly.edu.entity.Ticket;
import poly.edu.service.TicketService;

@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
@CrossOrigin
public class UserTicketController {

    private final TicketService ticketService;
    private final AccountDAO accountDAO;
    private final PostDAO postDAO;

    @PostMapping
    public ResponseEntity<Ticket> createTicket(
            @RequestParam Integer accountId,
            @RequestParam String ticketType, // 'BUG', 'REPORT', 'FEEDBACK'
            @RequestParam(required = false) Integer targetPostId,
            @RequestParam String title,
            @RequestParam String description,
            @RequestParam(required = false) MultipartFile attachment) {

        Account account = accountDAO.findById(accountId).orElse(null);
        if (account == null) {
            return ResponseEntity.badRequest().build();
        }

        Ticket ticket = Ticket.builder()
                .account(account)
                .ticketType(ticketType.toUpperCase())
                .title(title)
                .description(description)
                .build();

        // Nếu là REPORT thì gắn TargetPost vào
        if ("REPORT".equalsIgnoreCase(ticketType) && targetPostId != null) {
            Post targetPost = postDAO.findById(targetPostId).orElse(null);
            ticket.setTargetPost(targetPost);
        }

        // Xử lý upload file (Lưu ý: Chỗ này sếp viết hàm upload Firebase/Cloudinary nhé)
        if (attachment != null && !attachment.isEmpty()) {
            ticket.setAttachment("uploaded_file_path.png");
        }

        Ticket savedTicket = ticketService.createTicket(ticket);
        return ResponseEntity.ok(savedTicket);
    }
}