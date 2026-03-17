package poly.edu.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import poly.edu.dao.TicketDAO;
import poly.edu.dto.AdminTicketDTO;
import poly.edu.entity.Ticket;
import poly.edu.service.TicketService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TicketServiceImpl implements TicketService {

    private final TicketDAO ticketDAO;
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public Ticket createTicket(Ticket ticket) {
        ticket.setStatus(0); // 0 = Chờ xử lý
        ticket.setCreatedAt(LocalDateTime.now());
        Ticket savedTicket = ticketDAO.save(ticket);

        // Send admin alert for new ticket
        sendAdminAlert(savedTicket);

        return savedTicket;
    }

    @Override
    public List<AdminTicketDTO> getTicketsByStatus(Integer status) {
        return ticketDAO.findByStatus(status).stream()
                .map(this::convertToAdminDTO) // Dùng hàm helper chuẩn
                .collect(Collectors.toList());
    }

    @Override
    public List<AdminTicketDTO> getAllTickets() {
        // 🔥 FIX Ở ĐÂY: Lấy hết và dùng convertToAdminDTO để map cho an toàn
        return ticketDAO.findAll().stream()
                .map(this::convertToAdminDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Ticket updateTicketStatus(Integer ticketId, Integer newStatus) {
        Ticket ticket = ticketDAO.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));

        ticket.setStatus(newStatus);

        // 🔥 1. NẾU LÀ TIẾP NHẬN (Status = 1) -> Lưu giờ bắt đầu xử lý
        // Dùng điều kiện == null để lỡ sếp có bấm lại nút này nó cũng không bị ghi đè
        // giờ cũ
        if (newStatus == 1 && ticket.getProcessedAt() == null) {
            ticket.setProcessedAt(LocalDateTime.now());
        }

        // 🔥 2. NẾU LÀ GIẢI QUYẾT XONG (2) HOẶC TỪ CHỐI (3) -> Lưu giờ đóng Ticket
        if (newStatus == 2 || newStatus == 3) {
            ticket.setResolvedAt(LocalDateTime.now());
        }

        return ticketDAO.save(ticket);
    }

    // Hàm helper "vàng" - Giữ nguyên nhưng sếp check kỹ tên field trong DTO nhé
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
            // Check xem DTO của sếp là setTargetPostId hay setTargetPostID nhé
            dto.setTargetPostId(ticket.getTargetPost().getPostID());
        }

        return dto;
    }

    /**
     * Send admin alert for new ticket
     */
    private void sendAdminAlert(Ticket ticket) {
        try {
            AdminTicketDTO dto = convertToAdminDTO(ticket);
            messagingTemplate.convertAndSend("/topic/admin-alerts", dto);
        } catch (Exception e) {
            System.err.println("Failed to send admin alert: " + e.getMessage());
        }
    }
}