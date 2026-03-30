package poly.edu.controller;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import poly.edu.dao.AccountDAO;
import poly.edu.dto.*;
import poly.edu.entity.Account;
import poly.edu.service.EmailService;
import poly.edu.service.GoogleAuthService;
import poly.edu.service.OtpStore;
import poly.edu.service.PasswordResetService;
import poly.edu.util.JwtUtils; // 🔥 IMPORT CỖ MÁY IN TOKEN VÀO ĐÂY

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AccountDAO           accountDAO;
    private final EmailService         emailService;
    private final OtpStore             otpStore;
    private final PasswordResetService passwordResetService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final GoogleAuthService    googleAuthService;
    private final JwtUtils             jwtUtils; // 🔥 NHÚNG JWT UTILS VÀO

    // ==========================================
    // 🔥 HELPER: TẠO RESPONSE KHI BỊ KHÓA (CLEAN CODE)
    // ==========================================
    private Map<String, Object> buildBannedResponse(Account acc) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("message", "ACCOUNT_BANNED");
        errorResponse.put("banReason", acc.getBanReason());
        errorResponse.put("bannedAt", acc.getBannedAt() != null ? acc.getBannedAt().toString() : null);
        return errorResponse;
    }

    // ─── GOOGLE LOGIN & REGISTER ──────────────────────────────────────────────
    @PostMapping("/google")
    public ResponseEntity<?> googleLogin(@RequestBody Map<String, String> request) {
        try {
            String idTokenString = request.get("token");

            if (idTokenString == null || idTokenString.isBlank()) {
                return ResponseEntity.badRequest().body(Map.of("message", "ID Token is missing"));
            }

            // 1. Xác thực token với nhà Google
            GoogleIdToken.Payload payload = googleAuthService.verifyToken(idTokenString);

            // 2. Lấy thông tin
            String email = payload.getEmail();
            String name = (String) payload.get("name");
            String avatarUrl = (String) payload.get("picture");

            Optional<Account> opt = accountDAO.findByEmail(email);
            Account acc;

            if (opt.isPresent()) {
                acc = opt.get();
                // 🔥 CHỐT CHẶN 1: CẤM USER BỊ BAN ĐĂNG NHẬP
                if (acc.getIsActive() != null && acc.getIsActive() == 0) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(buildBannedResponse(acc));
                }

                // 🔥 TẠO JWT TOKEN THAY VÌ LƯU DB
                String role = acc.getIsAdmin() == 1 ? "ADMIN" : "USER";
                String jwtToken = jwtUtils.generateJwtToken(acc.getEmail(), acc.getAccountID(), role);

                return ResponseEntity.ok(buildResponse(acc, jwtToken));

            } else {
                // TÌNH HUỐNG 2: CHƯA CÓ TÀI KHOẢN -> TỰ ĐỘNG ĐĂNG KÝ
                String finalUsername = name.replaceAll("\\s+", "");
                if (accountDAO.findByUsername(finalUsername).isPresent()) {
                    finalUsername = finalUsername + "_" + new Random().nextInt(10000);
                }

                String randomPassword = passwordEncoder.encode(UUID.randomUUID().toString());

                acc = Account.builder()
                        .username(finalUsername)
                        .email(email)
                        .password(randomPassword)
                        .avatar(avatarUrl)
                        .point(0)
                        .isAdmin(0)
                        .isPremium(0)
                        .isActive(1)
                        .createdAt(LocalDateTime.now())
                        // Đã xóa .token(...) đi
                        .build();

                acc = accountDAO.save(acc); // Lưu DB để lấy AccountID tự tăng

                // Cấp Token sau khi đăng ký
                String jwtToken = jwtUtils.generateJwtToken(acc.getEmail(), acc.getAccountID(), "USER");
                return ResponseEntity.ok(buildResponse(acc, jwtToken));
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Google authentication failed: " + e.getMessage()));
        }
    }


    // ─── LOGIN ────────────────────────────────────────────────────────────────
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequestDTO req) {
        Optional<Account> opt = accountDAO.findByEmail(req.getEmail());
        if (opt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Email not found"));
        }

        Account acc = opt.get();

        boolean passwordOk;
        if (acc.getPassword().startsWith("$2a$") || acc.getPassword().startsWith("$2b$")) {
            passwordOk = passwordEncoder.matches(req.getPassword(), acc.getPassword());
        } else {
            passwordOk = acc.getPassword().equals(req.getPassword());
        }

        if (!passwordOk) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Incorrect password"));
        }

        // 🔥 CHỐT CHẶN 2: TÀI KHOẢN BỊ BAN
        if (acc.getIsActive() != null && acc.getIsActive() == 0) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(buildBannedResponse(acc));
        }

        // 🔥 TẠO JWT TOKEN MÀ KHÔNG CẦN CHẠM VÀO DATABASE NỮA
        String role = acc.getIsAdmin() == 1 ? "ADMIN" : "USER";
        String jwtToken = jwtUtils.generateJwtToken(acc.getEmail(), acc.getAccountID(), role);

        return ResponseEntity.ok(buildResponse(acc, jwtToken));
    }

    // ─── REGISTRATION (OTP flow) ──────────────────────────────────────────────
    @PostMapping("/send-otp")
    public ResponseEntity<?> sendOtp(@RequestBody RegisterRequestDTO req) {
        if (req.getEmail() == null || req.getUsername() == null || req.getPassword() == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "All fields are required"));
        }
        if (accountDAO.findByEmail(req.getEmail()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", "This email is already registered"));
        }
        if (accountDAO.findByUsername(req.getUsername()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", "This username is already taken"));
        }

        String otp = String.format("%06d", new Random().nextInt(1_000_000));
        otpStore.save(req.getEmail(), otp, req.getUsername(), req.getPassword());

        try {
            emailService.sendOtpEmail(req.getEmail(), req.getUsername(), otp);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Failed to send verification email. Please try again."));
        }

        return ResponseEntity.ok(Map.of("message", "Verification code sent to " + req.getEmail()));
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody OtpVerifyRequestDTO req) {
        if (!otpStore.verify(req.getEmail(), req.getOtp())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Invalid or expired verification code"));
        }

        var pending = otpStore.getPending(req.getEmail());
        if (accountDAO.findByEmail(req.getEmail()).isPresent()) {
            otpStore.remove(req.getEmail());
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", "This email is already registered"));
        }

        Account acc = Account.builder()
                .username(pending.username())
                .email(req.getEmail())
                .password(pending.password())
                .avatar("")
                .point(0)
                .isAdmin(0)
                .isPremium(0)
                .isActive(1)
                .createdAt(LocalDateTime.now())
                // Đã xóa cột token
                .build();

        acc = accountDAO.save(acc); // Lưu xuống để lấy ID
        otpStore.remove(req.getEmail());

        // Cấp JWT
        String jwtToken = jwtUtils.generateJwtToken(acc.getEmail(), acc.getAccountID(), "USER");

        return ResponseEntity.status(HttpStatus.CREATED).body(buildResponse(acc, jwtToken));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequestDTO req) {
        return sendOtp(req);
    }

    // ─── FORGOT PASSWORD ──────────────────────────────────────────────────────
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(
            @RequestBody ForgotPasswordRequestDTO req,
            HttpServletRequest httpRequest) {

        if (req.getIdentifier() == null || req.getIdentifier().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Identifier is required"));
        }

        String ip = resolveClientIp(httpRequest);
        passwordResetService.processForgotPassword(req.getIdentifier(), ip);

        return ResponseEntity.ok(Map.of(
                "message", "If an account with that email or username exists, we've sent a password reset link."
        ));
    }

    // ─── RESET PASSWORD ───────────────────────────────────────────────────────
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequestDTO req) {
        if (req.getToken() == null || req.getToken().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Token is required"));
        }
        if (req.getNewPassword() == null || req.getNewPassword().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("message", "New password is required"));
        }

        try {
            boolean ok = passwordResetService.resetPassword(req.getToken(), req.getNewPassword());
            if (!ok) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("message", "Reset link is invalid or has expired. Please request a new one."));
            }
            return ResponseEntity.ok(Map.of("message", "Password updated successfully. You can now log in."));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("message", ex.getMessage()));
        }
    }

    // ─── ME (Lấy thông tin User hiện tại) ─────────────────────────────────────
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Not authenticated"));
        }

        String token = authHeader.substring(7);

        // 🔥 THAY MÁU HOÀN TOÀN LOGIC TÌM KIẾM
        // 1. Kiểm tra chữ ký JWT
        if (!jwtUtils.validateJwtToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid or expired token"));
        }

        // 2. Bóc email từ token ra (Tốc độ ánh sáng)
        String email = jwtUtils.getEmailFromJwtToken(token);

        // 3. Truy vấn thằng vào Database bằng Email (Đã đánh Index, tìm cực nhanh)
        Optional<Account> opt = accountDAO.findByEmail(email);

        if (opt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "User not found"));
        }

        Account acc = opt.get();

        // 🔥 CHỐT CHẶN 3: LƯỚI TRỜI LỒNG LỘNG KHI CHECK ME
        if (acc.getIsActive() != null && acc.getIsActive() == 0) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(buildBannedResponse(acc));
        }

        return ResponseEntity.ok(buildResponse(acc, token));
    }

    // ─── HELPERS ─────────────────────────────────────────────────────────────
    // 🔥 Đã truyền thêm tham số jwtToken vào đây
    private AuthResponseDTO buildResponse(Account acc, String jwtToken) {
        AuthResponseDTO res = new AuthResponseDTO();
        res.setAccountID(acc.getAccountID());
        res.setUsername(acc.getUsername());
        res.setEmail(acc.getEmail());
        res.setAvatar(acc.getAvatar());
        res.setIsAdmin(acc.getIsAdmin());
        res.setIsPremium(acc.getIsPremium());
        res.setToken(jwtToken); // Nhét JWT vào đây để trả về cho Frontend
        return res;
    }

    private String resolveClientIp(HttpServletRequest req) {
        String forwarded = req.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return req.getRemoteAddr();
    }
}