package poly.edu.service;

import poly.edu.dto.AdminTransactionDTO;
import java.util.List;

public interface AdminTransactionService {
    // Hàm lấy danh sách hóa đơn cho màn hình Admin
    List<AdminTransactionDTO> getAllTransactions();
}