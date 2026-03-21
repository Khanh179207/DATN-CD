package poly.edu.service;

import poly.edu.dto.AdminTicketDTO;
import poly.edu.dto.TicketDTO; // 🔥 Đảm bảo đã import TicketDTO
import poly.edu.entity.Ticket;
import java.util.List;

public interface TicketService {
    // 🔥 ĐỔI TÊN HÀM NÀY CHO KHỚP VỚI CONTROLLER
    Ticket saveTicket(TicketDTO dto);

    List<AdminTicketDTO> getTicketsByStatus(Integer status);
    Ticket updateTicketStatus(Integer ticketId, Integer newStatus);
    List<AdminTicketDTO> getAllTickets();
}