package poly.edu.service;

import poly.edu.dto.AdminAccountDTO;

import java.util.List;

public interface AccountService {
    List<AdminAccountDTO> findAll();
    AdminAccountDTO findById(Integer id);
    AdminAccountDTO save(AdminAccountDTO dto);

    // 🔥 Yêu cầu truyền thêm người khóa và lý do
    void ban(Integer id, Integer adminId, String adminName, String adminEmail, String reason);
    void unban(Integer id, Integer adminId, String adminName); 
    void delete(Integer id);
    void hardDelete(Integer id);

    // ─────────────────────────────────────────────────────────────────────────
    // NEW: Quản lý Xóa mềm & Khôi phục (Self-Service)
    // ─────────────────────────────────────────────────────────────────────────
    
    /** Gửi OTP xác nhận Xóa tài khoản (Dành cho User đang đăng nhập) */
    void sendDeactivateOTP(Integer accountId);

    /** Thực hiện xóa mềm tài khoản */
    void verifyAndDeactivate(Integer accountId, String password, String otp);

    /** Gửi OTP xác nhận Khôi phục tài khoản (Dành cho User chưa đăng nhập) */
    void sendRestoreOTP(String email);

    /** Thực hiện khôi phục tài khoản */
    void verifyAndRestore(String email, String password, String otp);
}