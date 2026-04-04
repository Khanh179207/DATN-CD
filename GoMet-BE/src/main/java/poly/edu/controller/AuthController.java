package poly.edu.controller;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import poly.edu.dao.AccountDAO;
import poly.edu.dto.*;
import poly.edu.entity.Account;
import poly.edu.service.EmailService;
import poly.edu.service.GoogleAuthService;
import poly.edu.service.OtpStore;
import poly.edu.service.PasswordResetService;
import poly.edu.util.JwtUtils;

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

    private final AccountDAO accountDAO;
    private final EmailService emailService;
    private final OtpStore otpStore;
    private final PasswordResetService passwordResetService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final GoogleAuthService googleAuthService;
    private final JwtUtils jwtUtils;
    private final poly.edu.service.AccountService accountService;

    // Helper tạo Response khi bị khóa hoặc xóa mềm
    private Map<String, Object> buildErrorResponse(Account acc, String type) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("message", type); // ACCOUNT_BANNED hoặc ACCOUNT_DEACTIVATED
        errorResponse.put("email", acc.getEmail());
        
        if ("ACCOUNT_BANNED".equals(type)) {
            errorResponse.put("banReason", acc.getBanReason());
            errorResponse.put("bannedAt", acc.getBannedAt() != null ? acc.getBannedAt().toString() : null);
        } else {
            errorResponse.put("deletedAt", acc.getDeletedAt() != null ? acc.getDeletedAt().toString() : null);
        }
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

            GoogleIdToken.Payload payload = googleAuthService.verifyToken(idTokenString);
            String email = payload.getEmail();
            String name = (String) payload.get("name");
            String avatarUrl = (String) payload.get("picture");

            Optional<Account> opt = accountDAO.findByEmail(email);
            Account acc;

            if (opt.isPresent()) {
                acc = opt.get();
                if (acc.getIsActive() != null) {
                    if (acc.getIsActive() == -1) {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(buildErrorResponse(acc, "ACCOUNT_BANNED"));
                    }
                    if (acc.getIsActive() == 0) {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(buildErrorResponse(acc, "ACCOUNT_DEACTIVATED"));
                    }
                }
                String role = (acc.getIsAdmin() != null && acc.getIsAdmin() == 1) ? "ADMIN" : "USER";
                String jwtToken = jwtUtils.generateJwtToken(acc.getEmail(), acc.getAccountID(), role);
                return ResponseEntity.ok(buildResponse(acc, jwtToken));
            } else {
                String finalUsername = name.replaceAll("\\s+", "");
                if (accountDAO.findByUsername(finalUsername).isPresent()) {
                    finalUsername = finalUsername + "_" + new Random().nextInt(10000);
                }

                // Password ngẫu nhiên cho Google User cũng phải băm
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
                        .build();

                acc = accountDAO.save(acc);
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
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Email không tồn tại"));
        }

        Account acc = opt.get();
        boolean passwordOk;

        // Cơ chế đọ mật khẩu thông minh: Ưu tiên đọ pass đã băm, nếu không thì đọ pass thô (tài khoản cũ)
        if (acc.getPassword() != null && (acc.getPassword().startsWith("$2a$") || acc.getPassword().startsWith("$2b$"))) {
            passwordOk = passwordEncoder.matches(req.getPassword(), acc.getPassword());
        } else {
            passwordOk = req.getPassword().equals(acc.getPassword());
        }

        if (!passwordOk) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Mật khẩu không chính xác"));
        }

        if (acc.getIsActive() != null) {
            if (acc.getIsActive() == -1) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(buildErrorResponse(acc, "ACCOUNT_BANNED"));
            }
            if (acc.getIsActive() == 0) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(buildErrorResponse(acc, "ACCOUNT_DEACTIVATED"));
            }
        }

        String role = (acc.getIsAdmin() != null && acc.getIsAdmin() == 1) ? "ADMIN" : "USER";
        String jwtToken = jwtUtils.generateJwtToken(acc.getEmail(), acc.getAccountID(), role);

        return ResponseEntity.ok(buildResponse(acc, jwtToken));
    }

    // ─── REGISTRATION (OTP flow) ──────────────────────────────────────────────
    @PostMapping("/send-otp")
    public ResponseEntity<?> sendOtp(@RequestBody RegisterRequestDTO req) {
        if (req.getEmail() == null || req.getUsername() == null || req.getPassword() == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Vui lòng điền đầy đủ thông tin"));
        }
        if (accountDAO.findByEmail(req.getEmail()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", "Email này đã được đăng ký"));
        }
        if (accountDAO.findByUsername(req.getUsername()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", "Tên đăng nhập đã tồn tại"));
        }

        String otp = String.format("%06d", new Random().nextInt(1_000_000));
        otpStore.save(req.getEmail(), otp, req.getUsername(), req.getPassword());

        try {
            emailService.sendOtpEmail(req.getEmail(), req.getUsername(), otp);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Không thể gửi email OTP"));
        }

        return ResponseEntity.ok(Map.of("message", "Mã xác thực đã được gửi tới email của bạn"));
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody OtpVerifyRequestDTO req) {
        if (!otpStore.verify(req.getEmail(), req.getOtp())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Mã OTP không hợp lệ hoặc hết hạn"));
        }

        var pending = otpStore.getPending(req.getEmail());
        if (accountDAO.findByEmail(req.getEmail()).isPresent()) {
            otpStore.remove(req.getEmail());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", "Email này đã được đăng ký"));
        }

        // 🔥 QUAN TRỌNG: Băm mật khẩu từ OTP Store trước khi lưu vào Database
        String hashedPassword = passwordEncoder.encode(pending.password());

        Account acc = Account.builder()
                .username(pending.username())
                .email(req.getEmail())
                .password(hashedPassword) // Lưu pass đã băm
                .avatar("")
                .point(0)
                .isAdmin(0)
                .isPremium(0)
                .isActive(1)
                .createdAt(LocalDateTime.now())
                .build();

        acc = accountDAO.save(acc);
        otpStore.remove(req.getEmail());

        String jwtToken = jwtUtils.generateJwtToken(acc.getEmail(), acc.getAccountID(), "USER");
        return ResponseEntity.status(HttpStatus.CREATED).body(buildResponse(acc, jwtToken));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequestDTO req) {
        return sendOtp(req);
    }

    // ─── FORGOT & RESET PASSWORD ─────────────────────────────────────────────
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequestDTO req, HttpServletRequest httpRequest) {
        if (req.getIdentifier() == null || req.getIdentifier().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Vui lòng nhập Email hoặc Username"));
        }
        String ip = resolveClientIp(httpRequest);
        passwordResetService.processForgotPassword(req.getIdentifier(), ip);
        return ResponseEntity.ok(Map.of("message", "Nếu tài khoản tồn tại, một liên kết đặt lại mật khẩu đã được gửi đi."));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequestDTO req) {
        if (req.getToken() == null || req.getNewPassword() == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Dữ liệu không hợp lệ"));
        }
        try {
            boolean ok = passwordResetService.resetPassword(req.getToken(), req.getNewPassword());
            if (!ok) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Link hết hạn hoặc không hợp lệ"));
            return ResponseEntity.ok(Map.of("message", "Đặt lại mật khẩu thành công!"));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("message", ex.getMessage()));
        }
    }

    // ─── ME (Lấy thông tin User hiện tại) ─────────────────────────────────────
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Chưa đăng nhập"));
        }

        String token = authHeader.substring(7);
        if (!jwtUtils.validateJwtToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Token không hợp lệ"));
        }

        String email = jwtUtils.getEmailFromJwtToken(token);
        Optional<Account> opt = accountDAO.findByEmail(email);

        if (opt.isEmpty()) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Không tìm thấy User"));

        Account acc = opt.get();
        if (acc.getIsActive() != null) {
            if (acc.getIsActive() == -1) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(buildErrorResponse(acc, "ACCOUNT_BANNED"));
            }
            if (acc.getIsActive() == 0) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(buildErrorResponse(acc, "ACCOUNT_DEACTIVATED"));
            }
        }

        return ResponseEntity.ok(buildResponse(acc, token));
    }

    // ─── ACCOUNT RESTORATION (KHÔI PHỤC TÀI KHOẢN) ──────────────────────────────
    
    /** Bước 1: Gửi OTP khôi phục */
    @PostMapping("/send-restore-otp")
    public ResponseEntity<?> sendRestoreOtp(@RequestBody Map<String, String> req) {
        String email = req.get("email");
        try {
            accountService.sendRestoreOTP(email);
            return ResponseEntity.ok(Map.of("message", "Mã xác thực đã được gửi về Email của bạn."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    /** Bước 2: Xác nhận Password + OTP để khôi phục */
    @PostMapping("/restore-account")
    public ResponseEntity<?> restoreAccount(@RequestBody Map<String, String> req) {
        String email = req.get("email");
        String password = req.get("password");
        String otp = req.get("otp");
        
    try {
        accountService.verifyAndRestore(email, password, otp);
        
        // 🔥 Lấy lại tài khoản vừa khôi phục để tạo token đăng nhập luôn
        Account acc = accountDAO.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản sau khi khôi phục"));
        
        String role = (acc.getIsAdmin() != null && acc.getIsAdmin() == 1) ? "ADMIN" : "USER";
        String jwtToken = jwtUtils.generateJwtToken(acc.getEmail(), acc.getAccountID(), role);
        
        return ResponseEntity.ok(buildResponse(acc, jwtToken));
    } catch (Exception e) {
        return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
    }
    }

    // ─── HELPERS ─────────────────────────────────────────────────────────────
    private AuthResponseDTO buildResponse(Account acc, String jwtToken) {
        AuthResponseDTO res = new AuthResponseDTO();
        res.setAccountID(acc.getAccountID());
        res.setUsername(acc.getUsername());
        res.setEmail(acc.getEmail());
        res.setAvatar(acc.getAvatar());
        res.setIsAdmin(acc.getIsAdmin());
        res.setIsPremium(acc.getIsPremium());
        res.setToken(jwtToken);
        return res;
    }

    private String resolveClientIp(HttpServletRequest req) {
        String forwarded = req.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) return forwarded.split(",")[0].trim();
        return req.getRemoteAddr();
    }
}