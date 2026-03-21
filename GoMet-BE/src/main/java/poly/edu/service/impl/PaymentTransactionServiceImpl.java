package poly.edu.service.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.NoResultException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import poly.edu.dao.PaymentTransactionDAO;
import poly.edu.entity.PaymentTransaction;

@Repository
@Transactional
public class PaymentTransactionServiceImpl implements PaymentTransactionDAO {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void create(PaymentTransaction pt) {
        entityManager.persist(pt);
    }

    @Override
    public void update(PaymentTransaction pt) {
        entityManager.merge(pt);
    }

    @Override
    public PaymentTransaction findByOrderCode(String orderCode) {
        try {
            String jpql = "SELECT p FROM PaymentTransaction p WHERE p.orderCode = :orderCode";
            return entityManager.createQuery(jpql, PaymentTransaction.class)
                    .setParameter("orderCode", orderCode)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null; // Không tìm thấy thì trả về null
        }
    }
}