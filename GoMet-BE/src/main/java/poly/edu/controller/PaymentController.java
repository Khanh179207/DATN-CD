package poly.edu.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.scheduling.annotation.Scheduled;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.HashMap;
import java.util.Map;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/payments")

public class PaymentController {

    @PersistenceContext
    private EntityManager entityManager;

    // =================================================================
    // 1. API NÚT DEV: NÂNG CẤP TRỰC TIẾP
    // =================================================================
    @PostMapping("/force-upgrade")
    @Transactional
    public ResponseEntity<?> forceUpgrade(@RequestBody Map<String, Object> payload) {
        try {
            Integer accountId = Integer.parseInt(payload.get("accountId").toString());
            Integer planType = Integer.parseInt(payload.get("planType").toString());

            // Gọi hàm nâng cấp
            updateAccountToPremium(accountId, planType);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Đã nâng cấp Premium thành công!");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Lỗi: " + e.getMessage()));
        }
    }

    private void updateAccountToPremium(Integer accountId, Integer planType) {
        // 1. Cập nhật bảng Account
        String updateAccSql = "UPDATE Account SET isPremium = 1 WHERE AccountID = :accId";
        entityManager.createNativeQuery(updateAccSql)
                .setParameter("accId", accountId)
                .executeUpdate();

        // ==========================================================
        // BƯỚC MỚI: TÌM XEM CÒN HẠN KHÔNG ĐỂ CỘNG DỒN THỜI GIAN
        // ==========================================================
        LocalDateTime currentEndDate = null;
        try {
            String checkSql = "SELECT TOP 1 EndAt FROM Subscription WHERE AccountID = :accId AND isActive = 1 ORDER BY EndAt DESC";
            Object result = entityManager.createNativeQuery(checkSql)
                    .setParameter("accId", accountId)
                    .getSingleResult();

            if (result != null) {
                if (result instanceof java.time.LocalDateTime) {
                    currentEndDate = (java.time.LocalDateTime) result;
                } else if (result instanceof java.sql.Timestamp) {
                    currentEndDate = ((java.sql.Timestamp) result).toLocalDateTime();
                } else {
                    String dateStr = result.toString();
                    if(dateStr.contains(" ")) {
                        dateStr = dateStr.replace(" ", "T");
                    }
                    currentEndDate = LocalDateTime.parse(dateStr);
                }
            }
        } catch (jakarta.persistence.NoResultException e) {
            // Không có gói nào đang active
        } catch (Exception ex) {
            System.err.println("Cảnh báo khi parse ngày: " + ex.getMessage());
        }

        LocalDateTime startDate = (currentEndDate != null && currentEndDate.isAfter(LocalDateTime.now()))
                ? currentEndDate
                : LocalDateTime.now();

        LocalDateTime endDate;
        int amount = 0;

        // 2. Cộng thêm thời gian vào startDate và GÁN GIÁ TIỀN (Khớp với FE)
        if (planType == 0) {
            endDate = startDate.plusSeconds(10);
            amount = 1000;      // Gói Test 10s: 1.000đ
        } else if (planType == 1) {
            endDate = startDate.plusMonths(1);
            amount = 25000;     // Gói Tháng: 25.000đ
        } else if (planType == 2) {
            endDate = startDate.plusYears(1);
            amount = 100000;    // Gói Năm: 100.000đ
        } else {
            endDate = startDate.plusYears(99);
            amount = 1000000;   // Gói Trọn Đời: 1.000.000đ
        }

        // 3. Vô hiệu hóa các gói cũ đang chạy
        String disableOldSubSql = "UPDATE Subscription SET isActive = 0 WHERE AccountID = :accId";
        entityManager.createNativeQuery(disableOldSubSql)
                .setParameter("accId", accountId)
                .executeUpdate();

        // ==========================================================
        // 🔥 ĐẢO LẠI THỨ TỰ: TẠO HÓA ĐƠN TRƯỚC, RỒI MỚI GHI VÀO GÓI
        // ==========================================================
        Integer newTransactionId = null;

        // 4. GHI BIÊN LAI VÀO BẢNG PaymentTransaction
        try {
            String orderCode = "DEV_" + System.currentTimeMillis();
            // Dùng OUTPUT INSERTED.TransactionID để tóm ngay cái ID vừa tự động sinh ra
            String insertTxSql = "INSERT INTO PaymentTransaction (AccountID, OrderCode, Amount, PlanType, Status, CreatedAt, PaidAt) " +
                    "OUTPUT INSERTED.TransactionID " +
                    "VALUES (:accId, :orderCode, :amount, :planType, 'PAID', :now, :now)";

            Object txnResult = entityManager.createNativeQuery(insertTxSql)
                    .setParameter("accId", accountId)
                    .setParameter("orderCode", orderCode)
                    .setParameter("amount", amount)
                    .setParameter("planType", planType)
                    .setParameter("now", LocalDateTime.now())
                    .getSingleResult();

            // Ép kiểu an toàn để lấy ID
            if (txnResult != null) {
                newTransactionId = ((Number) txnResult).intValue();
            }

        } catch (Exception e) {
            System.err.println("Lỗi khi ghi lịch sử giao dịch: " + e.getMessage());
        }

        // 5. Thêm gói mới vào lịch sử Subscription (Kèm theo TransactionID vừa lấy được)
        try {
            String insertSubSql = "INSERT INTO Subscription (AccountID, PlanType, StartAt, EndAt, isActive, TransactionID) " +
                    "VALUES (:accId, :planType, :start, :end, 1, :txnId)";

            entityManager.createNativeQuery(insertSubSql)
                    .setParameter("accId", accountId)
                    .setParameter("planType", planType)
                    .setParameter("start", startDate)
                    .setParameter("end", endDate)
                    .setParameter("txnId", newTransactionId) // Liên kết khóa ngoại
                    .executeUpdate();
        } catch (Exception e) {
            System.err.println("Lỗi khi ghi Subscription: " + e.getMessage());
        }
    }

    // =================================================================
    // 2. JOB TỰ ĐỘNG HỦY GÓI (CHẾ ĐỘ TEST: Quét 5 giây/lần)
    // =================================================================
    @Scheduled(fixedRate = 5000)
    @Transactional
    public void autoCancelExpiredSubscriptions() {
        LocalDateTime now = LocalDateTime.now();

        try {
            String updateSubSql = "UPDATE Subscription SET isActive = 0 WHERE EndAt < :now AND isActive = 1";
            int expiredCount = entityManager.createNativeQuery(updateSubSql)
                    .setParameter("now", now)
                    .executeUpdate();

            if (expiredCount > 0) {
                String updateAccSql = "UPDATE Account SET isPremium = 0 WHERE AccountID NOT IN " +
                        "(SELECT AccountID FROM Subscription WHERE isActive = 1)";
                entityManager.createNativeQuery(updateAccSql).executeUpdate();
                System.out.println("CronJob: Đã tự động hủy " + expiredCount + " gói Premium hết hạn lúc " + now);
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi chạy Job hủy gói: " + e.getMessage());
        }
    }

    // =================================================================
    // 3. API TRA CỨU NGÀY HẾT HẠN PREMIUM
    // =================================================================
    @GetMapping("/check-expiry/{accountId}")
    public ResponseEntity<?> checkPremiumExpiry(@PathVariable Integer accountId) {
        try {
            String sql = "SELECT TOP 1 EndAt FROM Subscription " +
                    "WHERE AccountID = :accId AND isActive = 1 " +
                    "ORDER BY EndAt DESC";

            Object result = entityManager.createNativeQuery(sql)
                    .setParameter("accId", accountId)
                    .getSingleResult();

            if (result != null) {
                return ResponseEntity.ok(Map.of(
                        "success", true,
                        "endAt", result.toString()
                ));
            } else {
                return ResponseEntity.ok(Map.of("success", false, "message", "Không tìm thấy gói Premium nào"));
            }
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of("success", false, "message", "User không có gói hoặc lỗi: " + e.getMessage()));
        }
    }
}