package poly.edu.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.scheduling.annotation.Scheduled;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import poly.edu.config.VnPayConfig;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    @PersistenceContext
    private EntityManager entityManager;

    // ==========================================
    // 1. TẠO LINK THANH TOÁN
    // ==========================================
    @PostMapping("/create-vnpay-payment")
    @Transactional
    public ResponseEntity<?> createPayment(HttpServletRequest req, @RequestBody Map<String, Object> payload) {
        try {
            Integer accountId = Integer.parseInt(payload.get("accountId").toString());
            Integer planType = Integer.parseInt(payload.get("planType").toString());

            long amount;
            String configKey = switch (planType) {
                case 0 -> "PREMIUM_PRICE_TEST";
                case 1 -> "PREMIUM_PRICE_1_MONTH";
                case 2 -> "PREMIUM_PRICE_12_MONTHS";
                case 3 -> "PREMIUM_PRICE_LIFETIME";
                default -> "PREMIUM_PRICE_1_MONTH";
            };

            try {
                String sqlGetPrice = "SELECT ConfigValue FROM SystemConfig WHERE ConfigKey = :key";
                String priceStr = (String) entityManager.createNativeQuery(sqlGetPrice)
                        .setParameter("key", configKey)
                        .getSingleResult();
                amount = Long.parseLong(priceStr);
            } catch (Exception e) {
                amount = switch (planType) {
                    case 0 -> 1000;
                    case 1 -> 49000;
                    case 2 -> 399000;
                    case 3 -> 999000;
                    default -> 49000;
                };
            }

            String vnp_TxnRef = VnPayConfig.getRandomNumber(8) + "_" + System.currentTimeMillis();
            String insertTxSql = "INSERT INTO PaymentTransaction (AccountID, OrderCode, Amount, PlanType, Status, CreatedAt) " +
                    "VALUES (:accId, :orderCode, :amount, :planType, 'PENDING', :now)";
            entityManager.createNativeQuery(insertTxSql)
                    .setParameter("accId", accountId)
                    .setParameter("orderCode", vnp_TxnRef)
                    .setParameter("amount", (int)amount)
                    .setParameter("planType", planType)
                    .setParameter("now", LocalDateTime.now())
                    .executeUpdate();

            Map<String, String> vnp_Params = new TreeMap<>();
            vnp_Params.put("vnp_Version", "2.1.0");
            vnp_Params.put("vnp_Command", "pay");
            vnp_Params.put("vnp_TmnCode", VnPayConfig.vnp_TmnCode);
            vnp_Params.put("vnp_Amount", String.valueOf(amount * 100));
            vnp_Params.put("vnp_CurrCode", "VND");
            vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
            vnp_Params.put("vnp_OrderInfo", "ThanhToanPremium");
            vnp_Params.put("vnp_OrderType", "other");
            vnp_Params.put("vnp_Locale", "vn");
            vnp_Params.put("vnp_ReturnUrl", VnPayConfig.vnp_ReturnUrl);
            vnp_Params.put("vnp_IpAddr", "127.0.0.1");

            Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
            vnp_Params.put("vnp_CreateDate", formatter.format(cld.getTime()));

            StringJoiner joiner = new StringJoiner("&");
            for (Map.Entry<String, String> entry : vnp_Params.entrySet()) {
                String value = entry.getValue();
                if (value != null && !value.isEmpty()) {
                    String encodedKey = URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8.toString());
                    String encodedValue = URLEncoder.encode(value, StandardCharsets.UTF_8.toString()).replace("+", "%20");
                    joiner.add(encodedKey + "=" + encodedValue);
                }
            }

            String queryUrl = joiner.toString();
            String vnp_SecureHash = VnPayConfig.hmacSHA512(VnPayConfig.vnp_HashSecret, queryUrl);
            String paymentUrl = VnPayConfig.vnp_PayUrl + "?" + queryUrl + "&vnp_SecureHash=" + vnp_SecureHash;

            return ResponseEntity.ok(Map.of("url", paymentUrl));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    // ==========================================
    // 2. CALLBACK
    // ==========================================
    @GetMapping("/vnpay-callback")
    @Transactional
    public void vnpayCallback(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String vnp_ResponseCode = request.getParameter("vnp_ResponseCode");
        String vnp_TxnRef = request.getParameter("vnp_TxnRef");
        String vnp_Amount = request.getParameter("vnp_Amount");

        if ("00".equals(vnp_ResponseCode)) {
            Integer realPlanType = 1;
            try {
                // Cập nhật PAID
                entityManager.createNativeQuery("UPDATE PaymentTransaction SET Status = 'PAID', PaidAt = :now WHERE OrderCode = :code")
                        .setParameter("now", LocalDateTime.now())
                        .setParameter("code", vnp_TxnRef)
                        .executeUpdate();

                // Dùng Number để tránh lỗi ép kiểu Long -> Integer
                String findSql = "SELECT TransactionID, AccountID, PlanType FROM PaymentTransaction WHERE OrderCode = :code";
                List<?> results = entityManager.createNativeQuery(findSql)
                        .setParameter("code", vnp_TxnRef)
                        .getResultList();

                if (!results.isEmpty()) {
                    Object[] row = (Object[]) results.get(0);
                    // Ép kiểu an toàn (Safe Casting)
                    Integer transId = ((Number) row[0]).intValue();
                    Integer accId = ((Number) row[1]).intValue();
                    realPlanType = ((Number) row[2]).intValue();

                    updateAccountToPremium(accId, realPlanType, transId);
                }
            } catch (Exception e) {
                System.err.println("Lỗi DB Callback: " + e.getMessage());
            }

            response.sendRedirect("http://localhost:5173/payment-success?status=success"
                    + "&vnp_Amount=" + vnp_Amount
                    + "&vnp_TxnRef=" + vnp_TxnRef
                    + "&planType=" + realPlanType);
        } else {
            try {
                entityManager.createNativeQuery("UPDATE PaymentTransaction SET Status = 'FAILED' WHERE OrderCode = :code")
                        .setParameter("code", vnp_TxnRef)
                        .executeUpdate();
            } catch (Exception e) {}
            response.sendRedirect("http://localhost:5173/payment-success?status=cancel&vnp_TxnRef=" + vnp_TxnRef);
        }
    }

    // ==========================================
    // 3. LOGIC NÂNG CẤP
    // ==========================================
    private void updateAccountToPremium(Integer accountId, Integer planType, Integer transactionId) {
        Object currentStatusObj = entityManager.createNativeQuery(
                        "SELECT isPremium FROM Account WHERE AccountID = :accId")
                .setParameter("accId", accountId)
                .getSingleResult();

        int currentIsPremium = (currentStatusObj != null) ? ((Number) currentStatusObj).intValue() : 0;
        LocalDateTime currentEndDate = null;

        if (currentIsPremium == 1) {
            try {
                String checkSql = "SELECT TOP 1 EndAt FROM Subscription WHERE AccountID = :accId AND isActive = 1 ORDER BY EndAt DESC";
                Object result = entityManager.createNativeQuery(checkSql).setParameter("accId", accountId).getSingleResult();
                if (result != null) {
                    if (result instanceof java.time.LocalDateTime) currentEndDate = (java.time.LocalDateTime) result;
                    else if (result instanceof java.sql.Timestamp) currentEndDate = ((java.sql.Timestamp) result).toLocalDateTime();
                }
            } catch (Exception e) {}
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startDate = (currentIsPremium == 1 && currentEndDate != null && currentEndDate.isAfter(now))
                ? currentEndDate : now;

        LocalDateTime endDate = switch (planType) {
            case 0 -> startDate.plusSeconds(15);
            case 1 -> startDate.plusMonths(1);
            case 2 -> startDate.plusYears(1);
            case 3 -> startDate.plusYears(99);
            default -> startDate.plusMonths(1);
        };

        entityManager.createNativeQuery("UPDATE Account SET isPremium = 1 WHERE AccountID = :accId")
                .setParameter("accId", accountId).executeUpdate();

        entityManager.createNativeQuery("UPDATE Subscription SET isActive = 0 WHERE AccountID = :accId")
                .setParameter("accId", accountId).executeUpdate();

        entityManager.createNativeQuery("INSERT INTO Subscription (AccountID, PlanType, StartAt, EndAt, isActive, TransactionID) " +
                        "VALUES (:accId, :planType, :start, :end, 1, :transId)")
                .setParameter("accId", accountId)
                .setParameter("planType", planType)
                .setParameter("start", startDate)
                .setParameter("end", endDate)
                .setParameter("transId", transactionId)
                .executeUpdate();
    }

    // ==========================================
    // 4. GIẢ LẬP
    // ==========================================
    @PostMapping("/force-upgrade")
    @Transactional
    public ResponseEntity<?> forceUpgrade(@RequestBody Map<String, Object> payload) {
        try {
            Integer accId = Integer.parseInt(payload.get("accountId").toString());
            Integer pType = Integer.parseInt(payload.get("planType").toString());

            String mockOrderCode = "DEV_MOCK_" + System.currentTimeMillis();
            String insertMockSql = "INSERT INTO PaymentTransaction (AccountID, OrderCode, Amount, PlanType, Status, CreatedAt, PaidAt) " +
                    "VALUES (:accId, :code, 0, :pType, 'PAID', :now, :now)";

            entityManager.createNativeQuery(insertMockSql)
                    .setParameter("accId", accId)
                    .setParameter("code", mockOrderCode)
                    .setParameter("pType", pType)
                    .setParameter("now", LocalDateTime.now())
                    .executeUpdate();

            Object newIdObj = entityManager.createNativeQuery("SELECT TransactionID FROM PaymentTransaction WHERE OrderCode = :code")
                    .setParameter("code", mockOrderCode)
                    .getSingleResult();

            // Ép kiểu an toàn
            Integer mockTransId = ((Number) newIdObj).intValue();

            updateAccountToPremium(accId, pType, mockTransId);

            return ResponseEntity.ok(Map.of("success", true, "message", "Giả lập thành công với mã giao dịch: " + mockOrderCode));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    @Scheduled(fixedRate = 10000)
    @Transactional
    public void autoCancelExpiredSubscriptions() {
        LocalDateTime now = LocalDateTime.now();
        try {
            entityManager.createNativeQuery("UPDATE Subscription SET isActive = 0 WHERE EndAt < :now AND isActive = 1").setParameter("now", now).executeUpdate();
            entityManager.createNativeQuery("UPDATE Account SET isPremium = 0 WHERE AccountID NOT IN (SELECT AccountID FROM Subscription WHERE isActive = 1)").executeUpdate();
        } catch (Exception e) {}
    }

    @GetMapping("/check-expiry/{accountId}")
    public ResponseEntity<?> checkPremiumExpiry(@PathVariable Integer accountId) {
        try {
            Object result = entityManager.createNativeQuery("SELECT TOP 1 EndAt FROM Subscription WHERE AccountID = :accId AND isActive = 1 ORDER BY EndAt DESC")
                    .setParameter("accId", accountId).getSingleResult();
            return ResponseEntity.ok(Map.of("success", true, "endAt", result.toString()));
        } catch (Exception e) { return ResponseEntity.ok(Map.of("success", false)); }
    }
}