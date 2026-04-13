package poly.edu.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import poly.edu.dao.AccountDAO;
import poly.edu.dao.PostDAO;
import poly.edu.dao.TicketDAO;
import poly.edu.dto.AdminTicketDTO;
import poly.edu.dto.TicketDTO;
import poly.edu.entity.Account;
import poly.edu.entity.Post;
import poly.edu.entity.Ticket;
import poly.edu.service.NotificationService;
import poly.edu.service.TicketService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TicketServiceImpl implements TicketService {

    private final TicketDAO ticketDAO;
    // 🔥 Giữ lại cả AccountDAO, PostDAO (của sếp) và SimpMessagingTemplate (của
    // develop)
    private final AccountDAO accountDAO;
    private final PostDAO postDAO;
    private final SimpMessagingTemplate messagingTemplate;
    private final NotificationService notificationService;

    /**
     * 🔥 HÀM ĐỒNG BỘ: Lưu Ticket từ DTO (FE up ảnh lấy link trước)
     */
    @Override
    public Ticket saveTicket(TicketDTO dto) {
        Ticket ticket = new Ticket();
        ticket.setTicketType(dto.getTicketType());
        ticket.setTitle(dto.getTitle());
        ticket.setDescription(dto.getDescription());
        ticket.setAttachment(dto.getAttachment()); // Link secure_url từ FE gửi về
        ticket.setStatus(0); // Chờ xử lý
        ticket.setCreatedAt(LocalDateTime.now());

        // 🔥 Logic của sếp: Tìm và gán Account (Sử dụng accountId từ DTO)
        if (dto.getAccountId() != null) {
            accountDAO.findById(dto.getAccountId())
                    .ifPresent(ticket::setAccount);
        }

        // 🔥 Logic của sếp: Tìm và gán bài viết bị báo cáo (Sử dụng targetPostId từ
        // DTO)
        if (dto.getTargetPostId() != null) {
            postDAO.findById(dto.getTargetPostId())
                    .ifPresent(ticket::setTargetPost);
        }

        // 🔥 Lưu ticket trước
        Ticket savedTicket = ticketDAO.save(ticket);

        // 🔥 Logic của develop: Send admin alert for new ticket
        sendAdminAlert(savedTicket);

        return savedTicket;
    }

    @Override
    public List<AdminTicketDTO> getTicketsByStatus(Integer status) {
        return ticketDAO.findByStatus(status).stream()
                .map(this::convertToAdminDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<AdminTicketDTO> getAllTickets() {
        return ticketDAO.findAll().stream()
                .map(this::convertToAdminDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Ticket updateTicketStatus(Integer ticketId, Integer newStatus) {
        Ticket ticket = ticketDAO.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Ticket!"));

        Integer oldStatus = ticket.getStatus();

        ticket.setStatus(newStatus);

        // 🔥 1. NẾU LÀ TIẾP NHẬN (Status = 1) -> Lưu giờ bắt đầu xử lý
        // Dùng điều kiện == null để lỡ sếp có bấm lại nút này nó cũng không bị ghi đè
        // giờ cũ (Giữ comment của develop)
        if (newStatus == 1 && ticket.getProcessedAt() == null) {
            ticket.setProcessedAt(LocalDateTime.now());
        }

        // 2. NẾU LÀ GIẢI QUYẾT XONG (2) HOẶC TỪ CHỐI (3) -> Lưu giờ đóng Ticket
        if (newStatus == 2 || newStatus == 3) {
            ticket.setResolvedAt(LocalDateTime.now());
        }

        Ticket savedTicket = ticketDAO.save(ticket);

        // Send real-time notification to ticket owner if status changed to
        // ACCEPTED/RESOLVED/REJECTED
        if (oldStatus != null && !oldStatus.equals(newStatus) && (newStatus == 1 || newStatus == 2 || newStatus == 3)) {
            try {
                String link = null;
                String title, content, type;

                if (newStatus == 1) {
                    title = "Ticket accepted";
                    content = "Your support ticket is now being processed.";
                    type = "TICKET_ACCEPTED";
                } else if (newStatus == 2) {
                    title = "Ticket resolved";
                    content = "Your support ticket has been resolved.";
                    type = "TICKET_RESOLVED";
                } else { // 3
                    title = "Ticket rejected";
                    content = "Your support ticket has been rejected by admin.";
                    type = "TICKET_REJECTED";
                }

                notificationService.createNotification(title, content, type, ticket.getAccount().getAccountID(), null,
                        null, link);
            } catch (Exception e) {
                System.err.println("Failed to send ticket status notification: " + e.getMessage());
            }
        }

        return savedTicket;
    }

    /**
     * Hàm helper chuyển đổi Entity sang DTO cho Admin xem
     */
    private AdminTicketDTO convertToAdminDTO(Ticket ticket) {
        AdminTicketDTO dto = new AdminTicketDTO();
        dto.setTicketID(ticket.getTicketID());
        dto.setTicketType(ticket.getTicketType());
        dto.setTitle(ticket.getTitle());
        dto.setDescription(ticket.getDescription());
        dto.setAttachment(ticket.getAttachment());
        dto.setStatus(ticket.getStatus());
        dto.setCreatedAt(ticket.getCreatedAt());
        dto.setResolvedAt(ticket.getResolvedAt());
        dto.setProcessedAt(ticket.getProcessedAt());

        if (ticket.getAccount() != null) {
            dto.setAccountID(ticket.getAccount().getAccountID());
            dto.setUsername(ticket.getAccount().getUsername());
            dto.setEmail(ticket.getAccount().getEmail());
        }

        if (ticket.getTargetPost() != null) {
            dto.setTargetPostId(ticket.getTargetPost().getPostID());
        }

        return dto;
    }

    /**
     * Send admin alert for new ticket using NotificationService
     */
    private void sendAdminAlert(Ticket ticket) {
        try {
            String userUsername = ticket.getAccount() != null ? ticket.getAccount().getUsername() : "Unknown User";
            notificationService.notifyAdminTicket(userUsername, ticket.getTicketID());
        } catch (Exception e) {
            System.err.println("Failed to notify admin about ticket: " + e.getMessage());
            e.printStackTrace();
        }
    }
}