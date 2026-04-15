package poly.edu.service.impl;

import poly.edu.dao.AppealDAO;
import poly.edu.dao.AccountDAO;
import poly.edu.dto.AppealDTO;
import poly.edu.entity.Appeal;
import poly.edu.entity.Account;
import poly.edu.service.AppealService;
import poly.edu.service.NotificationService;
import poly.edu.service.AccountService;
import poly.edu.service.EmailService;
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
    @Autowired
    private EmailService emailService;

    @Override
    public AppealDTO createAppeal(String email, String reason) {
        String trimEmail = email.trim();

        Account account = accountDAO.findByEmail(trimEmail)
                .orElseThrow(() -> new RuntimeException("Tài khoản không tồn tại trong hệ thống."));

        if (account.getIsActive() != -1) {
            throw new RuntimeException(
                    "Tài khoản của bạn đang hoạt động bình thường, vui lòng dùng tính năng phiếu hỗ trợ trong web để được hỗ trợ!");
        }

        if (!canCreateAppeal(trimEmail)) {
            throw new RuntimeException("Bạn chỉ có thể nộp 1 khiếu nại mỗi 24 giờ.");
        }

        Appeal appeal = new Appeal(trimEmail, reason, account);
        Appeal saved = appealDAO.save(appeal);

        try {
            List<Account> admins = accountDAO.findByIsAdmin(1);
            for (Account admin : admins) {
                notificationService.createNotification(
                        "Khiếu nại mới",
                        "Người dùng " + trimEmail + " đã nộp khiếu nại: " + reason,
                        "appeal",
                        admin.getAccountID(),
                        null, // actorId - null for system notification
                        null, // postId
                        "/admin/appeals");
            }
        } catch (Exception e) {
            System.err.println("Error notifying admins: " + e.getMessage());
        }

        return convertToDTO(saved);
    }

    @Override
    public Optional<AppealDTO> getAppealById(Integer appealID) {
        return appealDAO.findById(appealID).map(this::convertToDTO);
    }

    @Override
    public List<AppealDTO> getAllAppeals() {
        return appealDAO.findAllOrderByCreatedDesc().stream()
                .map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    public List<AppealDTO> getAppealsByStatus(String status) {
        return appealDAO.findByStatus(status).stream()
                .map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    public AppealDTO updateAppeal(Integer appealID, String status, String note, Integer adminId, String adminName) {
        Appeal appeal = appealDAO.findById(appealID)
                .orElseThrow(() -> new RuntimeException("Khiếu nại không tồn tại"));

        if ("Rejected".equalsIgnoreCase(status) && (note == null || note.trim().isEmpty())) {
            throw new RuntimeException("Vui lòng nhập lý do từ chối (Note).");
        }

        appeal.setStatus(status);
        appeal.setNote(note);
        appeal.setResolvedAt(LocalDateTime.now());

        // 🔥 Lưu Admin vào phiếu
        appeal.setAdminId(adminId);
        appeal.setAdminName(adminName);

        Appeal updated = appealDAO.save(appeal);

        try {
            // Đã cập nhật truyền thêm biến "note"
            emailService.sendAppealDecisionEmail(appeal.getEmail(), status, note);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return convertToDTO(updated);
    }

    @Override
    public boolean unbanAccountByAppeal(Integer appealID, Integer adminId, String adminName) {
        Appeal appeal = appealDAO.findById(appealID)
                .orElseThrow(() -> new RuntimeException("Khiếu nại không tồn tại"));

        if (appeal.getAccount() == null) {
            throw new RuntimeException("Đơn khiếu nại này không liên kết với tài khoản nào.");
        }

        Account account = appeal.getAccount();

        // ⚠️ Tạm thời vẫn gọi hàm này để kích hoạt thuộc tính isActive = 1 cho user
        accountService.unban(account.getAccountID(), adminId, adminName);

        appeal.setStatus("Resolved");
        appeal.setResolvedAt(LocalDateTime.now());

        // 🔥 Lưu Admin vào phiếu
        appeal.setAdminId(adminId);
        appeal.setAdminName(adminName);

        appealDAO.save(appeal);

        try {
            // Khi gỡ ban, truyền thêm 1 câu nhắc nhở hoặc truyền appeal.getNote()
            String welcomeBackNote = "Chào mừng bạn quay trở lại với GoMet!";
            emailService.sendAppealDecisionEmail(appeal.getEmail(), "Approved", welcomeBackNote);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public Optional<AppealDTO> getAppealStatusByEmail(String email) {
        return appealDAO.findByEmail(email).map(this::convertToDTO);
    }

    @Override
    public boolean canCreateAppeal(String email) {
        LocalDateTime yesterday = LocalDateTime.now().minusHours(24);
        long recentAppeals = appealDAO.countRecentAppeals(email, yesterday);
        return recentAppeals == 0;
    }

    private AppealDTO convertToDTO(Appeal appeal) {
        AppealDTO dto = new AppealDTO();
        dto.setAppealID(appeal.getAppealID());
        dto.setEmail(appeal.getEmail());
        if (appeal.getAccount() != null)
            dto.setAccountID(appeal.getAccount().getAccountID());
        dto.setReason(appeal.getReason());
        dto.setStatus(appeal.getStatus());
        dto.setNote(appeal.getNote());
        dto.setCreatedAt(appeal.getCreatedAt());
        dto.setResolvedAt(appeal.getResolvedAt());

        // 🔥 Đẩy Admin lên DTO cho Frontend
        dto.setAdminId(appeal.getAdminId());
        dto.setAdminName(appeal.getAdminName());

        return dto;
    }
}