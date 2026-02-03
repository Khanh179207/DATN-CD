package poly.edu.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import poly.edu.dao.AccountDAO;
import poly.edu.dto.RegisterRequest;
import poly.edu.entity.Account;

import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AccountDAO accountDAO;
    private final PasswordEncoder passwordEncoder;

    public void register(RegisterRequest req) {

        // 1. Check confirm password
        if (!req.getPassword().equals(req.getConfirmPassword())) {
            throw new RuntimeException("Mật khẩu xác nhận không khớp");
        }

        // 2. Check email tồn tại
        if (accountDAO.findByEmail(req.getEmail()).isPresent()) {
            throw new RuntimeException("Email đã tồn tại");
        }

        // 3. Tạo account mới
        Account acc = new Account();
        acc.setUsername(req.getUsername());
        acc.setEmail(req.getEmail());
        acc.setPassword(passwordEncoder.encode(req.getPassword()));

        acc.setAvatar(null);

        // ✅ QUAN TRỌNG: token KHÔNG được null
        acc.setToken("REGISTER_" + UUID.randomUUID());

        acc.setPoint(0);
        acc.setIsAdmin(0);
        acc.setIsPremium(0);
        acc.setIsActive(1);
        acc.setCreatedAt(LocalDate.now());

        accountDAO.save(acc);
    }
}
