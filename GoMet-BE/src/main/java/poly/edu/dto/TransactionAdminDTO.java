package poly.edu.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionAdminDTO {
    private Integer transactionID;  // ID gốc của bảng (Int)
    private String orderCode;       // Mã giao dịch (VD: GOMET9999)
    private Integer amount;         // Số tiền (Int)
    private String planName;        // Tên gói (Dịch từ PlanType ra)
    private String status;          // PENDING, PAID, CANCELLED
    private LocalDateTime createdAt;// Ngày tạo bill
    private LocalDateTime paidAt;   // Ngày thanh toán thành công

    // Lấy từ bảng Account qua AccountID
    private Integer accountID;
    private String username;
    private String email;
    private String avatar;
}