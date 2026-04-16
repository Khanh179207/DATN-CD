package poly.edu.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import poly.edu.dao.AccountDAO;
import poly.edu.dao.FollowDAO;
import poly.edu.dao.InteractionLogDAO;
import poly.edu.dao.PostDAO;
import poly.edu.dao.CommentDAO;
import poly.edu.dao.TicketDAO;
import poly.edu.dto.UserProfileDTO;
import poly.edu.entity.Account;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final BCryptPasswordEncoder passwordEncoder;
    private final AccountDAO accountDAO;
    private final PostDAO postDAO;
    private final FollowDAO followDAO;
    private final poly.edu.service.AccountService accountService;

    private final CommentDAO commentDAO;
    private final TicketDAO ticketDAO;

    @Autowired
    private InteractionLogDAO interactionLogDAO;

    // 🔥 HÀM HELPER ĐỌ MẬT KHẨU THÔNG MINH (DÙNG CHUNG)
    private boolean isPasswordMatch(String rawPassword, String savedPassword) {
        if (rawPassword == null || savedPassword == null) return false;
        if (savedPassword.startsWith("$2a$") || savedPassword.startsWith("$2b$")) {
            return passwordEncoder.matches(rawPassword, savedPassword);
        }
        return rawPassword.equals(savedPassword);
    }

    // 🟢 PUBLIC: Xem profile
    @GetMapping("/{id}")
    public ResponseEntity<UserProfileDTO> getProfile(@PathVariable Integer id) {
        return accountDAO.findById(id).map(acc -> {
            if (acc.getIsActive() != 1) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).<UserProfileDTO>build();
            }
            UserProfileDTO dto = toDTO(acc);
            return ResponseEntity.ok(dto);
        }).orElse(ResponseEntity.notFound().build());
    }

    // 🟡 USER ONLY: Sửa đổi thông tin cơ bản
    @PreAuthorize("isAuthenticated()")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateProfile(
            @PathVariable Integer id,
            @RequestBody Map<String, String> body) {
        Account acc = accountDAO.findById(id).orElse(null);
        if (acc == null) return ResponseEntity.notFound().build();

        if (body.containsKey("username")) acc.setUsername(body.get("username"));
        if (body.containsKey("avatar")) acc.setAvatar(body.get("avatar"));
        if (body.containsKey("bio")) acc.setBio(body.get("bio"));

        accountDAO.save(acc);
        return ResponseEntity.ok(toDTO(acc));
    }

    // 🟢 PUBLIC: Lấy chỉ số
    @GetMapping("/{id}/stats")
    public ResponseEntity<?> getStats(@PathVariable Integer id) {
        Account acc = accountDAO.findById(id).orElse(null);
        if (acc == null) return ResponseEntity.notFound().build();

        long followerCount = followDAO.countActiveFollowers(id);
        long followingCount = followDAO.countActiveFollowing(id);
        long postCount = postDAO.countByAccountId(id);

        return ResponseEntity.ok(Map.of(
                "postCount", postCount,
                "followerCount", followerCount,
                "followingCount", followingCount,
                "point", acc.getPoint()
        ));
    }

    // 🟢 PUBLIC: Tìm kiếm
    @GetMapping("/search")
    public ResponseEntity<?> searchUsers(@RequestParam(required = false, defaultValue = "") String keyword) {
        List<Account> accounts;

        if (keyword.isBlank()) {
            accounts = accountDAO.findByIsActive(1);
        } else {
            accounts = accountDAO.findByUsernameContainingIgnoreCaseAndIsActive(keyword.trim(), 1);
        }

        List<UserProfileDTO> result = accounts.stream()
                .map(this::toDTO)
                .toList();

        return ResponseEntity.ok(result);
    }

    @GetMapping("/leaderboard")
    public ResponseEntity<?> getLeaderboard(
            @RequestParam(defaultValue = "all") String timeframe,
            @RequestParam(defaultValue = "10") int limit) {
        try {
            LocalDateTime startDate = calculateStartDate(timeframe);
            Pageable pageable = PageRequest.of(0, limit);
            List<Map<String, Object>> topChefs = accountDAO.findTopChefs(startDate, pageable);
            return ResponseEntity.ok(topChefs);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", "Lỗi lấy BXH Đầu Bếp: " + e.getMessage()));
        }
    }

    private UserProfileDTO toDTO(Account acc) {
        UserProfileDTO dto = new UserProfileDTO();
        dto.setAccountID(acc.getAccountID());
        dto.setUsername(acc.getUsername());
        dto.setEmail(acc.getEmail());
        dto.setAvatar(acc.getAvatar());
        dto.setPoint(acc.getPoint());
        dto.setIsPremium(acc.getIsPremium());
        dto.setIsAdmin(acc.getIsAdmin());
        dto.setBio(acc.getBio());
        dto.setCreatedAt(acc.getCreatedAt());


        String currentProvider = "local";
        if (acc.getPassword() != null && acc.getPassword().startsWith("GOOGLE_")) {
            currentProvider = "google";
        }
        dto.setProvider(currentProvider);

        long postCount = acc.getPosts() != null ? acc.getPosts().stream()
                .filter(p -> p.getIsApproved() == 1 && p.getIsActive() == 1).count() : 0;
        dto.setPostCount(postCount);

        long followerCount = followDAO.countActiveFollowers(acc.getAccountID());
        dto.setFollowerCount(followerCount);

        long followingCount = followDAO.countActiveFollowing(acc.getAccountID());
        dto.setFollowingCount(followingCount);

        long totalViews = acc.getPosts() != null ? acc.getPosts().stream()
                .mapToLong(p -> p.getViews() != null ? p.getViews() : 0).sum() : 0;
        dto.setTotalViews(totalViews);

        long totalLikes = acc.getPosts() != null ? acc.getPosts().stream()
                .filter(p -> p.getIsApproved() == 1 && p.getIsActive() == 1)
                .mapToLong(p -> p.getLikeCount() != null ? p.getLikeCount() : 0).sum() : 0;
        dto.setTotalLikes(totalLikes);

        return dto;
    }

    private LocalDateTime calculateStartDate(String timeframe) {
        if (timeframe == null) return LocalDateTime.now().minusMonths(1);
        return switch (timeframe.toLowerCase()) {
            case "day" -> LocalDateTime.now().with(java.time.LocalTime.MIN);
            case "month" -> LocalDateTime.now().minusMonths(1);
            case "year" ->
                    LocalDateTime.now().with(java.time.temporal.TemporalAdjusters.firstDayOfYear()).with(java.time.LocalTime.MIN);
            default -> LocalDateTime.now().minusMonths(1);
        };
    }

    // ─────────────────────────────────────────────────────────────────────────
    // TÍNH NĂNG XÓA MỀM TÀI KHOẢN
    // ─────────────────────────────────────────────────────────────────────────

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{id}/send-deactivate-otp")
    public ResponseEntity<?> sendDeactivateOtp(@PathVariable Integer id, @RequestBody Map<String, String> body) {
        try {
            String password = body.get("password");
            accountService.sendDeactivateOTP(id, password);
            return ResponseEntity.ok(Map.of("message", "Mã xác thực đã được gửi về Email của bạn."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{id}/deactivate")
    public ResponseEntity<?> deactivateAccount(@PathVariable Integer id, @RequestBody Map<String, String> body) {
        String password = body.get("password");
        String otp = body.get("otp");
        try {
            accountService.verifyAndDeactivate(id, password, otp);
            return ResponseEntity.ok(Map.of("message", "Tài khoản của bạn đã được xóa mềm thành công. Hẹn gặp lại!"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // 🔥 NEW: API ĐỔI MẬT KHẨU (CHANGE PASSWORD)
    // ─────────────────────────────────────────────────────────────────────────

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{id}/send-pwd-otp")
    public ResponseEntity<?> sendPasswordOtp(@PathVariable Integer id) {
        try {
            accountService.sendPasswordChangeOTP(id);
            return ResponseEntity.ok(Map.of("message", "Mã xác thực đổi mật khẩu đã được gửi về Email."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", "Lỗi gửi OTP: " + e.getMessage()));
        }
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/{id}/change-password")
    public ResponseEntity<?> changePassword(@PathVariable Integer id, @RequestBody Map<String, Object> body) {
        try {
            String oldPassword = (String) body.get("oldPassword");
            String newPassword = (String) body.get("newPassword");
            String otp = (String) body.get("otp");
            boolean isGoogleUser = body.containsKey("isGoogleUser") && (Boolean) body.get("isGoogleUser");

            Account acc = accountDAO.findById(id)
                    .orElseThrow(() -> new RuntimeException("Tài khoản không tồn tại."));

            // 1. Kiểm tra mật khẩu cũ (SỬA LẠI: Dùng hàm đọ pass thông minh)
            if (!isGoogleUser) {
                if (oldPassword == null) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Vui lòng nhập mật khẩu hiện tại."));
                }

                if (!isPasswordMatch(oldPassword, acc.getPassword())) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(Map.of("message", "Mật khẩu hiện tại không chính xác."));
                }
            }

            // 2. Xác thực mã OTP thông qua AccountService
            accountService.verifyPasswordChangeOTP(id, otp);

            // 3. Tiến hành mã hóa và lưu mật khẩu mới
            acc.setPassword(passwordEncoder.encode(newPassword));
            accountDAO.save(acc);

            return ResponseEntity.ok(Map.of("message", "Đổi mật khẩu thành công."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // LẤY DANH SÁCH BÌNH LUẬN & PHIẾU HỖ TRỢ
    // ─────────────────────────────────────────────────────────────────────────

    @GetMapping("/{id}/comments")
    public ResponseEntity<?> getUserComments(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(commentDAO.findHistoryByAccountId(id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", "Lỗi lấy bình luận: " + e.getMessage()));
        }
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}/tickets")
    public ResponseEntity<?> getUserTickets(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(ticketDAO.findHistoryByAccountId(id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", "Lỗi lấy phiếu hỗ trợ: " + e.getMessage()));
        }
    }
}