package poly.edu.service;

import poly.edu.dto.AdminAccountDTO;

import java.util.List;

public interface AccountService {
    List<AdminAccountDTO> findAll();
    AdminAccountDTO findById(Integer id);
    AdminAccountDTO save(AdminAccountDTO dto);

    // 🔥 Yêu cầu truyền thêm người khóa và lý do
    void ban(Integer id, Integer adminId, String adminName, String adminEmail, String reason);
    void unban(Integer id, Integer adminId, String adminName); // Sửa dòng này
    void delete(Integer id);
    void hardDelete(Integer id);
}