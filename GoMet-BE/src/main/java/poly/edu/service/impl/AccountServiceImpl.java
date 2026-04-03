package poly.edu.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import poly.edu.dao.AccountDAO;
import poly.edu.dao.PostDAO;
import poly.edu.dto.AdminAccountDTO;
import poly.edu.entity.Account;
import poly.edu.service.AccountService;
import poly.edu.service.EmailService;
import poly.edu.service.ModerationLogService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountDAO accountDAO;
    private final PostDAO postDAO;
    private final ModerationLogService moderationLogService;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    // Bộ nhớ tạm lưu OTP (Key: Email, Value: Code)
    private final Map<String, String> otpStore = new ConcurrentHashMap<>();

    private AdminAccountDTO toDTO(Account acc) {
        AdminAccountDTO dto = new AdminAccountDTO();
        dto.setAccountID(acc.getAccountID());
        dto.setUsername(acc.getUsername());
        dto.setEmail(acc.getEmail());
        dto.setAvatar(acc.getAvatar());
        dto.setIsAdmin(acc.getIsAdmin());
        dto.setIsPremium(acc.getIsPremium());
        dto.setIsActive(acc.getIsActive());
        dto.setPoint(acc.getPoint());
        dto.setCreatedAt(acc.getCreatedAt() != null ? acc.getCreatedAt().toString() : null);
        dto.setRole(acc.getIsAdmin() != null && acc.getIsAdmin() == 1 ? "ADMIN" : "USER");
        dto.setBanReason(acc.getBanReason());
        dto.setBannedAt(acc.getBannedAt() != null ? acc.getBannedAt().toString() : null);

        int posts = acc.getPosts() != null ? acc.getPosts().size() : 0;
        dto.setPostCount(posts);

        int followers = acc.getFollowers() != null ? acc.getFollowers().size() : 0;
        dto.setFollowerCount(followers);

        int likes = 0;
        if (acc.getPosts() != null) {
            likes = acc.getPosts().stream()
                    .mapToInt(p -> p.getLikeCount() != null ? p.getLikeCount() : 0)
                    .sum();
        }
        dto.setTotalLikes(likes);
        return dto;
    }

    @Override
    public List<AdminAccountDTO> findAll() {
        return accountDAO.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public AdminAccountDTO findById(Integer id) {
        return toDTO(accountDAO.findById(id).orElseThrow());
    }

    @Override
    public AdminAccountDTO save(AdminAccountDTO dto) {
        Account acc;
        if (dto.getAccountID() != null) {
            acc = accountDAO.findById(dto.getAccountID()).orElseThrow();
            if (dto.getUsername() != null) acc.setUsername(dto.getUsername());
            if (dto.getEmail() != null) acc.setEmail(dto.getEmail());
            if (dto.getAvatar() != null) acc.setAvatar(dto.getAvatar());
            if (dto.getIsPremium() != null) acc.setIsPremium(dto.getIsPremium());
            if (dto.getIsActive() != null) acc.setIsActive(dto.getIsActive());
            if (dto.getIsAdmin() != null) acc.setIsAdmin(dto.getIsAdmin());
            if (dto.getRole() != null) {
                acc.setIsAdmin("ADMIN".equalsIgnoreCase(dto.getRole()) ? 1 : 0);
            }
            if (dto.getPassword() != null && !dto.getPassword().trim().isEmpty()) {
                acc.setPassword(passwordEncoder.encode(dto.getPassword()));
            }
            acc.setUpdatedAt(LocalDateTime.now());
        } else {
            acc = Account.builder()
                    .username(dto.getUsername())
                    .email(dto.getEmail())
                    .password(passwordEncoder.encode(dto.getPassword() != null ? dto.getPassword() : "changeme"))
                    .avatar(dto.getAvatar())
                    .isPremium(dto.getIsPremium() != null ? dto.getIsPremium() : 0)
                    .isActive(dto.getIsActive() != null ? dto.getIsActive() : 1)
                    .isAdmin(dto.getIsAdmin() != null ? dto.getIsAdmin() : 0)
                    .point(0)
                    .createdAt(LocalDateTime.now())
                    .build();
        }
        return toDTO(accountDAO.save(acc));
    }

    @Override
    public void ban(Integer id, Integer adminId, String adminName, String adminEmail, String reason) {
        Account acc = accountDAO.findById(id).orElseThrow();
        acc.setIsActive(-1); // -1 là BỊ BAN
        acc.setBanReason(reason);
        acc.setBannedAt(LocalDateTime.now());
        acc.setUpdatedAt(LocalDateTime.now());
        accountDAO.save(acc);
        moderationLogService.logAction(id, "ACCOUNT", "BAN", adminId, adminName, reason);
    }

    @Override
    public void unban(Integer id, Integer adminId, String adminName) {
        Account acc = accountDAO.findById(id).orElseThrow();
        acc.setIsActive(1);
        acc.setBanReason(null);
        acc.setBannedAt(null);
        acc.setUpdatedAt(LocalDateTime.now());
        accountDAO.save(acc);
        moderationLogService.logAction(id, "ACCOUNT", "UNBAN", adminId, adminName, "Đã ân xá, gỡ lệnh cấm");
    }

    @Override
    public void delete(Integer id) {
        Account acc = accountDAO.findById(id).orElseThrow();
        acc.setIsActive(0);
        acc.setDeletedAt(LocalDateTime.now());
        accountDAO.save(acc);
    }

    @Override
    public void hardDelete(Integer id) {
        accountDAO.deleteById(id);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // NEW: Quản lý Xóa mềm & Khôi phục (Self-Service)
    // ─────────────────────────────────────────────────────────────────────────

    private String generateOtp() {
        return String.format("%06d", new Random().nextInt(1000000));
    }

    @Override
    public void sendDeactivateOTP(Integer accountId) {
        Account acc = accountDAO.findById(accountId).orElseThrow();
        String otp = generateOtp();
        otpStore.put(acc.getEmail(), otp);
        emailService.sendOtpEmail(acc.getEmail(), acc.getUsername(), otp);
    }

    @Override
    public void verifyAndDeactivate(Integer accountId, String password, String otp) {
        Account acc = accountDAO.findById(accountId).orElseThrow();
        
        // 🛡️ BỎ YÊU CẦU MẬT KHẨU THEO YÊU CẦU NHÓM TRƯỞNG (OTP LÀ LỚP BẢO VỆ DUY NHẤT)
        // if (password != null && !password.trim().isEmpty()) {
        //     if (!passwordEncoder.matches(password, acc.getPassword())) {
        //         throw new RuntimeException("Mật khẩu không chính xác!");
        //     }
        // }

        String savedOtp = otpStore.get(acc.getEmail());
        if (savedOtp == null || !savedOtp.equals(otp)) {
            throw new RuntimeException("Mã OTP không chính xác hoặc đã hết hạn!");
        }
        acc.setIsActive(0);
        acc.setDeletedAt(LocalDateTime.now());
        accountDAO.save(acc);

        // 🛡️ TỰ ĐỘNG ẨN TOÀN BỘ BÀI VIẾT CỦA NGƯỜI NÀY ĐỂ BẢO VỆ RIÊNG TƯ
        postDAO.deactivateAllPostsByAccountId(accountId);

        otpStore.remove(acc.getEmail());
    }

    @Override
    public void sendRestoreOTP(String email) {
        Account acc = accountDAO.findByEmail(email).orElse(null);
        if (acc == null) throw new RuntimeException("Không tìm thấy tài khoản với Email này!");
        if (acc.getIsActive() != 0) throw new RuntimeException("Tài khoản này không ở trạng thái cần khôi phục!");

        String otp = generateOtp();
        otpStore.put(email, otp);
        emailService.sendOtpEmail(email, acc.getUsername(), otp);
    }

    @Override
    public void verifyAndRestore(String email, String password, String otp) {
        Account acc = accountDAO.findByEmail(email).orElse(null);
        if (acc == null) throw new RuntimeException("Tài khoản không tồn tại!");
        
        // 🛡️ BỎ YÊU CẦU MẬT KHẨU THEO YÊU CẦU NHÓM TRƯỞNG (OTP LÀ LỚP BẢO VỆ DUY NHẤT)
        // if (password != null && !password.trim().isEmpty()) {
        //     if (!passwordEncoder.matches(password, acc.getPassword())) {
        //         throw new RuntimeException("Mật khẩu không chính xác!");
        //     }
        // }

        String savedOtp = otpStore.get(email);
        if (savedOtp == null || !savedOtp.equals(otp)) {
            throw new RuntimeException("Mã OTP không chính xác hoặc đã hết hạn!");
        }
        acc.setIsActive(1);
        acc.setDeletedAt(null);
        accountDAO.save(acc);
        otpStore.remove(email);
    }
}