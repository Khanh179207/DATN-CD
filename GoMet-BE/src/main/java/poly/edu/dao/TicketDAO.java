package poly.edu.dao;

import poly.edu.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TicketDAO extends JpaRepository<Ticket, Integer> {

    List<Ticket> findByIsReadFalse();
}