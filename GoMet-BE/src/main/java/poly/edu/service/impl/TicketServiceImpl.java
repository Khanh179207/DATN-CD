package poly.edu.service.impl;

import lombok.RequiredArgsConstructor;
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

    @Override
    public Ticket createTicket(Ticket ticket) {
        ticket.setIsRead(false);
        ticket.setCreatedAt(LocalDateTime.now());
        return ticketDAO.save(ticket);
    }

    @Override
    public List<AdminTicketDTO> getAllUnreadTickets() {
        return ticketDAO.findByIsReadFalse().stream()
                .map(this::convertToAdminDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Ticket markTicketAsRead(Integer ticketId) {
        Ticket ticket = ticketDAO.findById(ticketId).orElseThrow(() -> new RuntimeException("Ticket not found"));
        ticket.setIsRead(true);
        return ticketDAO.save(ticket);
    }

    private AdminTicketDTO convertToAdminDTO(Ticket ticket) {
        AdminTicketDTO dto = new AdminTicketDTO();
        dto.setTicketID(ticket.getTicketID());
        dto.setTitle(ticket.getTitle());
        dto.setDescription(ticket.getDescription());
        dto.setAttachment(ticket.getAttachment());
        dto.setIsRead(ticket.getIsRead());
        dto.setCreatedAt(ticket.getCreatedAt());

        if (ticket.getAccount() != null) {
            dto.setAccountID(ticket.getAccount().getAccountID());
            dto.setUsername(ticket.getAccount().getUsername());
            dto.setEmail(ticket.getAccount().getEmail());
        }

        return dto;
    }
}