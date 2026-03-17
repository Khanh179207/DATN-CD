package poly.edu.service;

import poly.edu.dto.AdminTicketDTO;
import poly.edu.entity.Ticket;
import java.util.List;

public interface TicketService {
    Ticket createTicket(Ticket ticket);
    List<AdminTicketDTO> getTicketsByStatus(Integer status);
    Ticket updateTicketStatus(Integer ticketId, Integer newStatus);

    // 🔥 THÊM DÒNG NÀY
    List<AdminTicketDTO> getAllTickets();
}