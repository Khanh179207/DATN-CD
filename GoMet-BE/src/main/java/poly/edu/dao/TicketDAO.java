package poly.edu.dao;

import poly.edu.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TicketDAO extends JpaRepository<Ticket, Integer> {

    // Tìm các ticket đang chờ xử lý (status = 0)
    List<Ticket> findByStatus(Integer status);

    // (Tùy chọn thêm) Tìm ticket theo loại
    List<Ticket> findByTicketType(String ticketType);
}