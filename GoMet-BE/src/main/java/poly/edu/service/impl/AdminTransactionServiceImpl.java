package poly.edu.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import poly.edu.dao.PaymentTransactionDAO;
import poly.edu.dto.TransactionAdminDTO;
import poly.edu.entity.PaymentTransaction;
import poly.edu.service.AdminTransactionService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminTransactionServiceImpl implements AdminTransactionService {

    private final PaymentTransactionDAO paymentTransactionDAO;

    @Override
    public List<TransactionAdminDTO> getAllTransactions() {
        List<PaymentTransaction> transactions = paymentTransactionDAO.findAllByOrderByCreatedAtDesc();
        return transactions.stream().map(this::convertToAdminDTO).collect(Collectors.toList());
    }

    private TransactionAdminDTO convertToAdminDTO(PaymentTransaction txn) {
        TransactionAdminDTO dto = new TransactionAdminDTO();

        dto.setTransactionID(txn.getTransactionID());
        dto.setOrderCode(txn.getOrderCode());
        dto.setAmount(txn.getAmount() != null ? txn.getAmount() : 0);

        // Trạng thái gốc từ DB: PENDING, PAID, CANCELLED
        dto.setStatus(txn.getStatus() != null ? txn.getStatus().toUpperCase() : "PENDING");
        dto.setCreatedAt(txn.getCreatedAt());
        dto.setPaidAt(txn.getPaidAt());

        // Dịch PlanType thành Tên Gói cho đồng bộ với FE
        String planName = "Nâng cấp Premium";
        if (txn.getPlanType() != null) {
            if (txn.getPlanType() == 0) planName = "Gói Test 10s";
            else if (txn.getPlanType() == 1) planName = "Gói Tháng";
            else if (txn.getPlanType() == 2) planName = "Gói Năm";
            else if (txn.getPlanType() == 3) planName = "Trọn Đời";
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