package poly.edu.service;

import poly.edu.dto.TransactionAdminDTO;
import java.util.List;

public interface AdminTransactionService {
    // Hàm lấy danh sách hóa đơn cho màn hình Admin
    List<TransactionAdminDTO> getAllTransactions();
}