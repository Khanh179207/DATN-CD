package poly.edu.service;

import poly.edu.dto.AppealDTO;
import poly.edu.entity.Appeal;

import java.util.List;
import java.util.Optional;

public interface AppealService {
    /**
     * Create a new appeal
     */
    AppealDTO createAppeal(String email, String reason, String ipAddress);

    /**
     * Get appeal by ID
     */
    Optional<AppealDTO> getAppealById(Integer appealID);

    /**
     * Get all appeals (admin only)
     */
    List<AppealDTO> getAllAppeals();

    /**
     * Get appeals by status
     */
    List<AppealDTO> getAppealsByStatus(String status);

    /**
     * Update appeal status and note
     */
    AppealDTO updateAppeal(Integer appealID, String status, String note);

    /**
     * Unban account by appeal
     */
    boolean unbanAccountByAppeal(Integer appealID);

    /**
     * Get appeal status by email (user-facing)
     */
    Optional<AppealDTO> getAppealStatusByEmail(String email);

    /**
     * Check rate limiting - 1 appeal per email per 24h
     */
    boolean canCreateAppeal(String email);
}
