package poly.edu.dao;

import poly.edu.entity.Ticket;
import poly.edu.dto.TicketHistoryDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TicketDAO extends JpaRepository<Ticket, Integer> {

    // 🔥 ĐÃ SỬA: Dùng t.description và t.adminNote khớp 100% với Entity Ticket
    @Query("SELECT new poly.edu.dto.TicketHistoryDTO(t.ticketID, t.title, t.description, t.status, t.createdAt, t.adminNote) " +
            "FROM Ticket t WHERE t.account.accountID = :accountId ORDER BY t.createdAt DESC")
    List<TicketHistoryDTO> findHistoryByAccountId(@Param("accountId") Integer accountId);

    // Các hàm cũ của Sếp giữ nguyên
    List<Ticket> findByAccount_AccountID(Integer accountId);
    List<Ticket> findByStatus(Integer status);
    List<Ticket> findByTicketType(String ticketType);

    @Query("SELECT t.ticketType, COUNT(t) FROM Ticket t GROUP BY t.ticketType")
    List<Object[]> countTicketsByType();

    @Query("SELECT t.status, COUNT(t) FROM Ticket t GROUP BY t.status")
    List<Object[]> countTicketsByStatus();
}