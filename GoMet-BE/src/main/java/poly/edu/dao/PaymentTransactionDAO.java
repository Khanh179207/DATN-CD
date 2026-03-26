package poly.edu.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import poly.edu.entity.PaymentTransaction;

import java.util.List;
import java.util.Optional;

public interface PaymentTransactionDAO extends JpaRepository<PaymentTransaction, Integer> {

    // JPA tự động tạo câu SQL dựa trên tên hàm
    Optional<PaymentTransaction> findByOrderCode(String orderCode);

    List<PaymentTransaction> findAllByOrderByCreatedAtDesc();

    // ==========================================
    // CÁC HÀM THỐNG KÊ DOANH THU CHO ADMIN
    // ==========================================

    @Query("SELECT SUM(p.amount) FROM PaymentTransaction p WHERE p.status = 'PAID'")
    Long getTotalRevenue();

    @Query("SELECT COUNT(p) FROM PaymentTransaction p WHERE p.status = 'PAID'")
    long countSuccessfulTransactions();

    @Query("SELECT COUNT(p) FROM PaymentTransaction p WHERE p.status <> 'PAID'")
    long countFailedTransactions();

    @Query("SELECT COUNT(DISTINCT p.account) FROM PaymentTransaction p WHERE p.status = 'PAID'")
    long countPayingUsers();

    @Query("SELECT MONTH(p.paidAt), SUM(p.amount) FROM PaymentTransaction p WHERE p.status = 'PAID' AND YEAR(p.paidAt) = :year GROUP BY MONTH(p.paidAt)")
    List<Object[]> getRevenueByMonth(@Param("year") int year);
}