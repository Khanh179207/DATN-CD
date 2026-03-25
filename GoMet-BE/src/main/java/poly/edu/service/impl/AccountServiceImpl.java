package poly.edu.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import poly.edu.dao.AccountDAO;
import poly.edu.dto.AdminAccountDTO;
import poly.edu.entity.Account;
import poly.edu.service.AccountService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountDAO accountDAO;

    // 1. SỬA HÀM toDTO
    private AdminAccountDTO toDTO(Account acc) {
        AdminAccountDTO dto = new AdminAccountDTO();

        // Map dữ liệu cơ bản
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

        // Map thẳng từ Entity sang DTO, không cần đi tìm trong DB nữa
        dto.setBannedBy(acc.getBannedBy());
        dto.setBannedByName(acc.getBannedByName());   // Lấy thẳng
        dto.setBannedByEmail(acc.getBannedByEmail()); // Lấy thẳng
        dto.setBanReason(acc.getBanReason());
        dto.setBannedAt(acc.getBannedAt() != null ? acc.getBannedAt().toString() : null);

        // ========================================================
        // 🔥 LOGIC ĐẾM SỐ BÀI, FOLLOW VÀ TỔNG LIKE 🔥
        // ========================================================

        // 1. Đếm số bài viết
        int posts = acc.getPosts() != null ? acc.getPosts().size() : 0;
        dto.setPostCount(posts);

        // 2. Đếm số người theo dõi (Followers)
        int followers = acc.getFollowers() != null ? acc.getFollowers().size() : 0;
        dto.setFollowerCount(followers);

        // 3. Tính tổng số lượt thích từ TẤT CẢ bài viết của user này
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
        return accountDAO.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
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
            if (dto.getUsername() != null)  acc.setUsername(dto.getUsername());
            if (dto.getEmail()    != null)  acc.setEmail(dto.getEmail());
            if (dto.getAvatar()   != null)  acc.setAvatar(dto.getAvatar());
            if (dto.getIsPremium()!= null)  acc.setIsPremium(dto.getIsPremium());
            if (dto.getIsActive() != null)  acc.setIsActive(dto.getIsActive());
            if (dto.getIsAdmin()  != null)  acc.setIsAdmin(dto.getIsAdmin());
            if (dto.getRole() != null) {
                acc.setIsAdmin("ADMIN".equalsIgnoreCase(dto.getRole()) ? 1 : 0);
            }
            acc.setUpdatedAt(LocalDateTime.now());
        } else {
            acc = Account.builder()
                    .username(dto.getUsername())
                    .email(dto.getEmail())
                    .password(dto.getPassword() != null ? dto.getPassword() : "changeme")
                    .avatar(dto.getAvatar())
                    .isPremium(dto.getIsPremium() != null ? dto.getIsPremium() : 0)
                    .isActive(dto.getIsActive()  != null ? dto.getIsActive()  : 1)
                    .isAdmin(dto.getIsAdmin()    != null ? dto.getIsAdmin()   : 0)
                    .point(0)
                    .token("NONE")
                    .createdAt(LocalDateTime.now())
                    .build();
        }
        return toDTO(accountDAO.save(acc));
    }

    // 🔥 LOGIC KHÓA TÀI KHOẢN (GHI NHẬN LÝ DO & ADMIN)
    @Override
    public void ban(Integer id, Integer adminId, String adminName, String adminEmail, String reason) {
        Account acc = accountDAO.findById(id).orElseThrow();
        acc.setIsActive(0);
        acc.setBannedBy(adminId);
        acc.setBannedByName(adminName);   // Lưu tên
        acc.setBannedByEmail(adminEmail); // Lưu email
        acc.setBanReason(reason);
        acc.setBannedAt(LocalDateTime.now());
        acc.setUpdatedAt(LocalDateTime.now());
        accountDAO.save(acc);
    }

    // 🔥 LOGIC MỞ KHÓA TÀI KHOẢN (XÓA DẤU VẾT KHÓA)
    @Override
    public void unban(Integer id) {
        Account acc = accountDAO.findById(id).orElseThrow();
        acc.setIsActive(1);
        acc.setBannedBy(null);
        acc.setBannedByName(null);   // Rửa sạch
        acc.setBannedByEmail(null);  // Rửa sạch
        acc.setBanReason(null);
        acc.setBannedAt(null);
        acc.setUpdatedAt(LocalDateTime.now());
        accountDAO.save(acc);
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
}