package poly.edu.service;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import poly.edu.Utility.JwtResetPasswordUtil;
import poly.edu.dao.AccountDAO;
import poly.edu.dto.*;
import poly.edu.entity.Account;
import poly.edu.entity.OtpStore;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AccountDAO accountDAO;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final JwtResetPasswordUtil jwtUtil;

    @Value("${google.client-id}")
    private String googleClientId;

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

    // ================= LOGIN GOOGLE =================
    public LoginResponse loginGoogle(String idToken) {

        try {
            String url = "https://oauth2.googleapis.com/tokeninfo?id_token=" + idToken;
            RestTemplate restTemplate = new RestTemplate();

            Map<String, Object> googleUser =
                    restTemplate.getForObject(url, Map.class);

            System.out.println("GOOGLE RESPONSE = " + googleUser);

            if (googleUser == null) {
                throw new RuntimeException("Google không phản hồi");
            }

            // ✅ CHECK EMAIL
            String email = (String) googleUser.get("email");
            if (email == null) {
                throw new RuntimeException("Token không có email");
            }

            // ✅ CHECK AUDIENCE
            String aud = (String) googleUser.get("aud");
            if (!aud.equals("407408718192.apps.googleusercontent.com")) {
                throw new RuntimeException("Client ID không khớp");
            }

            Account acc = accountDAO.findByEmail(email).orElseGet(() -> {
                Account newAcc = new Account();
                newAcc.setEmail(email);
                newAcc.setUsername((String) googleUser.get("name"));
                newAcc.setAvatar((String) googleUser.get("picture"));
                newAcc.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
                newAcc.setIsAdmin(0);
                newAcc.setIsPremium(0);
                newAcc.setIsActive(1);
                newAcc.setPoint(0);
                newAcc.setCreatedAt(LocalDate.now());
                newAcc.setToken("GOOGLE_" + UUID.randomUUID());
                return accountDAO.save(newAcc);
            });

            String token = "LOGIN_" + UUID.randomUUID();
            acc.setToken(token);
            accountDAO.save(acc);

            return new LoginResponse(
                    token,
                    acc.getUsername(),
                    acc.getEmail()
            );

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Đăng nhập Google thất bại");
        }
    }

    // ================= FORGOT PASSWORD =================
    public void forgotPassword(ForgotPasswordRequest req) {

        Account acc = accountDAO.findByEmail(req.getEmail())
                .orElseThrow(() -> new RuntimeException("Email không tồn tại"));

        String token = jwtUtil.generateToken(acc.getEmail());

        String resetLink = "http://localhost:8080/reset-password?token=" + token;

        emailService.sendResetPassword(acc.getEmail(), resetLink);
    }

    // ================= RESET PASSWORD =================
    public void resetPassword(ResetPasswordRequest req) {

        if (!req.getNewPassword().equals(req.getConfirmPassword())) {
            throw new RuntimeException("Mật khẩu xác nhận không khớp");
        }

        // Validate JWT
        jwtUtil.validateToken(req.getToken());

        String email = jwtUtil.getEmail(req.getToken());

        Account acc = accountDAO.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Tài khoản không tồn tại"));

        acc.setPassword(passwordEncoder.encode(req.getNewPassword()));
        accountDAO.save(acc);
    }

}
