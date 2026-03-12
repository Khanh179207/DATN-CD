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

import java.time.LocalDateTime; // ĐÃ ĐỔI TỪ LocalDate SANG LocalDateTime
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

    // VŨ KHÍ MỚI: Inject GoogleAuthService
    private final GoogleAuthService    googleAuthService;

    // ─── GOOGLE LOGIN & REGISTER ──────────────────────────────────────────────
    @PostMapping("/google")
    public ResponseEntity<?> googleLogin(@RequestBody Map<String, String> request) {
        try {
            // FIX LỖI: Đổi "idToken" thành "token" để khớp với data từ Frontend gửi xuống
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
                // TÌNH HUỐNG 1: ĐÃ CÓ TÀI KHOẢN -> ĐĂNG NHẬP
                acc = opt.get();
                if (acc.getIsActive() == 0) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN)
                            .body(Map.of("message", "ACCOUNT_BANNED"));
                }
            } else {
                // TÌNH HUỐNG 2: CHƯA CÓ TÀI KHOẢN -> TỰ ĐỘNG ĐĂNG KÝ

                // Xử lý trùng lặp Username (nếu tên từ Google bị trùng với user khác)
                String finalUsername = name.replaceAll("\\s+", ""); // Xóa khoảng trắng
                if (accountDAO.findByUsername(finalUsername).isPresent()) {
                    finalUsername = finalUsername + "_" + new Random().nextInt(10000);
                }

                // Mã hóa một chuỗi ngẫu nhiên làm mật khẩu để bảo vệ DB
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
                        .createdAt(LocalDateTime.now()) // ĐÃ SỬA THÀNH LocalDateTime.now()
                        .build();
            }

            // 3. Tạo Token phiên đăng nhập (Giống hệt luồng Login bình thường của sếp)
            String newToken = UUID.randomUUID().toString();
            acc.setToken(newToken);
            accountDAO.save(acc);

            // 4. Trả về Frontend thông qua buildResponse chuẩn form
            return ResponseEntity.ok(buildResponse(acc));

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

        // BCrypt-aware password check with plain-text fallback for legacy accounts.
        // After a password reset the hash starts with "$2a$"; legacy accounts are plain text.
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
        if (acc.getIsActive() == 0) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "ACCOUNT_BANNED"));
        }

        String newToken = UUID.randomUUID().toString();
        acc.setToken(newToken);
        accountDAO.save(acc);

        return ResponseEntity.ok(buildResponse(acc));
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

        String token = UUID.randomUUID().toString();
        Account acc = Account.builder()
                .username(pending.username())
                .email(req.getEmail())
                // Sếp lưu ý: Nếu muốn an toàn, sếp nên dùng passwordEncoder.encode(pending.password()) ở đây nhé!
                .password(pending.password())
                .avatar("")
                .token(token)
                .point(0)
                .isAdmin(0)
                .isPremium(0)
                .isActive(1)
                .createdAt(LocalDateTime.now()) // ĐÃ SỬA THÀNH LocalDateTime.now()
                .build();

        accountDAO.save(acc);
        otpStore.remove(req.getEmail());

        return ResponseEntity.status(HttpStatus.CREATED).body(buildResponse(acc));
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

    // ─── ME ───────────────────────────────────────────────────────────────────
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Not authenticated"));
        }
        String token = authHeader.substring(7);
        Optional<Account> opt = accountDAO.findAll().stream()
                .filter(a -> token.equals(a.getToken()))
                .findFirst();
        if (opt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid token"));
        }
        return ResponseEntity.ok(buildResponse(opt.get()));
    }

    // ─── HELPERS ─────────────────────────────────────────────────────────────
    private AuthResponseDTO buildResponse(Account acc) {
        AuthResponseDTO res = new AuthResponseDTO();
        res.setAccountID(acc.getAccountID());
        res.setUsername(acc.getUsername());
        res.setEmail(acc.getEmail());
        res.setAvatar(acc.getAvatar());
        res.setIsAdmin(acc.getIsAdmin());
        res.setIsPremium(acc.getIsPremium());
        res.setToken(acc.getToken());
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