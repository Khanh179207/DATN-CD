package poly.edu.service;

import poly.edu.dto.AdminNotificationDTO;

public interface AdminNotificationService {
    void sendToAll(AdminNotificationDTO dto);
    void sendToOne(Integer accountID, AdminNotificationDTO dto);
    void delete(Integer id);
}

