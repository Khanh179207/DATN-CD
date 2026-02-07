package poly.edu.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import poly.edu.dto.*;
import poly.edu.service.AuthService;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // ================= REGISTER =================
    @PostMapping("/register")
    public ResponseEntity<ApiResponse> register(
            @RequestBody @Valid RegisterRequest req) {

        authService.register(req);

        return ResponseEntity.ok(
                new ApiResponse(true, "Vui lòng kiểm tra email để nhập OTP")
        );
    }

    // ================= VERIFY OTP =================
    @PostMapping("/verify-otp")
    public ResponseEntity<ApiResponse> verifyOtp(
            @RequestBody @Valid VerifyOtpRequest req) {

        authService.verifyOtp(req);

        return ResponseEntity.ok(
                new ApiResponse(true, "Xác thực OTP thành công")
        );
    }

    // ================= LOGIN =================
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @RequestBody @Valid LoginRequest req) {

        return ResponseEntity.ok(
                authService.login(req)
        );
    }

    // ================= LOGIN GOOGLE =================
    @PostMapping("/login/google")
    public ResponseEntity<LoginResponse> loginGoogle(
            @RequestBody @Valid GoogleLoginRequest req) {

        return ResponseEntity.ok(
                authService.loginGoogle(req.getIdToken())
        );
    }

    // ================= FORGOT-PASSWORD =================
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest req) {

        authService.forgotPassword(req);
        return ResponseEntity.ok("Đã gửi email đặt lại mật khẩu");
    }
    // ================= RESET-PASSWORD =================
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(
            @Valid @RequestBody ResetPasswordRequest req) {

        authService.resetPassword(req);
        return ResponseEntity.ok("Đặt lại mật khẩu thành công");
    }
}
