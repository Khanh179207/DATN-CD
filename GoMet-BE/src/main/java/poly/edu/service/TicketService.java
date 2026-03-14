package poly.edu.service;

import poly.edu.dto.AdminTicketDTO;
import poly.edu.entity.Ticket;

import java.util.List;

public interface TicketService {

    Ticket createTicket(Ticket ticket);

    List<AdminTicketDTO> getAllUnreadTickets();

    Ticket markTicketAsRead(Integer ticketId);
}