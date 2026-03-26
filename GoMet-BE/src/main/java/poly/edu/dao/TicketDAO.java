package poly.edu.dao;

import poly.edu.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TicketDAO extends JpaRepository<Ticket, Integer> {

    // Tìm các ticket đang chờ xử lý (status = 0)
    List<Ticket> findByStatus(Integer status);

    // Tìm ticket theo loại
    List<Ticket> findByTicketType(String ticketType);

    // Ticket Summary: Nhóm theo loại lỗi
    @Query("SELECT t.ticketType, COUNT(t) FROM Ticket t GROUP BY t.ticketType")
    List<Object[]> countTicketsByType();
    // Đếm Ticket/Khiếu nại theo Trạng thái
    @Query(value = "SELECT t.Status, COUNT(t.TicketID) " +
            "FROM Ticket t GROUP BY t.Status", nativeQuery = true)
    List<Object[]> countTicketsByStatus();
}