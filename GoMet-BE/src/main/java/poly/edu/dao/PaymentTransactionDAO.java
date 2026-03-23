package poly.edu.dao;

import poly.edu.entity.PaymentTransaction;
import java.util.List; // Nhớ import List

public interface PaymentTransactionDAO {
    void create(PaymentTransaction paymentTransaction);
    void update(PaymentTransaction paymentTransaction);
    PaymentTransaction findByOrderCode(String orderCode);

    // 🔥 THÊM ĐÚNG DÒNG NÀY CHO ADMIN: Lấy tất cả giao dịch mới nhất
    List<PaymentTransaction> findAllByOrderByCreatedAtDesc();
}