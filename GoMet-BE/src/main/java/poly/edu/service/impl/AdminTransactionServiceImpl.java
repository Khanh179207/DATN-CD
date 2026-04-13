package poly.edu.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import poly.edu.dao.PaymentTransactionDAO;
import poly.edu.dto.AdminTransactionDTO;
import poly.edu.entity.PaymentTransaction;
import poly.edu.service.AdminTransactionService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminTransactionServiceImpl implements AdminTransactionService {

    private final PaymentTransactionDAO paymentTransactionDAO;

    @Override
    public List<AdminTransactionDTO> getAllTransactions() {
        List<PaymentTransaction> transactions = paymentTransactionDAO.findAllByOrderByCreatedAtDesc();
        return transactions.stream().map(this::convertToAdminDTO).collect(Collectors.toList());
    }

    private AdminTransactionDTO convertToAdminDTO(PaymentTransaction txn) {
        AdminTransactionDTO dto = new AdminTransactionDTO();

        dto.setTransactionID(txn.getTransactionID());
        dto.setOrderCode(txn.getOrderCode());
        dto.setAmount(txn.getAmount() != null ? txn.getAmount() : 0);

        // Trạng thái gốc từ DB: PENDING, PAID, CANCELLED
        dto.setStatus(txn.getStatus() != null ? txn.getStatus().toUpperCase() : "PENDING");
        dto.setCreatedAt(txn.getCreatedAt());
        dto.setPaidAt(txn.getPaidAt());

// Dịch PlanType thành Tên Gói cho đồng bộ với FE (Gói 1 Tháng, 1 Năm, Trọn Đời)
        String planName = "Premium";
        if (txn.getPlanType() != null) {
            switch (txn.getPlanType()) {
                case 0: planName = "Gói Test 10s"; break;
                case 1: planName = "Premium 1 Tháng"; break;
                case 2: planName = "Premium 1 Năm"; break;
                case 3: planName = "Premium Vĩnh Viễn"; break; // Đã đổi từ Trọn Đời sang Vĩnh Viễn cho sang
                default: planName = "Nâng cấp Premium";
            }
        }
        dto.setPlanName(planName);

        // Lấy thông tin người thanh toán
        if (txn.getAccount() != null) {
            dto.setAccountID(txn.getAccount().getAccountID());
            dto.setUsername(txn.getAccount().getUsername());
            dto.setEmail(txn.getAccount().getEmail());
            dto.setAvatar(txn.getAccount().getAvatar());
        }

        return dto;
    }
}