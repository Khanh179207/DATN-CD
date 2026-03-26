package poly.edu.service.impl;

import poly.edu.dao.AppealDAO;
import poly.edu.dao.AccountDAO;
import poly.edu.dto.AppealDTO;
import poly.edu.entity.Appeal;
import poly.edu.entity.Account;
import poly.edu.service.AppealService;
import poly.edu.service.NotificationService;
import poly.edu.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class AppealServiceImpl implements AppealService {

    @Autowired
    private AppealDAO appealDAO;

    @Autowired
    private AccountDAO accountDAO;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private AccountService accountService;

    @Override
    public AppealDTO createAppeal(String email, String reason, String ipAddress) {
        // Check rate limiting
        if (!canCreateAppeal(email)) {
            throw new RuntimeException("Bạn chỉ có thể nộp 1 khiếu nại mỗi 24 giờ");
        }

        // Create new appeal
        Appeal appeal = new Appeal(email, reason, ipAddress);
        appeal.setStatus("Pending");
        appeal.setCreatedAt(LocalDateTime.now());

        Appeal saved = appealDAO.save(appeal);

        // Notify all admins about new appeal
        try {
            List<Account> admins = accountDAO.findByIsAdmin(1);
            for (Account admin : admins) {
                notificationService.createNotification(
                        "Khiếu nại mới",
                        "Người dùng " + email + " đã nộp khiếu nại: " + reason,
                        "appeal",
                        admin.getAccountID(),
                        null, // actorId - null for system notification
                        null, // postId
                        "/admin/appeals");
            }
        } catch (Exception e) {
            // Log error but don't fail the appeal creation
            System.err.println("Error notifying admins about appeal: " + e.getMessage());
            e.printStackTrace();
        }

        return convertToDTO(saved);
    }

    @Override
    public Optional<AppealDTO> getAppealById(Integer appealID) {
        return appealDAO.findById(appealID)
                .map(this::convertToDTO);
    }

    @Override
    public List<AppealDTO> getAllAppeals() {
        return appealDAO.findAllOrderByCreatedDesc()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<AppealDTO> getAppealsByStatus(String status) {
        return appealDAO.findByStatus(status)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public AppealDTO updateAppeal(Integer appealID, String status, String note) {
        Appeal appeal = appealDAO.findById(appealID)
                .orElseThrow(() -> new RuntimeException("Khiếu nại không tồn tại"));

        appeal.setStatus(status);
        appeal.setNote(note);
        appeal.setUpdatedAt(LocalDateTime.now());

        Appeal updated = appealDAO.save(appeal);
        return convertToDTO(updated);
    }

    @Override
    public boolean unbanAccountByAppeal(Integer appealID) {
        Appeal appeal = appealDAO.findById(appealID)
                .orElseThrow(() -> new RuntimeException("Khiếu nại không tồn tại"));

        // Find account by email
        Account account = accountDAO.findByEmail(appeal.getEmail())
                .orElseThrow(() -> new RuntimeException("Tài khoản không tồn tại"));

        // 🔥 Gọi hàm unban 3 tham số.
        // Vì đây là mở khóa tự động qua đơn Appeal, sếp có thể hardcode số 0 và "Hệ Thống"
        accountService.unban(account.getAccountID(), 0, "Hệ Thống (Đơn Khiếu Nại)");

        // Update appeal status to Resolved
        appeal.setStatus("Resolved");
        appeal.setUpdatedAt(LocalDateTime.now());
        appealDAO.save(appeal);

        return true;
    }
    @Override
    public Optional<AppealDTO> getAppealStatusByEmail(String email) {
        return appealDAO.findByEmail(email)
                .map(this::convertToDTO);
    }

    @Override
    public boolean canCreateAppeal(String email) {
        long recentAppeals = appealDAO.countRecentAppeals(email);
        return recentAppeals == 0; // Only 1 appeal per 24h
    }

    private AppealDTO convertToDTO(Appeal appeal) {
        AppealDTO dto = new AppealDTO();
        dto.setAppealID(appeal.getAppealID());
        dto.setEmail(appeal.getEmail());
        dto.setReason(appeal.getReason());
        dto.setStatus(appeal.getStatus());
        dto.setNote(appeal.getNote());
        dto.setCreatedAt(appeal.getCreatedAt());
        dto.setUpdatedAt(appeal.getUpdatedAt());
        dto.setIpAddress(appeal.getIpAddress());
        return dto;
    }
}
