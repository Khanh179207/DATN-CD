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
}
