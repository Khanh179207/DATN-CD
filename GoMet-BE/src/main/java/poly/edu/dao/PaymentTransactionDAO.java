package poly.edu.dao;

import poly.edu.entity.PaymentTransaction;

public interface PaymentTransactionDAO {
    void create(PaymentTransaction paymentTransaction);
    void update(PaymentTransaction paymentTransaction);
    PaymentTransaction findByOrderCode(String orderCode);
}