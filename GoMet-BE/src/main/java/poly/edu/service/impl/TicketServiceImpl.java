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

    @Override
    public Ticket saveTicket(TicketDTO dto) {
        Ticket ticket = new Ticket();
        ticket.setTicketType(dto.getTicketType());
        ticket.setTitle(dto.getTitle());
        ticket.setDescription(dto.getDescription());
        ticket.setAttachment(dto.getAttachment());
        ticket.setStatus(0);
        ticket.setCreatedAt(LocalDateTime.now());

        if (dto.getAccountId() != null) {
            accountDAO.findById(dto.getAccountId()).ifPresent(ticket::setAccount);
        }

        // 🔥 Logic của sếp: Tìm và gán bài viết bị báo cáo (Sử dụng targetPostId từ
        // DTO)
        if (dto.getTargetPostId() != null) {
            postDAO.findById(dto.getTargetPostId()).ifPresent(ticket::setTargetPost);
        }

        Ticket savedTicket = ticketDAO.save(ticket);
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

    /**
     * 🔥 HÀM ĐÃ NÂNG CẤP: Lưu chủ quyền của sếp (Admin)
     */
    @Override
    public Ticket updateTicketStatus(Integer ticketId, Integer newStatus, Integer adminId, String adminName,
            String adminNote) {
        Ticket ticket = ticketDAO.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Ticket!"));

        Integer oldStatus = ticket.getStatus();

        // 1. Cập nhật trạng thái và thông tin người xử lý
        ticket.setStatus(newStatus);
        ticket.setAdminId(adminId);
        ticket.setAdminName(adminName);
        ticket.setAdminNote(adminNote);

        // 2. Logic thời gian
        if (newStatus == 1 && ticket.getProcessedAt() == null) {
            ticket.setProcessedAt(LocalDateTime.now());
        }

        if (newStatus == 2 || newStatus == 3) {
            ticket.setResolvedAt(LocalDateTime.now());
        }

        Ticket savedTicket = ticketDAO.save(ticket);

        // 3. Gửi thông báo Real-time cho User
        if (oldStatus != null && !oldStatus.equals(newStatus)) {
            sendUserStatusNotification(savedTicket, newStatus);
        }

        return savedTicket;
    }

    /**
     * Helper: Chuyển đổi Entity sang DTO (Đã thêm thông tin Admin xử lý để FE hiển
     * thị)
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

        // 🔥 GỬI THÔNG TIN ADMIN XỬ LÝ VỀ CHO FRONTEND
        dto.setAdminId(ticket.getAdminId());
        dto.setAdminName(ticket.getAdminName());
        dto.setAdminNote(ticket.getAdminNote());

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

    private void sendUserStatusNotification(Ticket ticket, Integer newStatus) {
        try {
            String title, content, type;
            if (newStatus == 1) {
                title = "Ticket accepted";
                content = "Your support ticket #" + ticket.getTicketID() + " is now being processed.";
                type = "TICKET_ACCEPTED";
            } else if (newStatus == 2) {
                title = "Ticket resolved";
                content = "Your support ticket #" + ticket.getTicketID() + " has been resolved.";
                type = "TICKET_RESOLVED";
            } else {
                title = "Ticket rejected";
                content = "Your support ticket #" + ticket.getTicketID() + " has been rejected.";
                type = "TICKET_REJECTED";
            }
            if (ticket.getAccount() != null) {
                notificationService.createNotification(title, content, type, ticket.getAccount().getAccountID(), null,
                        null, null);
            } else {
                System.err
                        .println("sendUserStatusNotification: Ticket has no account, skip user notification for ticket "
                                + ticket.getTicketID());
            }
        } catch (Exception e) {
            System.err.println("Failed to send notification: " + e.getMessage());
        }
    }

    private void sendAdminAlert(Ticket ticket) {
        try {
            String userUsername = ticket.getAccount() != null ? ticket.getAccount().getUsername() : "Unknown User";
            notificationService.notifyAdminTicket(userUsername, ticket.getTicketID());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}