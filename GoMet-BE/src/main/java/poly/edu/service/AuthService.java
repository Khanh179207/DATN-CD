package poly.edu.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import poly.edu.dao.AccountDAO;
import poly.edu.dto.LoginRequest;
import poly.edu.dto.LoginResponse;
import poly.edu.dto.RegisterRequest;
import poly.edu.dto.VerifyOtpRequest;
import poly.edu.entity.Account;
import poly.edu.entity.OtpStore;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AccountDAO accountDAO;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    // ================= REGISTER =================
    public void register(RegisterRequest req) {

        if (!req.getPassword().equals(req.getConfirmPassword())) {
            throw new RuntimeException("Mật khẩu xác nhận không khớp");
        }

        if (accountDAO.findByEmail(req.getEmail()).isPresent()) {
            throw new RuntimeException("Email đã tồn tại");
        }

        // Sinh OTP
        String otp = String.valueOf((int) (Math.random() * 900000 + 100000));

        Account acc = new Account();
        acc.setUsername(req.getUsername());
        acc.setEmail(req.getEmail());
        acc.setPassword(passwordEncoder.encode(req.getPassword()));

        acc.setAvatar(null);
        acc.setPoint(0);
        acc.setIsAdmin(0);
        acc.setIsPremium(0);
        acc.setIsActive(0);

        acc.setCreatedAt(LocalDate.now());
        acc.setToken("REGISTER_" + UUID.randomUUID());

        accountDAO.save(acc);

        // ✅ LƯU OTP VÀO RAM
        OtpStore.save(acc.getEmail(), otp);

        emailService.sendOtp(acc.getEmail(), otp);
    }

    // ================= VERIFY OTP =================
    public void verifyOtp(VerifyOtpRequest req) {

        Account acc = accountDAO.findByEmail(req.getEmail())
                .orElseThrow(() -> new RuntimeException("Email không tồn tại"));

        var otpData = OtpStore.get(req.getEmail());

        if (otpData == null) {
            throw new RuntimeException("OTP không tồn tại hoặc đã hết hạn");
        }

        if (!otpData.otp.equals(req.getOtp().trim())) {
            throw new RuntimeException("OTP không đúng");
        }

        if (otpData.expiredAt.isBefore(LocalDateTime.now())) {
            OtpStore.remove(req.getEmail());
            throw new RuntimeException("OTP đã hết hạn");
        }

        // ✅ KÍCH HOẠT TÀI KHOẢN
        acc.setIsActive(1);
        accountDAO.save(acc);

        // ✅ XÓA OTP
        OtpStore.remove(req.getEmail());
    }

    // ================= LOGIN =================
    public LoginResponse login(LoginRequest req) {

        Account acc = accountDAO.findByEmail(req.getEmail())
                .orElseThrow(() -> new RuntimeException("Email không tồn tại"));

        if (acc.getIsActive() == 0) {
            throw new RuntimeException("Tài khoản chưa xác thực OTP");
        }

        if (!passwordEncoder.matches(req.getPassword(), acc.getPassword())) {
            throw new RuntimeException("Sai mật khẩu");
        }

        String token = "LOGIN_" + UUID.randomUUID();
        acc.setToken(token);
        accountDAO.save(acc);

        return new LoginResponse(
                token,
                acc.getUsername(),
                acc.getEmail()
        );
    }
}
