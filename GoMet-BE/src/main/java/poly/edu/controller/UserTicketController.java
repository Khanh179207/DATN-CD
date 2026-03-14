package poly.edu.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import poly.edu.entity.Ticket;
import poly.edu.service.TicketService;
import poly.edu.entity.Account;
import poly.edu.entity.Post;

// 🔥 IMPORT THƯ VIỆN CLOUDINARY
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
@CrossOrigin
public class UserTicketController {

    private final TicketService ticketService;

    // 🔥 TIÊM CLOUDINARY
    private final Cloudinary cloudinary;

    @PostMapping("/create")
    public ResponseEntity<?> createTicket(
            @RequestParam("accountId") Integer accountId,
            @RequestParam("ticketType") String ticketType,
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam(value = "targetPostId", required = false) Integer targetPostId,
            @RequestParam(value = "attachment", required = false) MultipartFile file) {

        try {
            Ticket ticket = new Ticket();
            ticket.setTicketType(ticketType);
            ticket.setTitle(title);
            ticket.setDescription(description);
            ticket.setStatus(0); // Chờ duyệt
            ticket.setCreatedAt(LocalDateTime.now());

            // Gán Account
            Account account = new Account();
            account.setAccountID(accountId);
            ticket.setAccount(account);

            // Gán Post (Nếu có)
            if (targetPostId != null) {
                Post post = new Post();
                post.setPostID(targetPostId);
                ticket.setTargetPost(post);
            }

            // 🔥 TÍCH HỢP CLOUDINARY (Đã tối ưu)
            if (file != null && !file.isEmpty()) {
                // Đẩy file lên Cloudinary, lưu vào thư mục "gomet_tickets" cho gọn
                Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
                        "folder", "gomet_tickets",
                        "resource_type", "auto"
                ));

                // Lấy đường dẫn an toàn từ Cloudinary
                String imageUrl = uploadResult.get("secure_url").toString();

                // Lưu link xịn này vào Database
                ticket.setAttachment(imageUrl);
            }

            return ResponseEntity.ok(ticketService.createTicket(ticket));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Lỗi upload hoặc hệ thống: " + e.getMessage());
        }
    }
}