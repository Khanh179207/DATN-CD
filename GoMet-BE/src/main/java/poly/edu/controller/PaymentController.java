package poly.edu.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.scheduling.annotation.Scheduled;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.HashMap;
import java.util.Map;
import java.time.LocalDateTime; // Đổi sang LocalDateTime để tính bằng Giây

@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = "*")
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
        // 🔥 BƯỚC MỚI: TÌM XEM CÒN HẠN KHÔNG ĐỂ CỘNG DỒN THỜI GIAN
        // ==========================================================
        LocalDateTime currentEndDate = null;
        try {
            String checkSql = "SELECT TOP 1 EndAt FROM Subscription WHERE AccountID = :accId AND isActive = 1 ORDER BY EndAt DESC";
            Object result = entityManager.createNativeQuery(checkSql)
                    .setParameter("accId", accountId)
                    .getSingleResult();

            if (result != null) {
                // Xử lý thông minh: Dù Driver trả về Timestamp, String hay LocalDateTime thì cũng parse được hết
                if (result instanceof java.time.LocalDateTime) {
                    currentEndDate = (java.time.LocalDateTime) result;
                } else if (result instanceof java.sql.Timestamp) {
                    currentEndDate = ((java.sql.Timestamp) result).toLocalDateTime();
                } else {
                    // Nếu trả về String dạng "2026-03-14 11:00:00.0"
                    String dateStr = result.toString();
                    if(dateStr.contains(" ")) {
                        dateStr = dateStr.replace(" ", "T"); // Format lại chuẩn ISO
                    }
                    currentEndDate = LocalDateTime.parse(dateStr);
                }
            }
        } catch (jakarta.persistence.NoResultException e) {
            // Không có gói nào đang active
        } catch (Exception ex) {
            System.err.println("Cảnh báo khi parse ngày: " + ex.getMessage());
        }

        // Nếu còn hạn: Thời gian bắt đầu tính = Ngày hết hạn cũ
        // Nếu hết hạn hoặc mua lần đầu: Thời gian bắt đầu tính = Hiện tại
        LocalDateTime startDate = (currentEndDate != null && currentEndDate.isAfter(LocalDateTime.now()))
                ? currentEndDate
                : LocalDateTime.now();

        LocalDateTime endDate;
        int amount = 0;

        // 2. Cộng thêm thời gian vào startDate
        if (planType == 0) {
            endDate = startDate.plusSeconds(10);
            amount = 10000;
        } else if (planType == 1) {
            endDate = startDate.plusMonths(1);
            amount = 50000;
        } else if (planType == 2) {
            endDate = startDate.plusYears(1);
            amount = 500000;
        } else {
            endDate = startDate.plusYears(99);
            amount = 999000;
        }

        // 3. Vô hiệu hóa các gói cũ đang chạy (Để hệ thống chỉ lấy 1 gói Active duy nhất làm chuẩn)
        String disableOldSubSql = "UPDATE Subscription SET isActive = 0 WHERE AccountID = :accId";
        entityManager.createNativeQuery(disableOldSubSql)
                .setParameter("accId", accountId)
                .executeUpdate();

        // 4. Thêm gói mới (Đã cộng dồn ngày) vào lịch sử Subscription
        String insertSubSql = "INSERT INTO Subscription (AccountID, PlanType, StartAt, EndAt, isActive) " +
                "VALUES (:accId, :planType, :start, :end, 1)";
        entityManager.createNativeQuery(insertSubSql)
                .setParameter("accId", accountId)
                .setParameter("planType", planType)
                .setParameter("start", startDate) // Lưu lại điểm mốc nối tiếp
                .setParameter("end", endDate)     // Ngày hết hạn cuối cùng
                .executeUpdate();

        // 5. GHI BIÊN LAI VÀO BẢNG PaymentTransaction
        try {
            String orderCode = "DEV_" + System.currentTimeMillis();
            String insertTxSql = "INSERT INTO PaymentTransaction (AccountID, OrderCode, Amount, PlanType, Status, CreatedAt, PaidAt) " +
                    "VALUES (:accId, :orderCode, :amount, :planType, 'PAID', :now, :now)";

            entityManager.createNativeQuery(insertTxSql)
                    .setParameter("accId", accountId)
                    .setParameter("orderCode", orderCode)
                    .setParameter("amount", amount)
                    .setParameter("planType", planType)
                    .setParameter("now", LocalDateTime.now()) // Biên lai thì vẫn phải lấy giờ hiện tại
                    .executeUpdate();

        } catch (Exception e) {
            System.err.println("Lỗi khi ghi lịch sử giao dịch: " + e.getMessage());
        }
    }
    // =================================================================
    // 2. JOB TỰ ĐỘNG HỦY GÓI (CHẾ ĐỘ TEST: Quét 5 giây/lần)
    // =================================================================
    @Scheduled(fixedRate = 5000) // ⏱️ Cứ 5 giây là chạy ngầm rà soát DB
    @Transactional
    public void autoCancelExpiredSubscriptions() {
        LocalDateTime now = LocalDateTime.now();

        try {
            // 1. Quét và tắt (isActive = 0) những gói đã quá hạn
            String updateSubSql = "UPDATE Subscription SET isActive = 0 WHERE EndAt < :now AND isActive = 1";
            int expiredCount = entityManager.createNativeQuery(updateSubSql)
                    .setParameter("now", now)
                    .executeUpdate();

            // 2. Hạ cấp (isPremium = 0) cho những User không còn gói nào đang Active
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
            // Lấy ra gói Subscription MỚI NHẤT đang Active của User này
            String sql = "SELECT TOP 1 EndAt FROM Subscription " +
                    "WHERE AccountID = :accId AND isActive = 1 " +
                    "ORDER BY EndAt DESC";

            Object result = entityManager.createNativeQuery(sql)
                    .setParameter("accId", accountId)
                    .getSingleResult();

            if (result != null) {
                return ResponseEntity.ok(Map.of(
                        "success", true,
                        "endAt", result.toString() // Trả về chuỗi thời gian (VD: 2026-03-14 12:00:00)
                ));
            } else {
                return ResponseEntity.ok(Map.of("success", false, "message", "Không tìm thấy gói Premium nào"));
            }
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of("success", false, "message", "User không có gói hoặc lỗi: " + e.getMessage()));
        }
    }
}