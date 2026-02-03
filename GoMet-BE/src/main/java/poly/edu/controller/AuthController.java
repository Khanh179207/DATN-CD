package poly.edu.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import poly.edu.dto.RegisterRequest;
import poly.edu.dto.ApiResponse;
import poly.edu.service.AuthService;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> register(
            @RequestBody @Valid RegisterRequest req) {

        authService.register(req);
        return ResponseEntity.ok(
                new ApiResponse(true, "Đăng ký thành công")
        );
    }
}

