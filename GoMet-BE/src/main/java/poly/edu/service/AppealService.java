package poly.edu.service;

import poly.edu.dto.AppealDTO;
import java.util.List;
import java.util.Optional;

public interface AppealService {
    AppealDTO createAppeal(String email, String reason);
    Optional<AppealDTO> getAppealById(Integer appealID);
    List<AppealDTO> getAllAppeals();
    List<AppealDTO> getAppealsByStatus(String status);

    // 🔥 Cập nhật tham số adminName
    AppealDTO updateAppeal(Integer appealID, String status, String note, Integer adminId, String adminName);
    boolean unbanAccountByAppeal(Integer appealID, Integer adminId, String adminName);

    Optional<AppealDTO> getAppealStatusByEmail(String email);
    boolean canCreateAppeal(String email);
}