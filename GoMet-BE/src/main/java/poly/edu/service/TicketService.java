package poly.edu.service;

import poly.edu.dto.AdminTicketDTO;
import poly.edu.dto.TicketDTO;
import poly.edu.entity.Ticket;
import java.util.List;

public interface TicketService {
    Ticket saveTicket(TicketDTO dto);
    List<AdminTicketDTO> getTicketsByStatus(Integer status);
    List<AdminTicketDTO> getAllTickets();

    // 🔥 CẬP NHẬT: Thêm 3 tham số để lưu vết Admin
    Ticket updateTicketStatus(Integer ticketId, Integer newStatus, Integer adminId, String adminName, String adminNote);
}